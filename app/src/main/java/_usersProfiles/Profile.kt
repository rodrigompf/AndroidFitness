package _usersProfiles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
        val resumoTextView = findViewById<TextView>(R.id.userResumo)  // Added for resumo
        val profileImageView = findViewById<ImageView>(R.id.profileImage)
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)
        val galleryContainer = findViewById<LinearLayout>(R.id.galleryContainer)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Set up a real-time listener for the user's profile document
            db.collection("Perfiles").document(userId)
                .addSnapshotListener { documentSnapshot, exception ->
                    if (exception != null) {
                        Toast.makeText(this, "Failed to fetch profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("nome")
                        val userAge = documentSnapshot.getLong("idade")?.toInt()
                        val userDescription = documentSnapshot.getString("descrição")
                        val userResumo = documentSnapshot.getString("resumo")  // Fetch resumo
                        val profileImageBase64 = documentSnapshot.getString("picture")
                        val galleryImagesBase64 = documentSnapshot.get("images") as? List<String> ?: emptyList()

                        // Display user info
                        nameTextView.text = userName ?: "No Name Found"
                        ageTextView.text = userAge?.toString() ?: "N/A"
                        descriptionTextView.text = userDescription ?: "No Description"
                        resumoTextView.text = userResumo ?: "No Summary"

                        // Load the profile picture if it's Base64 encoded
                        if (profileImageBase64 != null) {
                            val bitmap = decodeBase64ToBitmap(profileImageBase64)
                            profileImageView.setImageBitmap(bitmap)
                        }

                        // Display gallery images
                        displayGalleryImages(galleryImagesBase64, galleryContainer)

                    } else {
                        Toast.makeText(this, "No profile found. Please create your profile.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, CreateProfile::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

            // Edit Gallery Button
            val editGalleryButton = findViewById<Button>(R.id.Edit_Gallery)
            editGalleryButton.setOnClickListener {
                val intent = Intent(this, EditGalleryActivity::class.java)
                startActivity(intent)
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

    // Display gallery images in the gallery container
    private fun displayGalleryImages(imagesBase64: List<String>, galleryContainer: LinearLayout) {
        galleryContainer.removeAllViews() // Clear any previous images

        for (base64Image in imagesBase64) {
            val bitmap = decodeBase64ToBitmap(base64Image)
            val imageView = ImageView(this)
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                setMargins(8, 8, 8, 8)
            }
            galleryContainer.addView(imageView)
        }
    }
}
