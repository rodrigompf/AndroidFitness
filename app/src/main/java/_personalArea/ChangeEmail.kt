package _personalArea

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.example.androidfitness.R

class ChangeEmail : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_email)

        mAuth = FirebaseAuth.getInstance()
        val newEmailField = findViewById<EditText>(R.id.newEmailField)
        val changeEmailButton = findViewById<Button>(R.id.changeEmailButton)

        changeEmailButton.setOnClickListener {
            val newEmail = newEmailField.text.toString().trim()
            if (newEmail.isNotEmpty()) {
                mAuth.currentUser?.updateEmail(newEmail)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Email updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update email.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
