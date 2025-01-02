package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfitness.R

class PartnerAdapter(
    private val partnerList: MutableList<PartnerProfile>, // Now just PartnerProfile list
    private val onCardClick: (PartnerProfile) -> Unit // Callback for card click
) : RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.partner_card_item, parent, false)
        return PartnerViewHolder(view, onCardClick)
    }

    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
        val partner = partnerList[position]
        holder.bind(partner)
    }

    override fun getItemCount(): Int = partnerList.size

    // Method to update the list and notify the adapter
    fun updatePartnerProfile(partner: PartnerProfile) {
        partnerList.add(partner)
        notifyItemInserted(partnerList.size - 1)
    }

    // ViewHolder class for binding data to views
    class PartnerViewHolder(
        itemView: View,
        private val onCardClick: (PartnerProfile) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val partnerNome: TextView = itemView.findViewById(R.id.partnerNome)
        private val partnerIdade: TextView = itemView.findViewById(R.id.partnerIdade)
        private val partnerPicture: ImageView = itemView.findViewById(R.id.partnerPicture)

        fun bind(partner: PartnerProfile) {
            partnerNome.text = partner.nome
            partnerIdade.text = "Age: ${partner.idade}"

            // Call the loadImage function to load the picture
            loadImage(partner.picture, partnerPicture)

            // Handle card click
            itemView.setOnClickListener {
                onCardClick(partner)
            }
        }

        // Method to load images (Base64 or URL)
        private fun loadImage(imageData: String?, imageView: ImageView) {
            if (imageData.isNullOrEmpty()) {
                imageView.setImageResource(R.drawable.ic_launcher) // Fallback image if data is empty
                return
            }

            if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                // Load image from URL using Glide
                Glide.with(itemView.context)
                    .load(imageData)
                    .placeholder(R.drawable.ic_launcher) // Placeholder while image is loading
                    .into(imageView)
            } else {
                try {
                    // Decode Base64 string and load into ImageView
                    val decodedString = Base64.decode(imageData, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imageView.setImageBitmap(decodedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(itemView.context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    imageView.setImageResource(R.drawable.ic_launcher) // Fallback image in case of error
                }
            }
        }
    }
}



