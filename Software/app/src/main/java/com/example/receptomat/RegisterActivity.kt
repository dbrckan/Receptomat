package com.example.receptomat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val txtHasAccount = findViewById<TextView>(R.id.txtHasAccount)

        btnRegister.setOnClickListener {
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }

        btnCancel.setOnClickListener {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
        }

        txtHasAccount.setOnClickListener {
            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }

        var txtName = findViewById<EditText>(R.id.txtName)
        var txtUsername = findViewById<EditText>(R.id.txtUsername)
        var txtEmail = findViewById<EditText>(R.id.txtEmail)
        var txtPassword = findViewById<EditText>(R.id.txtPassword)
        var txtConfirmPassword = findViewById<EditText>(R.id.txtConfirmPassword)

        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}