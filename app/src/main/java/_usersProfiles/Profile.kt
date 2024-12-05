package _usersProfiles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R
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

            // Check if the user already has a profile in the "Perfiles" collection
            db.collection("Perfiles").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // If profile exists, fetch the profile data
                        val userName = document.getString("name")
                        nameTextView.text = userName ?: "No Name Found"
                    } else {
                        // If profile doesn't exist, navigate to CreateProfile activity
                        Toast.makeText(this, "No profile found. Please create your profile.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, CreateProfile::class.java)
                        startActivity(intent)
                        finish() // Close Profile Activity
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error checking profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user is currently logged in.", Toast.LENGTH_SHORT).show()
            // Optionally redirect to Login Activity
        }

        // Edit Profile Button Click Listener
        editProfileButton.setOnClickListener {
            // Navigate to Edit Profile Activity (you can create this if needed)
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }
}
