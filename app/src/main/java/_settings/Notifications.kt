package _settings

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class Notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications)

        val notificationsSwitch = findViewById<Switch>(R.id.enableNotificationsSwitch)

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) {
                "Notifications Enabled"
            } else {
                "Notifications Disabled"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
