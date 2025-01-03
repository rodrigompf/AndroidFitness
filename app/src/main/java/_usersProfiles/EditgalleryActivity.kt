package _usersProfiles

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditGalleryActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var galleryContainer: LinearLayout
    private lateinit var addImageButton: Button
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    private val STORAGE_PERMISSION_CODE = 1001
    private val PICK_IMAGE_REQUEST = 1
    private val imageUris = mutableListOf<Uri>()
    private val imageBase64List = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editgallery)

        // Initialize Firebase services
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Bind views
        galleryContainer = findViewById(R.id.galleryContainer)
        addImageButton = findViewById(R.id.addImageButton)
        saveButton = findViewById(R.id.uploadImagesButton)
        backButton = findViewById(R.id.backToProfileButton)

        // Request permissions
        checkAndRequestPermissions()

        // Set click listeners
        addImageButton.setOnClickListener { openImagePicker() }
        saveButton.setOnClickListener { saveGalleryImages() }
        backButton.setOnClickListener { finish() }

        // Load existing images from Firestore
        loadExistingImages()
    }

    // Request storage permissions if necessary
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, STORAGE_PERMISSION_CODE)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
        }
    }

    // Load existing images from Firestore
    private fun loadExistingImages() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        db.collection("Perfiles").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val existingImages = document.get("images") as? List<String> ?: mutableListOf()

                    // Save images to the imageBase64List and display them in the gallery
                    imageBase64List.clear()
                    imageBase64List.addAll(existingImages)

                    // Display existing images in gallery
                    displayExistingImages()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching images: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Display existing images in gallery container
    private fun displayExistingImages() {
        galleryContainer.removeAllViews() // Clear any existing views

        for (base64Image in imageBase64List) {
            val imageView = ImageView(this)
            val bitmap = decodeBase64ToBitmap(base64Image)
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                setMargins(8, 0, 8, 0)
            }

            // Set click listener to show image and delete option
            imageView.setOnClickListener {
                showDeleteDialog(base64Image, bitmap)
            }

            galleryContainer.addView(imageView)
        }
    }

    // Decode Base64 string to Bitmap
    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    // Show delete confirmation dialog
    private fun showDeleteDialog(imageBase64: String, bitmap: Bitmap) {
        val dialogBuilder = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setImageBitmap(bitmap)
        imageView.layoutParams = LinearLayout.LayoutParams(400, 400) // Show bigger image in dialog

        dialogBuilder.setTitle("Delete Image")
            .setMessage("Do you want to delete this image?")
            .setView(imageView)
            .setPositiveButton("Yes") { _, _ ->
                deleteImageFromFirestore(imageBase64)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Delete image from Firestore
    private fun deleteImageFromFirestore(imageBase64: String) {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val userProfileRef = db.collection("Perfiles").document(userId)

        userProfileRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val existingImages = document.get("images") as? List<String> ?: mutableListOf()
                val updatedImages = existingImages.toMutableList()

                // Remove the image from the list
                updatedImages.remove(imageBase64)

                // Update Firestore document with the new list
                userProfileRef.update("images", updatedImages)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Image deleted successfully.", Toast.LENGTH_SHORT).show()
                        // Refresh gallery
                        loadExistingImages()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error deleting image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    // Open image picker for multiple images
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple image selection
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val clipData = data.clipData
            if (clipData != null) {
                // If multiple images are selected
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                    displayImage(imageUri)
                }
            } else {
                // If a single image is selected
                val imageUri = data.data
                if (imageUri != null) {
                    imageUris.add(imageUri)
                    displayImage(imageUri)
                }
            }
        }
    }

    // Display selected image in the gallery container
    private fun displayImage(imageUri: Uri) {
        val imageView = ImageView(this)
        imageView.setImageURI(imageUri)
        imageView.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
            setMargins(8, 0, 8, 0)
        }
        galleryContainer.addView(imageView)
    }

    // Resize image to a suitable size for uploading
    private fun resizeImage(imageUri: Uri, context: Context): Bitmap? {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        val width = originalBitmap.width
        val height = originalBitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()
        val newWidth = 500 // Desired width in pixels
        val newHeight = (newWidth / aspectRatio).toInt()

        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)
    }

    // Convert image to Base64 string
    private fun convertImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Save selected images to Firestore
    private fun saveGalleryImages() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        val base64Images = mutableListOf<String>()

        // Convert each image URI to Base64 and add to the list
        try {
            for (uri in imageUris) {
                val resizedBitmap = resizeImage(uri, applicationContext)
                if (resizedBitmap != null) {
                    val base64Image = convertImageToBase64(resizedBitmap)
                    base64Images.add(base64Image)
                }
            }

            // Check if base64Images list is empty or not
            if (base64Images.isEmpty()) {
                Toast.makeText(this, "No images selected.", Toast.LENGTH_SHORT).show()
                return
            }

            // Save images to Firestore
            val userProfileRef = db.collection("Perfiles").document(userId)

            userProfileRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Fetch existing images if any
                    val existingImages = document.get("images") as? List<String> ?: mutableListOf()

                    // Combine the new images with the existing ones
                    val updatedImages = existingImages.toMutableList()
                    updatedImages.addAll(base64Images)

                    // Update the Firestore document with the new images array
                    userProfileRef.update("images", updatedImages)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Images uploaded successfully.", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error uploading images: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If document doesn't exist, create a new one with images
                    val newProfileData = hashMapOf("images" to base64Images)
                    userProfileRef.set(newProfileData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Images uploaded successfully.", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error creating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing images: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
