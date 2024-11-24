package _nutricionDiet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class IMCCalculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.imc_calculator)

        val weightInput = findViewById<EditText>(R.id.weightInput)
        val heightInput = findViewById<EditText>(R.id.heightInput)
        val calculateButton = findViewById<Button>(R.id.calculateImcButton)
        val resultText = findViewById<TextView>(R.id.imcResultText)

        calculateButton.setOnClickListener {
            val weightText = weightInput.text.toString()
            val heightText = heightInput.text.toString()

            if (weightText.isEmpty() || heightText.isEmpty()) {
                Toast.makeText(this, "Please enter both weight and height.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightText.toFloatOrNull()
            val height = heightText.toFloatOrNull()

            if (weight == null || height == null || height <= 0) {
                Toast.makeText(this, "Invalid input. Please enter valid numbers.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imc = weight / (height * height)
            val imcCategory = when {
                imc < 18.5 -> "Underweight"
                imc in 18.5..24.9 -> "Normal weight"
                imc in 25.0..29.9 -> "Overweight"
                else -> "Obesity"
            }

            resultText.text = "Your IMC: %.2f (%s)".format(imc, imcCategory)
        }
    }
}
