// LoginActivity.kt
package com.example.receptomat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import database.ApiService
import database.LoginResponse
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



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
                        saveUserId(loginResponse.user_id ?: 0)
                        Log.d("LoginActivity", "User ID set to: ${loginResponse.user_id}")
                        Toast.makeText(this@LoginActivity, "Prijava uspješna", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, RecipesActivity::class.java)
                        startActivity(intent)
                        finish()
                } else {
                        Toast.makeText(this@LoginActivity, loginResponse?.error ?:
                        "Pogrešno korisničko ime ili lozinka", Toast.LENGTH_SHORT).show()
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
}