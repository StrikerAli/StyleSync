package com.aliashraf.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SuccessfulPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_succesful)

        // Retrieve the order number passed from the CheckoutActivity
        val orderNumber = intent.getStringExtra("orderNumber")

        // Display the order number in a TextView
        val orderNumberTextView = findViewById<TextView>(R.id.txtDescription)
        orderNumberTextView.text = "Your order number: $orderNumber"

        // Find the buttons and text views
        val btnContinue = findViewById<Button>(R.id.btnContinue)
        val txtBack = findViewById<TextView>(R.id.txtBack)

        // Set click listener for btnContinue
        btnContinue.setOnClickListener {
            // Navigate to the Homepage (replace HomepageActivity::class.java with your actual homepage activity)
            val intent = Intent(this@SuccessfulPaymentActivity, HomeActivity::class.java)
            startActivity(intent)
            finish() // Optional: close this activity
        }

        // Set click listener for txtBack
        txtBack.setOnClickListener {
            // Navigate to MainActivity
            val intent = Intent(this@SuccessfulPaymentActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Optional: close this activity
        }
    }
}

