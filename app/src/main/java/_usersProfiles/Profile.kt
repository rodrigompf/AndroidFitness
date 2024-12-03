package com.example.androidfitness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get UI Elements
        val nameTextView = findViewById<TextView>(R.id.userName)
        val emailTextView = findViewById<TextView>(R.id.userEmail)
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)

        // Fetch Current User
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Set Email from Auth
            emailTextView.text = currentUser.email

            // Fetch Additional Data from Firestore
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")
                        nameTextView.text = userName ?: "No Name Found"
                    } else {
                        Log.d("Profile", "No document found for user.")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Profile", "Error fetching user data", exception)
                    Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user is currently logged in.", Toast.LENGTH_SHORT).show()
            // Optionally redirect to Login Activity
        }

        // Edit Profile Button Click Listener
        editProfileButton.setOnClickListener {
            // Navigate to Edit Profile Activity
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}
