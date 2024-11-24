package _main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import _homeScreen.HomeScreen
import com.example.androidfitness.R
import com.google.firebase.auth.FirebaseAuth

class SignUp : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Back Arrow Click Listener
        val backArrow = findViewById<ImageView>(R.id.backArrow2)
        backArrow.setOnClickListener {
            // Navigate back to LoginActivity (replace with your login activity class)
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Close the current activity
        }

        mAuth = FirebaseAuth.getInstance()

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signUpButton = findViewById(R.id.signUpButton)

        // Handle sign-up action
        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                signUpUser(name, email, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User is successfully created
                    val user = mAuth.currentUser
                    Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()

                    // Optionally save user data to Firebase Realtime Database or Firestore
                    // FirebaseFirestore.getInstance().collection("users").document(user!!.uid).set(User(name, email))

                    // Navigate to HomeScreenActivity after registration
                    startActivity(Intent(this, HomeScreen::class.java))
                    finish()
                } else {
                    // If registration fails, display a message to the user
                    Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
