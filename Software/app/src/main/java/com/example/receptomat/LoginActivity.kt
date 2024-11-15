package com.example.receptomat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import database.BazaKorisnika

class LoginActivity : AppCompatActivity() {

    private val bazaKorisnika = BazaKorisnika()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val loginActivity = findViewById<ConstraintLayout>(R.id.loginActivity)
        val prijavaButton = findViewById<Button>(R.id.login_Button)
        val odustaniButton = findViewById<Button>(R.id.cancel_Button)
        val etUsername = findViewById<EditText>(R.id.usernameEditText)
        val etPassword = findViewById<EditText>(R.id.passwordEditText)

        prijavaButton.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (bazaKorisnika.provjeriKorisnika(username, password)) {
                val intent = Intent(this, RecipesActivity::class.java)
                startActivity(intent)
            } else {
                etUsername.setTextColor(Color.RED)
                etPassword.setTextColor(Color.RED)
                Toast.makeText(this, "Podaci nisu točni", Toast.LENGTH_SHORT).show()
            }
        }

        odustaniButton.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(loginActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}