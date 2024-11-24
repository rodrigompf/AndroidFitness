package _personalArea

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class PersonalArea : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_area)

        // Buttons for each action
        val changeThemeButton: Button = findViewById(R.id.changeThemeButton)
        val changeLanguageButton: Button = findViewById(R.id.changeLanguageButton)
        val changeEmailButton: Button = findViewById(R.id.changeEmailButton)
        val changePasswordButton: Button = findViewById(R.id.changePasswordButton)

        // Handle the Change Theme button click
        changeThemeButton.setOnClickListener {
            // Start the Change Theme Activity
            val intent = Intent(this, ChangeTheme::class.java)
            startActivity(intent)
        }

        // Handle the Change Language button click
        changeLanguageButton.setOnClickListener {
            // Start the Change Language Activity
            val intent = Intent(this, ChangeLanguage::class.java)
            startActivity(intent)
        }

        // Handle the Change Email button click
        changeEmailButton.setOnClickListener {
            // Start the Change Email Activity
            val intent = Intent(this, ChangeEmail::class.java)
            startActivity(intent)
        }

        // Handle the Change Password button click
        changePasswordButton.setOnClickListener {
            // Start the Change Password Activity
            val intent = Intent(this, ChangePassword::class.java)
            startActivity(intent)
        }
    }
}
