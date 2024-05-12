// AdminDashboardActivity.kt

package com.aliashraf.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Find buttons by their IDs
        val btnAddPromoCode = findViewById<Button>(R.id.btnAddPromoCode)
        val btnAddProduct = findViewById<Button>(R.id.btnAddProduct)

        // Set click listeners for buttons
        btnAddPromoCode.setOnClickListener {
            // Open AddPromoCodeActivity
            val intent = Intent(this, AddPromoCodeActivity::class.java)
            startActivity(intent)
        }

        btnAddProduct.setOnClickListener {
            // Open AddProductActivity
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }
    }
}
