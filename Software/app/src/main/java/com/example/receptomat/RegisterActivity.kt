package com.example.receptomat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import database.BazaKorisnika

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

            val result = BazaKorisnika.dodajKorisnika(
                txtName.text.toString(),
                txtUsername.text.toString(),
                txtEmail.text.toString(),
                txtPassword.text.toString(),
                txtConfirmPassword.text.toString()
            )

            when (result) {
                "Korisnik uspješno dodan" -> {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    txtName.text.clear()
                    txtUsername.text.clear()
                    txtEmail.text.clear()
                    txtPassword.text.clear()
                    txtConfirmPassword.text.clear()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                "Korisnik već postoji u bazi" -> Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                "Korisničko ime nije ispravno" -> {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    txtUsername.setTextColor(Color.RED)
                }
                "Email nije ispravan" -> {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    txtEmail.setTextColor(Color.RED)
                }
                "Lozinke se ne podudaraju" -> {
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                    txtPassword.setTextColor(Color.RED)
                    txtConfirmPassword.setTextColor(Color.RED)
                }
            }
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
