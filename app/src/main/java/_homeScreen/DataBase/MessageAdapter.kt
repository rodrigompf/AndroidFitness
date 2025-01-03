package _homeScreen.DataBase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R

class MessageAdapter(
    private val messages: List<MessageItem>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Define ViewTypes
    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_PARTNER = 2

    // ViewHolder for User's Messages (Right-aligned)
    inner class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: MessageItem) {
            messageTextView.text = message.message
        }
    }

    // ViewHolder for Partner's Messages (Left-aligned)
    inner class PartnerMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: MessageItem) {
            messageTextView.text = message.message
        }
    }

    // Determine which ViewType to use
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_USER // Current user's message
        } else {
            VIEW_TYPE_PARTNER // Partner's message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_PARTNER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item_partner, parent, false)
                PartnerMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is PartnerMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size
}
