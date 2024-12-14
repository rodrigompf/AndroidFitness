package _usersProfiles

import _main.Login
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val nameTextView = findViewById<TextView>(R.id.userName)
        val ageTextView = findViewById<TextView>(R.id.userAge)
        val descriptionTextView = findViewById<TextView>(R.id.userDescription)
        val profileImageView = findViewById<ImageView>(R.id.profileImage)
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Fetch the user's profile document from Firestore
            db.collection("Perfiles").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("name")
                        val userAge = document.getLong("age")?.toInt()
                        val userDescription = document.getString("description")
                        val profileImageUrl = document.getString("profileImageUrl")

                        nameTextView.text = userName ?: "No Name Found"
                        ageTextView.text = userAge?.toString() ?: "N/A"
                        descriptionTextView.text = userDescription ?: "No Description"

                        // Load the profile picture using Glide
                        if (profileImageUrl != null) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.fitness_logo)
                                .into(profileImageView)
                        }

                    } else {
                        Toast.makeText(this, "No profile found. Please create your profile.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, CreateProfile::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }
}
