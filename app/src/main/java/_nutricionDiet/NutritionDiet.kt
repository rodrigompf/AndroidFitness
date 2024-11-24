package _nutricionDiet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class NutritionDiet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nutrition_diet)

        val imcCalculatorButton = findViewById<Button>(R.id.imcCalculatorButton)
        val dietPlanButton = findViewById<Button>(R.id.dietPlanButton)
        val nutritionismTipsButton = findViewById<Button>(R.id.nutritionismTipsButton)
        val nutritionistConsultButton = findViewById<Button>(R.id.nutritionistConsultButton)

        imcCalculatorButton.setOnClickListener {
            val intent = Intent(this, IMCCalculator::class.java)
            startActivity(intent)
        }

        dietPlanButton.setOnClickListener {
            val intent = Intent(this, DietPlan::class.java)
            startActivity(intent)
        }

        nutritionismTipsButton.setOnClickListener {
            val intent = Intent(this, NutritionismTips::class.java)
            startActivity(intent)
        }

        nutritionistConsultButton.setOnClickListener {
            val intent = Intent(this, NutritionistConsult::class.java)
            startActivity(intent)
        }
    }
}
