package com.example.receptomat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import database.ApiService
import database.BasicResponse
import database.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("SpellCheckingInspection")
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val txtHasAccount = findViewById<TextView>(R.id.txtHasAccount)

        btnRegister.setOnClickListener {
            val txtName = findViewById<EditText>(R.id.txtName)
            val txtUsername = findViewById<EditText>(R.id.txtUsername)
            val txtEmail = findViewById<EditText>(R.id.txtEmail)
            val txtPassword = findViewById<EditText>(R.id.txtPassword)
            val txtConfirmPassword = findViewById<EditText>(R.id.txtConfirmPassword)

            if (txtName.text.toString().isEmpty() || txtUsername.text.toString().isEmpty() || txtEmail.text.toString().isEmpty() || txtPassword.text.toString().isEmpty() || txtConfirmPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Molimo ispunite sva polja", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (txtPassword.text.toString() != txtConfirmPassword.text.toString()) {
                Toast.makeText(this, "Lozinka i potvrda lozinke se ne podudaraju", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            val registrationRequest = ApiService.RegisterRequest(
                name = txtName.text.toString(),
                username = txtUsername.text.toString(),
                email = txtEmail.text.toString(),
                password = txtPassword.text.toString()
            )
            val call = apiService.registerUser(registrationRequest)

            call.enqueue(object : Callback<BasicResponse> {
                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.success == true) {
                            Toast.makeText(this@RegisterActivity, "Registracija uspješna", Toast.LENGTH_SHORT).show()
                            txtName.text.clear()
                            txtUsername.text.clear()
                            txtEmail.text.clear()
                            txtPassword.text.clear()
                            txtConfirmPassword.text.clear()
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@RegisterActivity, registerResponse?.error ?: "Registracija neuspješna", Toast.LENGTH_SHORT).show()
                            Log.e("RegisterActivity", "Registracija neuspješna: ${registerResponse?.error}")
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Serverska greška", Toast.LENGTH_SHORT).show()
                        Log.e("RegisterActivity", "Serverska greška: ${response.code()} - ${response.message()} ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Greška sa mrežom: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("RegisterActivity mreža: ", "Poruka: ${t.message}")
                }
            })
        }

        btnCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        txtHasAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
