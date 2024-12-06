package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidfitness.R

class PartnerDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.partner_details)

        val partner = intent.getSerializableExtra("PARTNER_DATA") as? PartnerProfile

        val partnerImage: ImageView = findViewById(R.id.partnerDetailImage)
        val partnerName: TextView = findViewById(R.id.partnerDetailName)
        val partnerAge: TextView = findViewById(R.id.partnerDetailAge)
        val partnerDescription: TextView = findViewById(R.id.partnerDetailDescription)

        if (partner != null) {
            partnerName.text = partner.nome
            partnerAge.text = "Age: ${partner.idade}"
            partnerDescription.text = partner.resumo

            // Load the partner's image using Glide
            Glide.with(this)
                .load(partner.picture)
                .placeholder(R.drawable.ic_launcher)
                .into(partnerImage)

        } else {
            Toast.makeText(this, "No partner data found", Toast.LENGTH_SHORT).show()
        }
    }
}