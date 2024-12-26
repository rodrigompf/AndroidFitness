package _usersProfiles

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.InputStream

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Bind views
        profileImageView = findViewById(R.id.profileImage)
        userNameEditText = findViewById(R.id.userName)
        userAgeEditText = findViewById(R.id.userAge)
        userDescriptionEditText = findViewById(R.id.userDescription)
        saveButton = findViewById(R.id.saveButton)

        // Request permissions
        checkAndRequestPermissions()

        // Set click listeners
        saveButton.setOnClickListener { saveProfileData() }
        profileImageView.setOnClickListener { selectProfileImage() }
    }

    // Check and request permissions for storage
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above, request manage external storage permission
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, STORAGE_PERMISSION_CODE)
            }
        } else {
            // For lower versions, request normal storage permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    // Handle image selection from gallery
    private fun selectProfileImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the result of the image selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                // Resize and set image to ImageView
                val resizedBitmap = resizeImage(imageUri!!, applicationContext)
                profileImageView.setImageBitmap(resizedBitmap)
                Log.d("ImageSelection", "Image URI: $imageUri")
            } else {
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Show permission denied dialog
    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Please enable storage access in Settings to select an image.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Resize image to reduce size
    private fun resizeImage(imageUri: Uri, context: Context): Bitmap? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Resize the bitmap to a smaller size
        val width = originalBitmap.width
        val height = originalBitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth = 500 // Desired width in pixels
        val newHeight = (newWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)
    }

    // Convert image Bitmap to Base64
    private fun convertImageToBase64(bitmap: Bitmap, context: Context): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Save profile data to Firestore
    private fun saveProfileData() {
        val user = auth.currentUser ?: return
        val userId = user.uid
        val email = user.email ?: ""
        val name = userNameEditText.text.toString().trim()
        val age = userAgeEditText.text.toString().trim().toIntOrNull() ?: 0
        val description = userDescriptionEditText.text.toString().trim()

        val profileData = hashMapOf(
            "name" to name,
            "age" to age,
            "email" to email,
            "description" to description
        )

        // Upload the profile image (in Base64 format) to Firestore
        if (imageUri != null) {
            val resizedBitmap = resizeImage(imageUri!!, applicationContext)
            val base64String = convertImageToBase64(resizedBitmap!!, applicationContext)

            // Add the Base64 image string to the profile data
            profileData["profileImage"] = base64String
        }

        // Save the profile data to Firestore under the user's ID
        db.collection("Perfiles").document(userId).set(profileData)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Companion object for PICK_IMAGE_REQUEST constant
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
