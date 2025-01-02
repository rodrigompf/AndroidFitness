package _usersProfiles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
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

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
                        val userName = document.getString("nome")
                        val userAge = document.getLong("idade")?.toInt()
                        val userDescription = document.getString("descrição")
                        val profileImageBase64 = document.getString("picture") // Get the Base64 image

                        nameTextView.text = userName ?: "No Name Found"
                        ageTextView.text = userAge?.toString() ?: "N/A"
                        descriptionTextView.text = userDescription ?: "No Description"

                        // Load the profile picture if it's Base64 encoded
                        if (profileImageBase64 != null) {
                            val bitmap = decodeBase64ToBitmap(profileImageBase64)
                            profileImageView.setImageBitmap(bitmap)
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

        // Navigate to EditProfile activity
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }

    // Decode Base64 string to Bitmap
    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}

