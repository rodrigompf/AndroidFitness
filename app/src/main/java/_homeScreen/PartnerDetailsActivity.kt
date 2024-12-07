package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.github.chrisbanes.photoview.PhotoView

class PartnerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partner_details)

        val partner = intent.getSerializableExtra("PARTNER_DATA") as? PartnerProfile

        val partnerImage: ImageView = findViewById(R.id.partnerDetailImage)
        val partnerName: TextView = findViewById(R.id.partnerDetailName)
        val partnerAge: TextView = findViewById(R.id.partnerDetailAge)
        val partnerDescription: TextView = findViewById(R.id.partnerDetailDescription)
        val horizontalLayout: LinearLayout = findViewById(R.id.HorizontalLayout)

        if (partner != null) {
            partnerName.text = partner.nome
            partnerAge.text = "Age: ${partner.idade}"
            partnerDescription.text = partner.resumo

            Glide.with(this)
                .load(partner.picture)
                .placeholder(R.drawable.ic_launcher)
                .into(partnerImage)

            // Set OnClickListener on the main image to show popup fullscreen
            partnerImage.setOnClickListener {
                showImagePopup(partner.picture)
            }

            // Dynamically populate HorizontalLayout with additional images
            horizontalLayout.removeAllViews()

            for (imageUrl in partner.images) {
                val imageView = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(300, 300).apply {
                        setMargins(8, 0, 8, 0)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }

                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView)

                // Set OnClickListener to show popup dialog for each image
                imageView.setOnClickListener {
                    showImagePopup(imageUrl)
                }

                horizontalLayout.addView(imageView)
            }

        } else {
            Toast.makeText(this, "No partner data found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImagePopup(imageUrl: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.fullscreenimagem)

        val fullScreenImage: PhotoView = dialog.findViewById(R.id.fullScreenImageView)
        Glide.with(this).load(imageUrl).into(fullScreenImage)

        dialog.show()

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}

