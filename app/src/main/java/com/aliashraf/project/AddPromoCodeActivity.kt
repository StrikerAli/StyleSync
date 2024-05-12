
package com.aliashraf.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddPromoCodeActivity : AppCompatActivity() {
    private lateinit var etPromoCodeName: EditText
    private lateinit var etDiscountPercentage: EditText
    private lateinit var btnAddPromoCode: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var promoCodesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promocode)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        promoCodesRef = database.getReference("promo_codes")

        // Find views by their IDs
        etPromoCodeName = findViewById(R.id.etPromoCodeName)
        etDiscountPercentage = findViewById(R.id.etDiscountPercentage)
        btnAddPromoCode = findViewById(R.id.btnAddPromoCode)

        // Set OnClickListener for btnAddPromoCode
        btnAddPromoCode.setOnClickListener {
            // Get promo code name and discount percentage from EditText fields
            val promoCodeName = etPromoCodeName.text.toString().trim()
            val discountPercentage = etDiscountPercentage.text.toString().trim()

            // Check if promo code name and discount percentage are not empty
            if (promoCodeName.isNotEmpty() && discountPercentage.isNotEmpty()) {
                // Create a PromoCode object
                val promoCode = PromoCode(promoCodeName, discountPercentage.toDouble())

                // Push the PromoCode object to Firebase Realtime Database
                promoCodesRef.child(promoCodeName).setValue(promoCode)

                // Finish the activity
                finish()
            }
        }
    }
}

data class PromoCode(
    val name: String = "",
    val discountPercentage: Double = 0.0
)
