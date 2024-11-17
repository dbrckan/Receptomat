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

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prijavaButton = findViewById<Button>(R.id.login_Button)
        val odustaniButton = findViewById<Button>(R.id.cancel_Button)
        val etUsername = findViewById<EditText>(R.id.usernameEditText)
        val etPassword = findViewById<EditText>(R.id.passwordEditText)
        val txtDontHaveAccount = findViewById<TextView>(R.id.txtDontHaveAccount)

        prijavaButton.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (BazaKorisnika.provjeriKorisnika(username, password)) {
                val intent = Intent(this, RecipesActivity::class.java)
                startActivity(intent)
            } else {
                etUsername.setTextColor(Color.RED)
                etPassword.setTextColor(Color.RED)
                Toast.makeText(this, "Podaci nisu točni", Toast.LENGTH_SHORT).show()
            }
        }

        txtDontHaveAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        odustaniButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
