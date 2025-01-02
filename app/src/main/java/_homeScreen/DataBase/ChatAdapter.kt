package _homeScreen.DataBase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfitness.R

class ChatAdapter(
    private val chatList: List<ChatItem>,
    private val onItemClick: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partnerImage: ImageView = itemView.findViewById(R.id.partnerImage)
        val partnerName: TextView = itemView.findViewById(R.id.partnerName)

        // Bind the data to the views
        fun bind(chatItem: ChatItem) {
            // Set the name of the partner
            partnerName.text = chatItem.name

            // Use Glide to load the image (URL or Base64)
            Glide.with(itemView.context)
                .load(chatItem.imageUrl)  // If it's a URL or Base64, Glide will handle it
                .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder image
                .error(R.drawable.ic_launcher_foreground)  // Error image
                .into(partnerImage)  // Set the image in the ImageView

            // Set the click listener for the chat item
            itemView.setOnClickListener { onItemClick(chatItem) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chaticon, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]

        // Bind the chat item data to the ViewHolder
        holder.bind(chatItem)
    }

    override fun getItemCount(): Int = chatList.size
}


