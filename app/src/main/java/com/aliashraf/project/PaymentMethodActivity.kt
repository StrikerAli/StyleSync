package com.aliashraf.project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class PaymentMethodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_method)

        val etCardNumber = findViewById<EditText>(R.id.etCardNumber)
        val txt12twelve = findViewById<EditText>(R.id.txt12twelve)
        val etSecurityCode = findViewById<EditText>(R.id.etSecurityCode)
        val etCardHolder = findViewById<EditText>(R.id.etCardHolder)
        val btnUseThisCard = findViewById<Button>(R.id.btnUseThisCard)

        btnUseThisCard.setOnClickListener {
            // Get card information
            val cardNumber = etCardNumber.text.toString()
            val expiryDate = txt12twelve.text.toString()
            val securityCode = etSecurityCode.text.toString()
            val cardHolder = etCardHolder.text.toString()

            // Send HTTP POST request to PHP script
            insertCardInfo(cardNumber, expiryDate, securityCode, cardHolder)
        }
    }

    private fun insertCardInfo(card_number: String, expiry_date: String, security_code: String, card_holder_name: String) {
        val url = "http://192.168.18.32/insert_card_info.php?card_number=$card_number&expiry_date=$expiry_date&security_code=$security_code&card_holder_name=$card_holder_name"
        url.replace(" ", "_")
        Log.d("JSON", url)

        // Request a string response from the provided URL
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                try {
                    val obj = JSONObject(response)
                    val success = obj.getBoolean("success")
                    val message = obj.getString("message")
                    if (success) {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                val errorMessage = error.message ?: "Unknown error occurred"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(stringRequest)
    }
}

