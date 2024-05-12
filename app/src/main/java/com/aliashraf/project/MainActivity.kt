package com.aliashraf.project

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogIn: Button
    private lateinit var accountCreateButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var btnGuest: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()


        // Find views by their IDs
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogIn = findViewById(R.id.btnLogIn)
        accountCreateButton = findViewById(R.id.account_create_button)

        btnGuest = findViewById(R.id.btnGuest)
        // Set OnClickListener for btnGuest
        btnGuest.setOnClickListener {
            // Create an Intent to start the HomeActivity
            val intent = Intent(this, GuestHomeActivity::class.java)

            // Start the HomeActivity
            startActivity(intent)
        }
        // Set OnClickListener for btnLogIn
        btnLogIn.setOnClickListener {
            // Get email and password from EditText fields
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Check if email and password are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Sign in with email and password using Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Authentication successful
                            // Display a toast message
                            Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT)
                                .show()
                            // Create an Intent to start the HomeActivity

                            if (email.endsWith("@admin.com")) {
                                // Open AdminDashboardActivity
                                Log.d("TAG", "onCreate: ${email}")
                                val intent2 = Intent(this, AdminDashboardActivity::class.java)
                                startActivity(intent2)
                            }
                            else {
                                val intent = Intent(this, HomeActivity::class.java)
                                // Start the HomeActivity
                                startActivity(intent)
                            }
                        }
                         else {
                            // Authentication failed
                            // Display a toast message
                            Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Display a toast message if email or password is empty
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        accountCreateButton.setOnClickListener {
            // Create an Intent to start the RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)

            // Start the RegisterActivity
            startActivity(intent)
        }
    }
}
