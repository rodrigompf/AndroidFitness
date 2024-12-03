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
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
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
        db = FirebaseFirestore.getInstance()

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
                if (password.length >= 6) {
                    signUpUser(name, email, password)
                } else {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        // Save user data to Firestore
                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email
                        )

                        db.collection("users").document(userId).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()

                                // Navigate to HomeScreen
                                val intent = Intent(this, HomeScreen::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Failed to get user ID.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Registration failed."
                    Toast.makeText(baseContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
