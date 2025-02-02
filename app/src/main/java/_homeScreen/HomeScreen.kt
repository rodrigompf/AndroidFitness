package _homeScreen

import _main.Login
import _nutricionDiet.NutritionDiet
import _settings.Settings
import _usersProfiles.UserProfiles
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class HomeScreen : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var findPartnersButton: Button
    private lateinit var exploreActivitiesButton: Button
    private lateinit var healthTipsButton: Button
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Set up Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Views
        findPartnersButton = findViewById(R.id.findPartnersButton)
        exploreActivitiesButton = findViewById(R.id.exploreActivitiesButton)
        healthTipsButton = findViewById(R.id.healthTipsButton)
        welcomeText = findViewById(R.id.welcomeText)

        val userProfilesButton = findViewById<Button>(R.id.usersProfileButton)
        val nutritionDietButton = findViewById<Button>(R.id.nutritionDietButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val chatButton = findViewById<Button>(R.id.chatButton)

        // Set welcome message
        val currentUser = mAuth.currentUser
        welcomeText.text = "Welcome, ${currentUser?.email ?: "User"}!"

        // Navigation to features
        findPartnersButton.setOnClickListener {
            val intent = Intent(this, FindTrainingPartners::class.java)
            startActivity(intent)
        }

        chatButton.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        exploreActivitiesButton.setOnClickListener {
            val intent = Intent(this, ExploreActivities::class.java)
            startActivity(intent)
        }

        healthTipsButton.setOnClickListener {
            val intent = Intent(this, TrainingHealthTips::class.java)
            startActivity(intent)
        }

        userProfilesButton.setOnClickListener {
            val intent = Intent(this, UserProfiles::class.java)
            startActivity(intent)
        }

        nutritionDietButton.setOnClickListener {
            val intent = Intent(this, NutritionDiet::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.menu_home_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Handle logout
                mAuth.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish() // Close the HomeScreenActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
