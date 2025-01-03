package _homeScreen

import _homeScreen.DataBase.MessageAdapter
import _homeScreen.DataBase.MessageItem
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatmessageActivity : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = mutableListOf<MessageItem>()
    private lateinit var contactImageView: ImageView
    private lateinit var contactNameTextView: TextView
    private lateinit var sendButton: ImageButton
    private lateinit var messageEditText: EditText
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.chatmessage)

        // Initialize UI components
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        contactImageView = findViewById(R.id.contactImage)
        contactNameTextView = findViewById(R.id.contactNameHeader)
        sendButton = findViewById(R.id.sendButton)
        messageEditText = findViewById(R.id.messageEditText)

        // Set up RecyclerView
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get currentUserId here and pass it to MessageAdapter
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        messageAdapter = MessageAdapter(messageList, currentUserId)
        messagesRecyclerView.adapter = messageAdapter

        // Get the chat ID and partner ID from the intent
        val chatId = intent.getStringExtra("CHAT_ID") ?: run {
            Toast.makeText(this, "Invalid chat ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val partnerId = intent.getStringExtra("PARTNER_ID")
        if (partnerId.isNullOrBlank()) {
            Log.e("ChatmessageActivity", "PARTNER_ID is null or blank")
            Toast.makeText(this, "Invalid partner ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("ChatmessageActivity", "Received PARTNER_ID: $partnerId")

        // Load messages and partner details
        loadMessages(chatId)
        fetchPartnerProfile(partnerId)

        // Set up send button
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotBlank()) {
                sendMessage(chatId, message)
            }
        }
    }

    private fun loadMessages(chatId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("Chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    messageList.clear()
                    for (document in snapshots.documents) {
                        val message = document.toObject(MessageItem::class.java)
                        if (message != null) messageList.add(message)
                    }

                    // Now, pass the currentUserId to the MessageAdapter
                    messageAdapter = MessageAdapter(messageList, currentUserId)
                    messagesRecyclerView.adapter = messageAdapter
                    messageAdapter.notifyDataSetChanged()

                    // Scroll to the latest message
                    messagesRecyclerView.scrollToPosition(messageList.size - 1)
                }
            }
    }

    private fun sendMessage(chatId: String, message: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val partnerId = intent.getStringExtra("PARTNER_ID") ?: return

        // Create the message object
        val messageItem = MessageItem(
            senderId = currentUserId,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        // Ensure the chat document exists
        val chatRef = db.collection("Chats").document(chatId)
        chatRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Create the chat document with metadata if it doesn't exist
                val chatData = mapOf(
                    "participants" to listOf(currentUserId, partnerId),
                    "lastMessage" to message,
                    "lastUpdated" to System.currentTimeMillis()
                )
                chatRef.set(chatData)
            } else {
                // Update the lastMessage and lastUpdated fields
                chatRef.update(
                    "lastMessage", message,
                    "lastUpdated", System.currentTimeMillis()
                )
            }

            // Add the message to the "messages" subcollection
            chatRef.collection("messages")
                .add(messageItem)
                .addOnSuccessListener {
                    // Clear the input field and reload messages
                    messageEditText.text.clear()
                    loadMessages(chatId)
                    messagesRecyclerView.scrollToPosition(messageList.size - 1)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchPartnerProfile(partnerId: String) {
        db.collection("Perfiles").document(partnerId)
            .get()
            .addOnSuccessListener { profileDoc ->
                if (profileDoc.exists()) {
                    val partnerName = profileDoc.getString("nome") ?: "Unknown"
                    val partnerImageUrl = profileDoc.getString("picture") ?: ""
                    val partnerBase64 = profileDoc.getString("base64Image") ?: "" // Add this key if base64 string is stored

                    // Set partner's name in the TextView
                    contactNameTextView.text = partnerName

                    // Load image (either URL or Base64)
                    loadImage(contactImageView, partnerImageUrl, partnerBase64)
                }
            }
    }

    private fun loadImage(imageView: ImageView, imageUrl: String?, base64String: String?) {
        // Check for a valid Base64 string
        if (!base64String.isNullOrEmpty()) {
            try {
                // Decode the Base64 string into a byte array
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                // Decode byte array into Bitmap
                val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Set the Bitmap to the ImageView
                imageView.setImageBitmap(decodedBitmap)
            } catch (e: Exception) {
                // Log the error and set a default image if decoding fails
                e.printStackTrace()
                imageView.setImageResource(R.drawable.ic_launcher_foreground) // Default error image
            }
        } else if (!imageUrl.isNullOrEmpty()) {
            // If URL is provided, load it using Glide
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)  // Placeholder while loading
                .into(imageView)
        } else {
            // If no image URL or Base64 string, set a default image
            imageView.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
}
