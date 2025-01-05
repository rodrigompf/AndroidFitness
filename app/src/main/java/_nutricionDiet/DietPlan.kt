package _nutricionDiet

import _nutricionDiet.database.DayPlanAdapter
import _nutricionDiet.database.DayPlanItem
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DietPlan : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance() // Firestore instance
    private lateinit var dietPlanAdapter: DayPlanAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diet_plan)

        // Initialize UI elements
        val editDietPlanButton = findViewById<Button>(R.id.editDietPlanButton)
        recyclerView = findViewById(R.id.dietPlanRecyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)  // Setting Layout Manager
        dietPlanAdapter = DayPlanAdapter(mutableListOf(), showDeleteButton = false) // Pass false to hide delete button
        recyclerView.adapter = dietPlanAdapter

        // Load the saved diet plan from Firestore
        loadDietPlan()

        // Set the button click listener to go to EditDietPlanMainActivity
        editDietPlanButton.setOnClickListener {
            val intent = Intent(this, EditDietPlanMainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadDietPlan() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure the user is authenticated

        // Reference to the user's diet plan in Firestore
        val dietPlanDocument = firestore.collection("Perfiles")
            .document(userId) // User document
            .collection("Diet") // Diet collection
            .document("Plan") // Diet Plan document

        // Set up a real-time listener for changes to the diet plan document
        dietPlanDocument.addSnapshotListener { documentSnapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, "Failed to load Diet Plan: ${exception.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val plansList = documentSnapshot.get("plans") as? List<Map<String, Any>> ?: return@addSnapshotListener

                val dietPlanItems = mutableListOf<DayPlanItem>()

                // Process each item in the "plans" list and add it to the dietPlanItems list
                for (plan in plansList) {
                    when (plan["type"]) {
                        "text" -> {
                            val content = plan["content"] as? String ?: ""
                            dietPlanItems.add(DayPlanItem.TextPlan(content))
                        }
                        "plan" -> {
                            val title = plan["title"] as? String ?: ""
                            val description = plan["description"] as? String ?: ""
                            val calories = plan["calories"] as? Int ?: 0
                            dietPlanItems.add(DayPlanItem.Plan(title, description, calories))
                        }
                    }
                }

                // Update the RecyclerView's adapter with the loaded data
                dietPlanAdapter.updateItems(dietPlanItems)
            }
        }
    }

}

