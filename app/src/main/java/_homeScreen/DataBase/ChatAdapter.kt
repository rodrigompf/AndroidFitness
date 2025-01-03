package _homeScreen.DataBase

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

class ChatAdapter(
    private val chatList: List<ChatItem>,
    private val onItemClick: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val partnerImage: ImageView = itemView.findViewById(R.id.partnerImage)
        private val partnerName: TextView = itemView.findViewById(R.id.partnerName)

        fun bind(chatItem: ChatItem) {
            partnerName.text = chatItem.name

            // Load image based on whether it's Base64 or a URL
            if (chatItem.imageBitmap != null) {
                partnerImage.setImageBitmap(chatItem.imageBitmap)
            } else {
                loadImage(chatItem.imageUrl, partnerImage) // If it's not Base64, use the URL
            }

            // Set click listener
            itemView.setOnClickListener { onItemClick(chatItem) }
        }

        private fun loadImage(imageData: String?, imageView: ImageView) {
            if (imageData.isNullOrEmpty()) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground) // Placeholder for empty image
                return
            }

            if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                // Load image from URL using Glide
                Glide.with(itemView.context)
                    .load(imageData)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView)
            } else {
                try {
                    // Decode Base64 string and display
                    val decodedString = Base64.decode(imageData, Base64.DEFAULT)
                    val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imageView.setImageBitmap(decodedBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(itemView.context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    imageView.setImageResource(R.drawable.ic_launcher_foreground) // Fallback image in case of error
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chaticon, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size
}
