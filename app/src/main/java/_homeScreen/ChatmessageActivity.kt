package _homeScreen

import _homeScreen.DataBase.MessageAdapter
import _homeScreen.DataBase.MessageItem
import android.content.Intent
import android.graphics.Bitmap
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

    private var partnerId: String? = null // Store partnerId

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

        partnerId = intent.getStringExtra("PARTNER_ID")
        if (partnerId.isNullOrBlank()) {
            Log.e("ChatmessageActivity", "PARTNER_ID is null or blank")
            Toast.makeText(this, "Invalid partner ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("ChatmessageActivity", "Received PARTNER_ID: $partnerId")

        // Load messages and partner details
        loadMessages(chatId)
        fetchPartnerProfile(partnerId!!)

        // Set up send button
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotBlank()) {
                sendMessage(chatId, message)
            }
        }
        setClickListeners()
    }

    private fun openPartnerDetailsActivity() {
        // Open PartnerDetailsActivity and pass the partnerId
        val intent = Intent(this, PartnerDetailsActivity::class.java)
        intent.putExtra("PARTNER_ID", partnerId)
        startActivity(intent)
    }
    private fun setClickListeners() {
        // Make the partner's name clickable
        contactNameTextView.setOnClickListener {
            openPartnerDetailsActivity()
        }

        // Make the partner's profile image clickable
        contactImageView.setOnClickListener {
            openPartnerDetailsActivity()
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
                    val pictureData = profileDoc.getString("picture") ?: ""

                    // Determine if the pictureData is Base64 or a URL
                    val partnerImageBitmap: Bitmap? = if (isBase64Encoded(pictureData)) {
                        decodeBase64ToBitmap(pictureData)
                    } else {
                        null // If not Base64, use URL directly in the adapter
                    }

                    // Set partner's name in the TextView
                    contactNameTextView.text = partnerName

                    // Load the partner's image (Base64 or URL)
                    loadPartnerImage(partnerImageBitmap, pictureData)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatmessageActivity", "Error fetching profile: $partnerId", exception)
            }
    }

    private fun isBase64Encoded(data: String): Boolean {
        return try {
            val decodedBytes = Base64.decode(data, Base64.DEFAULT)
            val encodedString = Base64.encodeToString(decodedBytes, Base64.DEFAULT).trim()
            data.trim() == encodedString // Check if re-encoded Base64 matches original
        } catch (e: IllegalArgumentException) {
            false // If decoding fails, it's not Base64
        }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun loadPartnerImage(bitmap: Bitmap?, imageUrl: String) {
        if (bitmap != null) {
            contactImageView.setImageBitmap(bitmap)
        } else if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image
                .into(contactImageView)
        } else {
            contactImageView.setImageResource(R.drawable.ic_launcher_foreground) // Default image
        }
    }
}
