package _personalArea

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R
import java.util.*

class ChangeLanguage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_language)

        val languageSpinner = findViewById<Spinner>(R.id.languageSpinner)
        val applyLanguageButton = findViewById<Button>(R.id.applyLanguageButton)

        applyLanguageButton.setOnClickListener {
            val selectedLanguage = languageSpinner.selectedItem.toString()
            val locale = when (selectedLanguage) {
                "Spanish" -> Locale("es")
                "French" -> Locale("fr")
                else -> Locale("en")
            }

            val config = Configuration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)

            recreate() // Restart activity to apply the language
        }
    }
}
