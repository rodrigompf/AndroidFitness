package _nutricionDiet

import _nutricionDiet.database.DayPlanAdapter
import _nutricionDiet.database.DayPlanItem
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditDietPlanMainActivity : AppCompatActivity() {

    private lateinit var dayPlanAdapter: DayPlanAdapter
    private lateinit var recyclerView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance() // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editdietplan_main)

        recyclerView = findViewById(R.id.dayPlanRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter
        dayPlanAdapter = DayPlanAdapter(mutableListOf(), showDeleteButton = true)

        recyclerView.adapter = dayPlanAdapter

        // Add Plan Button
        val addPlanButton = findViewById<Button>(R.id.addPlanButton)
        addPlanButton.setOnClickListener {
            showAddPlanDialog()
        }

        // Save Button
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveDietPlanToFirestore() // Save the data to Firestore
        }

        // Load existing Diet Plan from Firestore
        loadDietPlanFromFirestore()
    }

    private fun loadDietPlanFromFirestore() {
        // Get the current authenticated user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure user is authenticated

        // Reference to the "Plan" document inside the "Diet" collection
        val dietPlanDocument = firestore.collection("Perfiles")
            .document(userId) // User document
            .collection("Diet") // Diet collection
            .document("Plan") // Document named "Plan"

        // Fetch the document
        dietPlanDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Get the plans data from the "plans" field
                    val dietPlans = documentSnapshot.get("plans") as? List<Map<String, Any>> ?: emptyList()

                    // Convert the fetched data into DayPlanItem objects
                    val dayPlanItems = dietPlans.map { planData ->
                        when (planData["type"]) {
                            "text" -> DayPlanItem.TextPlan(planData["content"] as String)
                            "plan" -> DayPlanItem.Plan(
                                planData["title"] as String,
                                planData["description"] as String,
                                planData["calories"] as? Int ?: 0
                            )
                            else -> null
                        }
                    }.filterNotNull()

                    // Update the adapter with the fetched data
                    dayPlanAdapter.updateItems(dayPlanItems)
                } else {
                    Toast.makeText(this, "No saved diet plans found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load Diet Plan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun showAddPlanDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_add_plan, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val addTextPlanButton = dialogView.findViewById<Button>(R.id.addTextPlanButton)
        val addPlanButton = dialogView.findViewById<Button>(R.id.addPlanButton)

        // Add Text Plan
        addTextPlanButton.setOnClickListener {
            val textPlanDialogView = layoutInflater.inflate(R.layout.item_text_plan, null)
            val textPlanEditText = textPlanDialogView.findViewById<EditText>(R.id.textPlanEditText)

            AlertDialog.Builder(this)
                .setTitle("Add Text Plan")
                .setView(textPlanDialogView)
                .setPositiveButton("Add") { _, _ ->
                    val textContent = textPlanEditText.text.toString()
                    if (textContent.isNotBlank()) {
                        dayPlanAdapter.addItem(DayPlanItem.TextPlan(textContent))
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Text Plan cannot be empty!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Add Plan
        addPlanButton.setOnClickListener {
            val planDialogView = layoutInflater.inflate(R.layout.item_plan, null)
            val titleEditText = planDialogView.findViewById<EditText>(R.id.planTitleEditText)
            val descriptionEditText = planDialogView.findViewById<EditText>(R.id.planDescriptionEditText)
            val caloriesEditText = planDialogView.findViewById<EditText>(R.id.planCaloriesEditText)

            android.app.AlertDialog.Builder(this)
                .setTitle("Add Plan")
                .setView(planDialogView)
                .setPositiveButton("Add") { _, _ ->
                    val title = titleEditText.text.toString()
                    val description = descriptionEditText.text.toString()
                    val calories = caloriesEditText.text.toString().toIntOrNull() ?: 0

                    if (title.isNotBlank() && description.isNotBlank()) {
                        dayPlanAdapter.addItem(DayPlanItem.Plan(title, description, calories))
                        dialog.dismiss()
                    } else {
                        Toast.makeText(this, "Title and Description are required!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        dialog.show()
    }

    private fun saveDietPlanToFirestore() {
        // Get the current authenticated user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return // Ensure user is authenticated

        // Reference to the Firestore instance and the "Diet" collection path
        val dietPlanDocument = firestore.collection("Perfiles")
            .document(userId) // Reference to the user document
            .collection("Diet") // Reference to the Diet collection
            .document("Plan") // Document named "Plan"

        // Loop through the items and create plan data
        val dietPlanData: List<Map<String, Any>> = dayPlanAdapter.getItems().map { item ->
            when (item) {
                is DayPlanItem.TextPlan -> mapOf(
                    "type" to "text",
                    "content" to item.content // Correct property for content
                )

                is DayPlanItem.Plan -> mapOf(
                    "type" to "plan",
                    "title" to item.title,
                    "description" to item.description,
                    "calories" to item.calories
                )
            }
        }

        // Save the diet plans data inside the "Plan" document as a field
        dietPlanDocument.set(mapOf("plans" to dietPlanData))
            .addOnSuccessListener {
                Toast.makeText(this, "Diet Plan saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save Diet Plan: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


}
