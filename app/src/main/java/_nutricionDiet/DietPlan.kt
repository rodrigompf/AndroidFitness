package _nutricionDiet

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class DietPlan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.diet_plan)

        val editDietPlanButton = findViewById<Button>(R.id.editDietPlanButton)

        editDietPlanButton.setOnClickListener {
            // For now, just show a toast. Later, this can open an editor or fetch/update the plan from a server.
            Toast.makeText(this, "Edit Diet Plan clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}
