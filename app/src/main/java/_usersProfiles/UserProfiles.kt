package _usersProfiles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class UserProfiles : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profiles)

        val chatButton = findViewById<Button>(R.id.chatButton)
        val trainingCalendarButton = findViewById<Button>(R.id.trainingCalendarButton)
        val trainingPartnerProfileButton = findViewById<Button>(R.id.trainingPartnerProfileButton)

        chatButton.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        trainingCalendarButton.setOnClickListener {
            val intent = Intent(this, TrainingCalendar::class.java)
            startActivity(intent)
        }

        trainingPartnerProfileButton.setOnClickListener {
            val intent = Intent(this, TrainingPartnerProfile::class.java)
            startActivity(intent)
        }
    }
}
