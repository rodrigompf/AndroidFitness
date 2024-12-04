package _main

import _homeScreen.HomeScreen
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.androidfitness.R

class SplashScreen : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // If user is logged in, navigate directly to HomeScreen
            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish() // Close the SplashScreen activity
        } else {
            // If user is not logged in, show the "Get Started" button
            val getStartedButton: Button = findViewById(R.id.startButton)
            getStartedButton.setOnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
    }
}
