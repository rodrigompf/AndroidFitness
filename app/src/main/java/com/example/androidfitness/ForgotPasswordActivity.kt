package com.example.androidfitness

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Back Arrow Click Listener
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            // Navigate back to LoginActivity (replace with your login activity class)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }
}
