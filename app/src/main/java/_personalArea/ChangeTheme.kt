package _personalArea

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.androidfitness.R

class ChangeTheme : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_theme)

        val themeRadioGroup = findViewById<RadioGroup>(R.id.themeRadioGroup)
        val applyThemeButton = findViewById<Button>(R.id.applyThemeButton)

        // Load saved theme
        sharedPreferences = getSharedPreferences("ThemePreferences", MODE_PRIVATE)
        val savedTheme = sharedPreferences.getString("theme", "light")
        if (savedTheme == "dark") {
            themeRadioGroup.check(R.id.darkThemeOption)
        } else {
            themeRadioGroup.check(R.id.lightThemeOption)
        }

        applyThemeButton.setOnClickListener {
            val selectedTheme =
                if (themeRadioGroup.checkedRadioButtonId == R.id.darkThemeOption) "dark" else "light"

            // Apply theme
            when (selectedTheme) {
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Save theme preference
            with(sharedPreferences.edit()) {
                putString("theme", selectedTheme)
                apply()
            }
        }
    }
}
