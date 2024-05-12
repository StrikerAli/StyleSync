package com.aliashraf.project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class SuccessfulPaymentActivity : AppCompatActivity() {

    // Notification channel ID
    private val CHANNEL_ID = "Successful_Payment_Channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_succesful)

        // Retrieve the order number passed from the CheckoutActivity
        val orderNumber = intent.getStringExtra("orderNumber")

        // Display the order number in a TextView
        val orderNumberTextView = findViewById<TextView>(R.id.txtDescription)
        orderNumberTextView.text = "Your order number: $orderNumber"

        // Create notification channel if Android version is Oreo or higher
        createNotificationChannel()

        // Display notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_add_shopping_cart_24)
            .setContentTitle("Order Success")
            .setContentText("Order number $orderNumber has been made successfully.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@SuccessfulPaymentActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, notification)
        }

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

    // Create notification channel if Android version is Oreo or higher
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Order Notifications"
            val descriptionText = "Notification for successful orders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}


