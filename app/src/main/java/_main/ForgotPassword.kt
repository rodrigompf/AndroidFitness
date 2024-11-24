package _main

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password)

        // Back Arrow Click Listener
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            // Navigate back to LoginActivity (replace with your login activity class)
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }
    }
}
