package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.firestore.FirebaseFirestore

class PartnerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partner_details)

        val partnerId = intent.getStringExtra("PARTNER_ID")
        if (partnerId != null) {
            fetchPartnerDetails(partnerId)
        } else {
            Toast.makeText(this, "No partner ID found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPartnerDetails(partnerId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Perfiles").document(partnerId).get()
            .addOnSuccessListener { document ->
                val partner = document.toObject(PartnerProfile::class.java)
                if (partner != null) {
                    displayPartnerDetails(partner)
                } else {
                    Toast.makeText(this, "Partner not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PartnerDetails", "Error fetching partner details", exception)
                Toast.makeText(this, "Error loading partner details", Toast.LENGTH_LONG).show()
            }
    }

    private fun displayPartnerDetails(partner: PartnerProfile) {
        val partnerImage: ImageView = findViewById(R.id.partnerDetailImage)
        val partnerName: TextView = findViewById(R.id.partnerDetailName)
        val partnerAge: TextView = findViewById(R.id.partnerDetailAge)
        val partnerDescription: TextView = findViewById(R.id.partnerDetailDescription)
        val horizontalLayout: LinearLayout = findViewById(R.id.HorizontalLayout)

        partnerName.text = partner.nome
        partnerAge.text = "Age: ${partner.idade}"
        partnerDescription.text = partner.resumo

        loadImage(partner.picture, partnerImage)

        // Set up click listener for the main image to view full-screen
        partnerImage.setOnClickListener {
            openFullScreenImage(partner.picture)
        }

        horizontalLayout.removeAllViews()
        partner.images?.forEach { imageData ->
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(300, 300).apply {
                    setMargins(8, 0, 8, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            loadImage(imageData, imageView)

            // Add click listener to open full-screen dialog on image click
            imageView.setOnClickListener {
                openFullScreenImage(imageData)
            }
            horizontalLayout.addView(imageView)
        }
    }

    private fun loadImage(imageData: String, imageView: ImageView) {
        if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
            Glide.with(this)
                .load(imageData)
                .placeholder(R.drawable.ic_launcher)
                .into(imageView)
        } else {
            try {
                if (imageData.isNotEmpty()) {
                    val decodedString = Base64.decode(imageData, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imageView.setImageBitmap(decodedBitmap)
                } else {
                    imageView.setImageResource(R.drawable.ic_launcher)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                imageView.setImageResource(R.drawable.ic_launcher)
            }
        }
    }

    private fun openFullScreenImage(imageData: String) {
        val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.fullscreenimagem)

        val photoView: PhotoView = dialog.findViewById(R.id.photoView)

        if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
            Glide.with(this)
                .load(imageData)
                .placeholder(R.drawable.ic_launcher)
                .into(photoView)
        } else {
            try {
                if (imageData.isNotEmpty()) {
                    val decodedString = Base64.decode(imageData, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    photoView.setImageBitmap(decodedBitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setCancelable(true)
        dialog.show()
    }
}
