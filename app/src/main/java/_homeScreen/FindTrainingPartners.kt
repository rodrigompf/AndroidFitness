package _homeScreen

import _homeScreen.DataBase.PartnerProfile
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
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

        val rootLayout = findViewById<FrameLayout>(R.id.rootLayout)
        partnersRecyclerView = findViewById(R.id.partnersRecyclerView)
        partnersRecyclerView.layoutManager = LinearLayoutManager(this)

        partnerAdapter = PartnerAdapter(profiles) { partner ->

            Toast.makeText(this, "Clicked on: ${partner.nome}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PartnerDetailActivity::class.java)
            intent.putExtra("PARTNER_DATA", partner)
            startActivity(intent)
        }
        partnersRecyclerView.adapter = partnerAdapter

        // Add swipe gesture handling
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedProfile = profiles[viewHolder.adapterPosition]
                profiles.removeAt(viewHolder.adapterPosition)

                if (direction == ItemTouchHelper.RIGHT) {
                    // Handle like
                    Log.d("Swipe", "Liked: ${swipedProfile.nome}")
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Handle dislike
                    Log.d("Swipe", "Disliked: ${swipedProfile.nome}")
                }

                partnerAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                fetchRandomProfile()
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val view = viewHolder.itemView
                val width = rootLayout.width

                // Set background color and intensity based on swipe direction
                val glowColor = if (dX > 0) Color.argb((Math.abs(dX) / width * 255).toInt(), 255, 0, 0) // Red
                else Color.argb((Math.abs(dX) / width * 255).toInt(), 0, 255, 0) // Green

                rootLayout.setBackgroundColor(glowColor)

                // Apply tilt effect
                val maxTilt = 15f
                view.rotation = (dX / width) * maxTilt

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                val rootLayout = findViewById<FrameLayout>(R.id.rootLayout)
                rootLayout.setBackgroundColor(Color.TRANSPARENT) // Reset background color
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(partnersRecyclerView)

        // Fetch profiles
        fetchProfilesFromFirestore()
    }

    private fun fetchProfilesFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("Perfiles")
            .get()
            .addOnSuccessListener { result ->
                profiles.clear()

                for (document in result) {
                    val partner = document.toObject(PartnerProfile::class.java)
                    profiles.add(partner)
                }

                fetchRandomProfile() // Show a random profile
            }
            .addOnFailureListener { exception ->
                Log.w("FindTrainingPartners", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading profiles: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchRandomProfile() {
        if (profiles.isNotEmpty()) {
            val randomProfile = profiles.random()
            profiles.clear()
            profiles.add(randomProfile)
            partnerAdapter.notifyDataSetChanged()
        }
    }
}
