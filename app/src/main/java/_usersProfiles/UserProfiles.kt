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

        val trainingCalendarButton = findViewById<Button>(R.id.trainingCalendarButton)
        val profileButton = findViewById<Button>(R.id.profileButton)

        trainingCalendarButton.setOnClickListener {
            val intent = Intent(this, TrainingCalendar::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
    }
}
