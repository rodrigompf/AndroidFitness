package _personalArea

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R
import com.google.firebase.auth.EmailAuthProvider

class ChangePassword : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        mAuth = FirebaseAuth.getInstance()
        val currentPasswordField = findViewById<EditText>(R.id.currentPasswordField)
        val newPasswordField = findViewById<EditText>(R.id.newPasswordField)
        val confirmNewPasswordField = findViewById<EditText>(R.id.confirmNewPasswordField)
        val changePasswordButton = findViewById<Button>(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordField.text.toString().trim()
            val newPassword = newPasswordField.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordField.text.toString().trim()

            // Basic validation
            if (currentPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your current password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter a new password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proceed with updating the password
            val user = mAuth.currentUser

            // If the user is authenticated, proceed to update the password
            if (user != null) {
                // Re-authenticate the user with the current password before updating the password
                val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Successfully re-authenticated, now update the password
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { passwordUpdateTask ->
                                if (passwordUpdateTask.isSuccessful) {
                                    Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                                    finish()  // Close the activity after success
                                } else {
                                    Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Authentication failed. Check your current password.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
