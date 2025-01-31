package com.example.receptomat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import database.ApiService
import database.LoginResponse
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.TotpMultiFactorGenerator
import database.UserProfileResponse

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val prijavaButton = findViewById<Button>(R.id.login_Button)
        val odustaniButton = findViewById<Button>(R.id.cancel_Button)
        val etUsername = findViewById<EditText>(R.id.usernameEditText)
        val etPassword = findViewById<EditText>(R.id.passwordEditText)
        val txtDontHaveAccount = findViewById<TextView>(R.id.txtDontHaveAccount)

        txtDontHaveAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        odustaniButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        prijavaButton.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Molimo ispunite sva polja", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }

    private fun loginUser(username: String, password: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.loginUser(username, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.success == true) {
                        val userId = loginResponse.user_id ?: 0
                        saveUserId(userId)
                        Log.d("LoginActivity", "User ID set to: $userId")

                        fetchUserEmailAndSignIn(userId, password)
                    } else {
                        Toast.makeText(this@LoginActivity, loginResponse?.error ?: "Pogrešno korisničko ime ili lozinka", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Greška na serveru", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserEmailAndSignIn(userId: Int, password: String) {
        Log.d("LoginActivity", "Pokrenuto dohvaćanje emaila za user_id: $userId")

        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.getUserProfile(userId)

        call.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                Log.d("LoginActivity", "API response code: ${response.code()}")

                if (response.isSuccessful) {
                    val userProfile = response.body()

                    if (userProfile?.success == true) {
                        val email = userProfile.email
                        Log.d("LoginActivity", "Dohvaćen email: $email")

                        firebaseSignIn(email, password)
                    } else {
                        Toast.makeText(this@LoginActivity, "Neuspjelo dohvaćanje emaila", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Greška pri dohvaćanju emaila", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Greška: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun firebaseSignIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = auth.currentUser
                if (user != null && !user.isEmailVerified) {
                    sendVerificationEmail(user)
                } else {
                    Log.d("MFA", "Prijava uspješna, nema MFA")
                    Toast.makeText(this, "Prijava uspješna!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, RecipesActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthMultiFactorException) {
                    startMfaSignIn(exception)
                } else {
                    Toast.makeText(this, "Greška pri prijavi", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendVerificationEmail(user: com.google.firebase.auth.FirebaseUser) {
        user.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Verifikacijski email poslan. Provjerite svoj inbox.", Toast.LENGTH_LONG
                ).show()
                auth.signOut()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Greška pri slanju verifikacijskog emaila", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startMfaSignIn(exception: FirebaseAuthMultiFactorException) {
        val otpInputDialog = EditText(this)
        otpInputDialog.hint = "Unesi TOTP kod"

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Unesi TOTP kod")
            .setView(otpInputDialog)
            .setPositiveButton("Potvrdi") { _, _ ->
                val otpCode = otpInputDialog.text.toString()
                if (otpCode.isNotEmpty()) {
                    verifyMfaCode(exception, otpCode)
                } else {
                    Toast.makeText(this, "Molimo unesite kod", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Odustani", null)
            .create()

        alertDialog.show()
    }

    private fun verifyMfaCode(exception: FirebaseAuthMultiFactorException, otpCode: String) {
        val assertion = TotpMultiFactorGenerator.getAssertionForSignIn(
            exception.resolver.hints.first().uid,
            otpCode
        )

        exception.resolver.resolveSignIn(assertion)
            .addOnSuccessListener {
                Log.d("MFA", "Prijava uspješna sa MFA")
                Toast.makeText(this, "Prijava uspješna!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, RecipesActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Neispravan TOTP kod", Toast.LENGTH_SHORT).show()
            }
    }
}
