package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class FindTrainingPartners : AppCompatActivity() {

    private lateinit var partnersRecyclerView: RecyclerView
    private lateinit var glowBackground: View
    private lateinit var partnerAdapter: PartnerAdapter
    private val profiles = mutableListOf<PartnerProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_training_partners)

        partnersRecyclerView = findViewById(R.id.partnersRecyclerView)
        glowBackground = findViewById(R.id.glowBackground)

        partnersRecyclerView.layoutManager = LinearLayoutManager(this)
        partnerAdapter = PartnerAdapter(profiles) { partner ->

            val intent = Intent(this, PartnerDetailsActivity::class.java)
            intent.putExtra("PARTNER_ID", partner.id) // Pass the ID to the details activity
            startActivity(intent)
        }
        partnersRecyclerView.adapter = partnerAdapter

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            private fun getCurrentUserId(): String {
                val user = FirebaseAuth.getInstance().currentUser
                return user?.uid ?: "anonymous"
            }


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                val partner = profiles[swipedPosition]
                val currentUserId = getCurrentUserId() // Fetch current user ID from your auth system

                val db = FirebaseFirestore.getInstance()
                val matchCollection = db.collection("Matches")
                val chatsCollection = db.collection("Chats") // Collection for chats

                val isYes = direction == ItemTouchHelper.LEFT // Swiping left means "Yes"

                if (isYes) {
                    // Step 1: Check if the user has already voted on this profile
                    matchCollection
                        .whereEqualTo("userId", currentUserId)
                        .whereEqualTo("partnerId", partner.id)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                // User has not voted yet, so proceed with the vote and match creation

                                // Step 2: Create the match document for current user and partner
                                val matchDocumentId = "$currentUserId-${partner.id}"

                                val matchData = mapOf(
                                    "userId" to currentUserId,
                                    "partnerId" to partner.id,
                                    "userVote" to true, // Current user voted "Yes"
                                    "partnerVote" to null // Initially null, as we haven't checked the partner's vote yet
                                )

                                // Create the match document
                                matchCollection.document(matchDocumentId)
                                    .set(matchData, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("FindTrainingPartners", "Match created successfully for: $matchDocumentId")

                                        // Step 3: Create a chat if the user swiped "Yes"
                                        val chatId = if (currentUserId < partner.id) {
                                            "$currentUserId-${partner.id}"
                                        } else {
                                            "${partner.id}-$currentUserId"
                                        }

                                        // Create the chat data
                                        val chatData = mapOf(
                                            "participants" to listOf(currentUserId, partner.id),
                                            "createdAt" to FieldValue.serverTimestamp()
                                        )

                                        // Create the chat document in Firestore
                                        chatsCollection.document(chatId)
                                            .set(chatData, SetOptions.merge())
                                            .addOnSuccessListener {
                                                Log.d("FindTrainingPartners", "Chat created successfully for: $chatId")
                                            }
                                            .addOnFailureListener { exception ->
                                                Log.w("FindTrainingPartners", "Error creating chat: ", exception)
                                            }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w("FindTrainingPartners", "Error creating match: $matchDocumentId", exception)
                                    }

                                // Remove the swiped item from the list and notify the adapter
                                profiles.removeAt(swipedPosition)
                                partnerAdapter.notifyItemRemoved(swipedPosition)

                                // Fetch a new profile if the list is empty
                                if (profiles.isEmpty()) {
                                    fetchProfilesFromFirestore()
                                }
                            } else {
                                Log.d("FindTrainingPartners", "You have already voted on this profile.")
                                // Notify the user that they can't vote on this profile again
                                Toast.makeText(this@FindTrainingPartners, "You've already voted on this profile.", Toast.LENGTH_SHORT).show()

                                // Remove the swiped item from the list and notify adapter
                                profiles.removeAt(swipedPosition)
                                partnerAdapter.notifyItemRemoved(swipedPosition)

                                // Fetch a new profile if the list is empty
                                if (profiles.isEmpty()) {
                                    fetchProfilesFromFirestore()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("FindTrainingPartners", "Error checking if user has already voted on this profile", exception)
                        }
                }
            }





            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val width = recyclerView.width
                val view = viewHolder.itemView

                view.translationX = dX

                val maxTiltAngle = 15f
                val tilt = (dX / width) * maxTiltAngle
                view.rotation = tilt

                view.translationY = Math.abs(dX / 10)

                val glowIntensity = Math.min(Math.abs(dX) / width, 1f)
                val gradientDrawable = GradientDrawable(
                    if (dX > 0) GradientDrawable.Orientation.LEFT_RIGHT else GradientDrawable.Orientation.RIGHT_LEFT,
                    intArrayOf(
                        Color.TRANSPARENT,
                        if (dX > 0) Color.RED else Color.GREEN
                    )
                )
                glowBackground.alpha = glowIntensity
                glowBackground.background = gradientDrawable

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                val view = viewHolder.itemView
                view.rotation = 0f
                view.translationY = 0f

                glowBackground.alpha = 0f
                glowBackground.background = null
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(partnersRecyclerView)

        FirebaseApp.initializeApp(this)
        fetchProfilesFromFirestore()
    }

    private fun fetchProfilesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid // Get current user's ID

        if (currentUserId != null) {
            // Step 1: Fetch all the partner IDs the current user has voted on
            db.collection("Matches")
                .whereEqualTo("userId", currentUserId) // Get matches where the current user is the user
                .get()
                .addOnSuccessListener { matchesResult ->
                    // Get the IDs of profiles the user has already voted on
                    val votedIds = matchesResult.documents.map { it.getString("partnerId") ?: "" }.toSet()

                    // Step 2: Fetch profiles, excluding the ones the user has voted on
                    db.collection("Perfiles")
                        .get()
                        .addOnSuccessListener { result ->
                            profiles.clear()

                            for (document in result) {
                                // If the document ID does not match the current user's ID or the voted IDs, add it to the list
                                if (document.id != currentUserId && !votedIds.contains(document.id)) {
                                    val partner = document.toObject(PartnerProfile::class.java).copy(id = document.id)
                                    profiles.add(partner)
                                }
                            }

                            if (profiles.isNotEmpty()) {
                                val randomProfile = profiles.random()  // Select a random profile
                                profiles.clear()
                                profiles.add(randomProfile)  // Add it to the list
                                partnerAdapter.notifyDataSetChanged()  // Update the adapter
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("FindTrainingPartners", "Error getting documents: ", exception)
                            Toast.makeText(this, "Error loading profiles: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w("FindTrainingPartners", "Error fetching matches: ", exception)
                    Toast.makeText(this, "Error loading matches: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_LONG).show()
        }
    }

}


