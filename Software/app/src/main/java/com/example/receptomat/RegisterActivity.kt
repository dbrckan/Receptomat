package com.example.receptomat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import database.BazaKorisnika

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

        val userDatabase = BazaKorisnika()
        val result = userDatabase.dodajKorisnika(txtName.text.toString(), txtUsername.text.toString(), txtEmail.text.toString(), txtPassword.text.toString(), txtConfirmPassword.text.toString())

        if(result == "Korisnik uspješno dodan"){
            Toast.makeText(this, "Korisnik uspješno dodan", Toast.LENGTH_SHORT).show()
            txtName.text.clear()
            txtUsername.text.clear()
            txtEmail.text.clear()
            txtPassword.text.clear()
            txtConfirmPassword.text.clear()

            val intent = Intent(baseContext, LoginActivity::class.java)
            startActivity(intent)
        }
        else if(result == "Korisnik već postoji u bazi"){
            Toast.makeText(this, "Korisnik već postoji u bazi", Toast.LENGTH_SHORT).show()
        }
        else if(result == "Korisničko ime nije ispravno"){
            Toast.makeText(this, "Korisničko ime nije ispravno", Toast.LENGTH_SHORT).show()
            txtUsername.setBackgroundColor(Color.RED)
        }
        else if(result == "Email nije ispravan"){
            Toast.makeText(this, "Email nije ispravan", Toast.LENGTH_SHORT).show()
            txtEmail.setBackgroundColor(Color.RED)
        }
        else if(result == "Lozinke se ne podudaraju"){
            Toast.makeText(this, "Lozinke se ne podudaraju", Toast.LENGTH_SHORT).show()
            txtPassword.setBackgroundColor(Color.RED)
            txtConfirmPassword.setBackgroundColor(Color.RED)
        }


        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}