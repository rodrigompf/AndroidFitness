package _settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import _personalArea.PersonalArea
import com.example.androidfitness.R

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        val notificationsButton = findViewById<Button>(R.id.notificationsButton)
        val contactSupportButton = findViewById<Button>(R.id.contactSupportButton)
        val personalAreaButton = findViewById<Button>(R.id.personalAreaButton)

        notificationsButton.setOnClickListener {
            val intent = Intent(this, Notifications::class.java)
            startActivity(intent)
        }

        contactSupportButton.setOnClickListener {
            val intent = Intent(this, ContactSupport::class.java)
            startActivity(intent)
        }

        personalAreaButton.setOnClickListener {
            val intent = Intent(this, PersonalArea::class.java)
            startActivity(intent)
        }
    }
}
