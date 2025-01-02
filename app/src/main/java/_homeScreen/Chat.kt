package _homeScreen

import _homeScreen.DataBase.ChatAdapter
import _homeScreen.DataBase.ChatItem
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Chat : AppCompatActivity() {

    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        // Initialize RecyclerView
        chatsRecyclerView = findViewById(R.id.chatsRecyclerView)
        chatsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = ChatAdapter(chatList) { chatItem ->
            // Handle chat item click (Navigate to chat screen)
            val intent = Intent(this, ChatmessageActivity::class.java)
            intent.putExtra("CHAT_ID", chatItem.id)
            intent.putExtra("PARTNER_ID", chatItem.partnerId)  // Pass the partnerId as well
            startActivity(intent)
        }
        chatsRecyclerView.adapter = chatAdapter

        // Load chats for the current user
        loadUserChats()
    }

    private fun loadUserChats() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (currentUserId != null) {
            // Query Chats where the logged-in user is a participant
            db.collection("Chats")
                .whereArrayContains("participants", currentUserId)
                .get()
                .addOnSuccessListener { chatsResult ->
                    for (chatDoc in chatsResult.documents) {
                        val chatId = chatDoc.id
                        val participants = chatDoc.get("participants") as? List<String> ?: listOf()

                        // Find the partner ID (the ID that isn't the logged-in user)
                        val partnerId = participants.firstOrNull { it != currentUserId }

                        if (partnerId != null) {
                            // Fetch partner's profile from Perfiles
                            fetchPartnerProfile(partnerId, chatId)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Chat", "Error fetching chats: ${exception.message}", exception)
                }
        } else {
            Log.e("Chat", "Current user is not authenticated.")
        }
    }



    private fun fetchPartnerProfile(partnerId: String, chatId: String) {
        val db = FirebaseFirestore.getInstance()

        Log.d("Chat", "Fetching profile for partnerId: $partnerId")

        db.collection("Perfiles").document(partnerId)
            .get()
            .addOnSuccessListener { profileDoc ->
                if (profileDoc.exists()) {
                    val partnerName = profileDoc.getString("nome") ?: "Unknown"
                    val partnerImageUrl = profileDoc.getString("picture") ?: ""

                    // Log the fetched profile data
                    Log.d("Chat", "Fetched profile: Name = $partnerName, Image URL = $partnerImageUrl, Partner ID = $partnerId")

                    // Create ChatItem and add to the chat list
                    val chatItem = ChatItem(
                        id = chatId,
                        name = partnerName,
                        imageUrl = partnerImageUrl,  // Use the fetched image URL
                        unreadCount = 0, // Example unread count
                        partnerId = partnerId  // Ensure partnerId is added to the chat item
                    )

                    chatList.add(chatItem)
                    chatAdapter.notifyDataSetChanged() // Notify adapter about the new data
                } else {
                    Log.w("Chat", "Profile not found for partnerId: $partnerId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Chat", "Error fetching profile for partnerId: $partnerId", exception)
            }
    }

}



