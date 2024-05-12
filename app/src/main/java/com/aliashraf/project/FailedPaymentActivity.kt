package com.aliashraf.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FailedPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_failed)

        // Find the buttons and text views
        val btnTryAgain = findViewById<Button>(R.id.btnTryAgain)
        val txtBack = findViewById<TextView>(R.id.txtBack)

        // Set click listener for btnTryAgain
        btnTryAgain.setOnClickListener {
            // Navigate to CartActivity (replace CartActivity::class.java with your actual cart activity)
            val intent = Intent(this@FailedPaymentActivity, CartActivity::class.java)
            startActivity(intent)
            finish() // Optional: close this activity
        }

        // Set click listener for txtBack
        txtBack.setOnClickListener {
            // Navigate back to HomeActivity (replace HomeActivity::class.java with your actual home activity)
            val intent = Intent(this@FailedPaymentActivity, HomeActivity::class.java)
            startActivity(intent)
            finish() // Optional: close this activity
        }
    }
}

