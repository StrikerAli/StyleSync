package com.aliashraf.project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.ImageView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CheckoutActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var selectedPromoCode: String? = null
    private val auth = FirebaseAuth.getInstance()
    private val usersDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    private var orderPlaced = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout)



        val totalPrice = intent.getStringExtra("totalPrice")
        findViewById<TextView>(R.id.txtRp350000).text = "Rs $totalPrice"

        val imageArrowrightOne = findViewById<ImageView>(R.id.imageArrowrightOne)
        val txtSelectmethod = findViewById<TextView>(R.id.txtSelectmethod)

        imageArrowrightOne.setOnClickListener {
            loadAddressEntries()
        }

        txtSelectmethod.setOnClickListener {
            loadAddressEntries()
        }

        val discountArrow = findViewById<ImageView>(R.id.discount_arrow)
        discountArrow.setOnClickListener {
            showPromoCodesDialog()
        }

        val discountText = findViewById<TextView>(R.id.txtPickdiscount)
        discountText.setOnClickListener {
            showPromoCodesDialog()
        }

        val imageImage: ImageView = findViewById(R.id.imageImage)
        imageImage.setOnClickListener {
            loadCardEntries()
        }

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference.child("promo_codes")


        val btnPlaceOrder = findViewById<Button>(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            placeOrderWithTimeout()
        }
    }

    private fun loadAddressEntries() {
        Thread {
            try {
                val PHP_SCRIPT_URL = "http://192.168.18.32/get_addresses.php" // Change to your PHP script URL
                val url = URL(PHP_SCRIPT_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                Log.d("JSON", "Response: $response")

                reader.close()
                conn.disconnect()
                processJsonResponse1(response.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CheckoutActivity, "Failed to fetch address entries", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun processJsonResponse1(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)
            val addressEntries = StringBuilder()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val cardNumber = jsonObject.getString("name")
                val expiryDate = jsonObject.getString("address_line1")
                val securityCode = jsonObject.getString("address_line2")
                val cardHolderName = jsonObject.getString("city")
                addressEntries.append("Address: ").append(cardNumber).append("\n")
                addressEntries.append("Line1: ").append(expiryDate).append("\n")
                addressEntries.append("Line2: ").append(securityCode).append("\n")
                addressEntries.append("City: ").append(cardHolderName).append("\n\n")
            }
            runOnUiThread {
                showAddressEntriesDialog(addressEntries.toString())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showAddressEntriesDialog(addressEntries: String) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Card Entries")

            // Inflate custom layout for the dialog
            val customLayout = layoutInflater.inflate(R.layout.dialog_card_entries, null)
            builder.setView(customLayout)

            // Get the ListView from the custom layout
            val listView = customLayout.findViewById<ListView>(R.id.listView_card_entries)

            // Set adapter for the ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, addressEntries.split("\n\n"))
            listView.adapter = adapter

            // Set item click listener for the ListView
            listView.setOnItemClickListener { _, _, position, _ ->
                val cardEntry = addressEntries.split("\n\n")[position]
                val cardHolderName = cardEntry.substringAfter("Address: ")
                Toast.makeText(this@CheckoutActivity, "$cardHolderName Selected", Toast.LENGTH_SHORT).show()
            }

            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
        }
    private fun loadCardEntries() {
        Thread {
            try {
                val PHP_SCRIPT_URL = "http://192.168.18.32/get_cards.php" // Change to your PHP script URL
                val url = URL(PHP_SCRIPT_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                conn.disconnect()
                processJsonResponse(response.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CheckoutActivity, "Failed to fetch card entries", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun processJsonResponse(jsonResponse: String) {
        try {
            val jsonArray = JSONArray(jsonResponse)
            val cardEntries = StringBuilder()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val cardNumber = jsonObject.getString("card_number")
                val expiryDate = jsonObject.getString("expiry_date")
                val securityCode = jsonObject.getString("security_code")
                val cardHolderName = jsonObject.getString("card_holder_name")
                cardEntries.append("Card Number: ").append(cardNumber).append("\n")
                cardEntries.append("Expiry Date: ").append(expiryDate).append("\n")
                cardEntries.append("Security Code: ").append(securityCode).append("\n")
                cardEntries.append("Card Holder Name: ").append(cardHolderName).append("\n\n")
            }
            runOnUiThread {
                showCardEntriesDialog(cardEntries.toString())
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showCardEntriesDialog(cardEntries: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Card Entries")

        // Inflate custom layout for the dialog
        val customLayout = layoutInflater.inflate(R.layout.dialog_card_entries, null)
        builder.setView(customLayout)

        // Get the ListView from the custom layout
        val listView = customLayout.findViewById<ListView>(R.id.listView_card_entries)

        // Set adapter for the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cardEntries.split("\n\n"))
        listView.adapter = adapter

        // Set item click listener for the ListView
        listView.setOnItemClickListener { _, _, position, _ ->
            val cardEntry = cardEntries.split("\n\n")[position]
            val cardHolderName = cardEntry.substringAfter("Card Holder Name: ")
            Toast.makeText(this@CheckoutActivity, "$cardHolderName's Card Selected", Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }


    private fun placeOrderWithTimeout() {
        placeOrder() // Start placing the order

        // Set a timeout for order placement
        val timeoutHandler = Handler()
        timeoutHandler.postDelayed({
            // Check if the order has been successfully placed
            if (!orderPlaced) {
                redirectToFailedPaymentActivity()
            }
        }, TIMEOUT_DELAY_MILLIS)
    }

    private fun redirectToFailedPaymentActivity() {
        val intent = Intent(this@CheckoutActivity, FailedPaymentActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun placeOrder() {
        val userEmail = auth.currentUser?.email
        userEmail?.let { email ->
            // Retrieve user information from "users" table based on email
            usersDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            // Get user's name
                            val userName = userSnapshot.child("name").value.toString()
                            // Generate a random order number
                            val orderNumber = (100000..999999).random().toString()

                            // Get the total price from TextView
                            val totalPriceTextView = findViewById<TextView>(R.id.txtRp350000).text.toString()
                            val totalPrice = extractTotalPrice(totalPriceTextView)

                            // Add the order information to the Firebase Realtime Database
                            val orderData = HashMap<String, Any>()
                            orderData["name"] = userName
                            orderData["email"] = email
                            orderData["orderNumber"] = orderNumber
                            orderData["totalPrice"] = totalPrice
                            // Add other order details as needed
                            val ordersDatabase = FirebaseDatabase.getInstance().reference.child("orders")
                            ordersDatabase.child(orderNumber).setValue(orderData)
                                .addOnSuccessListener {
                                    // Order placed successfully
                                    orderPlaced = true
                                    // Start SuccessfulPaymentActivity and pass the order number
                                    val intent = Intent(this@CheckoutActivity, SuccessfulPaymentActivity::class.java)
                                    intent.putExtra("orderNumber", orderNumber)
                                    startActivity(intent)
                                    finish()

                                    // Remove the selected promo code
                                    selectedPromoCode?.let { promoCode ->
                                        database.orderByChild("name").equalTo(promoCode).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(promoSnapshot: DataSnapshot) {
                                                for (promoCodeSnapshot in promoSnapshot.children) {
                                                    promoCodeSnapshot.ref.removeValue()
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                Log.e("Firebase", "Failed to delete promo code: ${error.message}")
                                            }
                                        })
                                    }
                                }
                                .addOnFailureListener {
                                    // Failed to place the order
                                    // Navigate to FailedPaymentActivity
                                    val intent = Intent(this@CheckoutActivity, FailedPaymentActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                        }
                    } else {
                        // User not found
                        Toast.makeText(this@CheckoutActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    Toast.makeText(this@CheckoutActivity, "Failed to retrieve user information", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun extractTotalPrice(totalPriceText: String): Double {
        return if (totalPriceText.contains("Rs")) {
            // If the text contains the format "Rs. originalPrice - discount% = discountedPrice"
            if (totalPriceText.contains("-") && totalPriceText.contains("%")) {
                val parts = totalPriceText.split("=")
                Log.d("JSON", "parts: $parts")
                val discountedPricePart = parts.lastOrNull()?.trim()?.removePrefix("Rs.")?.trim()?.toDoubleOrNull()
                Log.d("JSON", "discountedPricePart: $discountedPricePart")
                discountedPricePart ?: 0.0
            } else {
                // If the text contains only the original price
                val parts = totalPriceText.split("Rs")
                parts.lastOrNull()?.trim()?.toDoubleOrNull() ?: 0.0
            }
        } else {
            totalPriceText.trim().removePrefix("Rs").toDoubleOrNull() ?: 0.0
        }
    }

    private fun showPromoCodesDialog() {
        val promoCodesDialog = AlertDialog.Builder(this)
        promoCodesDialog.setTitle("Promo Codes")

        // Retrieve promo codes from Firebase
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val promoCodesList = mutableListOf<String>()
                val discountMap = mutableMapOf<String, Int>() // Map to store promo code name and discount percentage

                for (codeSnapshot in dataSnapshot.children) {
                    val promoCodeName = codeSnapshot.child("name").value.toString()
                    val discountPercentage = codeSnapshot.child("discount_percentage").value as Long
                    if (promoCodeName.isNotBlank()) {
                        promoCodesList.add(promoCodeName)
                        discountMap[promoCodeName] = discountPercentage.toInt()
                    }
                }

                promoCodesDialog.setItems(promoCodesList.toTypedArray()) { _, which ->
                    selectedPromoCode = promoCodesList[which]
                    findViewById<TextView>(R.id.txtPickdiscount).text = selectedPromoCode

                    // Show toast message with the selected promo code name
                    Toast.makeText(applicationContext, "Promo code selected: $selectedPromoCode", Toast.LENGTH_SHORT).show()

                    // Get the total price from intent extra
                    val totalPrice = intent.getStringExtra("totalPrice")?.toDoubleOrNull() ?: 0.0

                    // Calculate discounted price
                    val discountPercentage = discountMap[selectedPromoCode ?: ""] ?: 0
                    val discountAmount = totalPrice * (discountPercentage / 100.0)
                    val discountedPrice = totalPrice - discountAmount

                    // Update TextView with the discounted price
                    val txtRp350000 = findViewById<TextView>(R.id.txtRp350000)
                    val layoutParams = txtRp350000.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.marginStart = resources.getDimensionPixelSize(R.dimen._50pxh)
                    txtRp350000.layoutParams = layoutParams

                    val discountText = "Rs. $totalPrice - $discountPercentage% = Rs. $discountedPrice"
                    txtRp350000.text = discountText
                }
                promoCodesDialog.show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }

    companion object {
        private const val TIMEOUT_DELAY_MILLIS = 10000L // Timeout delay in milliseconds
    }
}







