package com.example.androidfitness

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity

class HomeScreen : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var logoutButton: Button
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Views
        logoutButton = findViewById(R.id.logoutButton)
        welcomeText = findViewById(R.id.welcomeText)

        // Set welcome message
        val currentUser = mAuth.currentUser
        welcomeText.text = "Welcome, ${currentUser?.email ?: "User"}!"

        // Handle logout button click
        logoutButton.setOnClickListener {
            mAuth.signOut() // Sign out the user
            val intent = Intent(this, LoginActivity::class.java) // Redirect to Login
            startActivity(intent)
            finish() // Finish HomeScreenActivity so user can't go back
        }
    }
}
