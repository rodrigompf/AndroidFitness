package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfitness.R
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class FindTrainingPartners : AppCompatActivity() {

    private lateinit var partnersRecyclerView: RecyclerView
    private lateinit var partnerAdapter: PartnerAdapter
    private val profiles = mutableListOf<PartnerProfile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_training_partners)

        partnersRecyclerView = findViewById(R.id.partnersRecyclerView)
        partnersRecyclerView.layoutManager = LinearLayoutManager(this)
        partnerAdapter = PartnerAdapter(profiles)
        partnersRecyclerView.adapter = partnerAdapter

        // Add ItemTouchHelper for swipe gestures
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Remove the swiped profile from the list
                val swipedProfile = profiles[viewHolder.adapterPosition]
                profiles.removeAt(viewHolder.adapterPosition)

                if (direction == ItemTouchHelper.RIGHT) {
                    // Like the profile
                    Log.d("Swipe", "Liked: ${swipedProfile.nome}")
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Dislike the profile
                    Log.d("Swipe", "Disliked: ${swipedProfile.nome}")
                }

                // Show the next random profile
                fetchProfilesFromFirestore()
            }

            // Override onChildDraw to create falling animation when swiping
            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val view = viewHolder.itemView
                val width = view.width

                // Calculate the tilt angle based on the swipe distance
                val maxTilt = 15f // Max tilt angle (degrees)
                val tiltFactor = (dX / width) * maxTilt

                // Apply the tilt to the view (rotation)
                view.rotation = tiltFactor

                // Make the item fade out based on swipe distance (for visual feedback)
                val alpha = 1 - Math.abs(dX) / width // Fade out based on swipe distance
                view.alpha = alpha

                // Apply the falling effect (move down slightly as it's being swiped)
                view.translationY = dY / 2 // Move vertically with the swipe

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(partnersRecyclerView)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Fetch profiles from Firestore
        fetchProfilesFromFirestore()
    }

    private fun fetchProfilesFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Perfiles")
            .get()
            .addOnSuccessListener { result ->
                profiles.clear() // Clear any existing profiles

                // Add the fetched profiles to the list
                for (document in result) {
                    val partner = document.toObject(PartnerProfile::class.java)
                    profiles.add(partner)
                }

                // Show only one random profile, make sure it's not the same as before
                if (profiles.isNotEmpty()) {
                    val randomProfile = profiles.random()  // Get a random profile
                    profiles.clear()  // Clear the existing list
                    profiles.add(randomProfile)  // Add the new profile
                    partnerAdapter.notifyDataSetChanged()  // Update the RecyclerView with the new profile
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FindTrainingPartners", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading profiles: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}



