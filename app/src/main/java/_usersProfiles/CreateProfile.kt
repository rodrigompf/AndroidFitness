package _usersProfiles

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val saveProfileButton = findViewById<Button>(R.id.saveProfileButton)

        saveProfileButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val currentUser = auth.currentUser

            if (name.isNotEmpty() && currentUser != null) {
                val userId = currentUser.uid

                val userProfile = mapOf(
                    "name" to name,
                    "email" to currentUser.email
                )

                // Save profile to Perfiles collection
                db.collection("Perfiles").document(userId).set(userProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile created successfully.", Toast.LENGTH_SHORT).show()
                        finish() // Close CreateProfile and return to Profile Activity
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error creating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
