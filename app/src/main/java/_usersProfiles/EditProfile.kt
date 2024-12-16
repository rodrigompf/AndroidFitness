package _usersProfiles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var profileImageView: ImageView
    private lateinit var userNameEditText: EditText
    private lateinit var userAgeEditText: EditText
    private lateinit var userDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private val STORAGE_PERMISSION_CODE = 1001

    private var imageUri: Uri? = null

    class EditProfile : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        private lateinit var profileImageView: ImageView
        private lateinit var userNameEditText: EditText
        private lateinit var userAgeEditText: EditText
        private lateinit var userDescriptionEditText: EditText
        private lateinit var saveButton: Button
        private val STORAGE_PERMISSION_CODE = 1001  // Permission request code

        private var imageUri: Uri? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Log.d("EditProfile", "onCreate called")

            try {
                setContentView(R.layout.edit_profile)

                auth = FirebaseAuth.getInstance()
                db = FirebaseFirestore.getInstance()

                profileImageView = findViewById(R.id.profileImage)
                userNameEditText = findViewById(R.id.userName)
                userAgeEditText = findViewById(R.id.userAge)
                userDescriptionEditText = findViewById(R.id.userDescription)
                saveButton = findViewById(R.id.saveButton)

                Log.d("EditProfile", "UI elements initialized")

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                }
            } catch (e: Exception) {
                Log.e("EditProfile", "Error in onCreate", e)
            }
        }


        // Handle the permission request response
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == STORAGE_PERMISSION_CODE) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun selectProfileImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                profileImageView.setImageURI(imageUri) // Display the selected image
            } else {
                Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveProfileData() {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val email = user.email ?: "" // Ensure email is non-null
            val name = userNameEditText.text.toString().trim()
            val age = userAgeEditText.text.toString().trim().toIntOrNull() ?: 0
            val description = userDescriptionEditText.text.toString().trim()

            // Initialize the profile data map
            val profileData = hashMapOf<String, Any>(
                "name" to name,
                "age" to age,
                "email" to email,
                "description" to description
            )

            val storageRef = FirebaseStorage.getInstance().reference

            if (imageUri != null) {
                val user = auth.currentUser
                if (user != null) {
                    val userId = user.uid

                    val storageRef = FirebaseStorage.getInstance().reference
                    val profileImageRef = storageRef.child("Perfiles/$userId/profile.jpg")

                    // Upload image to Firebase Storage
                    profileImageRef.putFile(imageUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            // Get the download URL
                            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()

                                // Save user data along with the profile URL to Firestore
                                val email = user.email ?: ""
                                val name = userNameEditText.text.toString().trim()
                                val age = userAgeEditText.text.toString().trim().toIntOrNull() ?: 0
                                val description = userDescriptionEditText.text.toString().trim()

                                val profileData = mapOf(
                                    "name" to name,
                                    "age" to age,
                                    "email" to email,
                                    "description" to description,
                                    "profileImageUrl" to imageUrl
                                )

                                db.collection("Perfiles").document(userId).set(profileData)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Firestore update failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }



        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}

