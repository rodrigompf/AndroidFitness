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
            intent.putExtra("PARTNER_DATA", partner)
            startActivity(intent)
        }
        partnersRecyclerView.adapter = partnerAdapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val swipedPosition = viewHolder.adapterPosition
                profiles.removeAt(swipedPosition)
                partnerAdapter.notifyItemRemoved(swipedPosition)

                if (profiles.isEmpty()) {
                    fetchProfilesFromFirestore() // Refill profiles if needed
                }
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val width = recyclerView.width
                val view = viewHolder.itemView

                // Apply the translation and tilt effect
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

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
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

        db.collection("Perfiles")
            .get()
            .addOnSuccessListener { result ->
                profiles.clear()

                for (document in result) {
                    val partner = document.toObject(PartnerProfile::class.java)
                    profiles.add(partner)
                }

                if (profiles.isNotEmpty()) {
                    val randomProfile = profiles.random()
                    profiles.clear()
                    profiles.add(randomProfile)
                    partnerAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("FindTrainingPartners", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading profiles: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
