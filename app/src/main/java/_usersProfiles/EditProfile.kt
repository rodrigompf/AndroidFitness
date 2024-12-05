package _usersProfiles

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val emailDisplay = findViewById<EditText>(R.id.emailDisplay)
        val saveProfileButton = findViewById<Button>(R.id.saveProfileButton)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Load existing profile data
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        nameInput.setText(name)
                        emailDisplay.setText(email)
                    } else {
                        Toast.makeText(this, "No profile data found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            // Save updated profile data
            saveProfileButton.setOnClickListener {
                val updatedName = nameInput.text.toString().trim()

                if (updatedName.isNotEmpty()) {
                    val updatedProfile = mapOf(
                        "name" to updatedName,
                        "email" to currentUser.email // Ensure the email remains consistent
                    )

                    db.collection("users").document(userId).update(updatedProfile)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                            finish() // Close the EditProfile activity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No user is currently logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
