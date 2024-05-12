package com.aliashraf.project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class DeliveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        val btnSaveAddress = findViewById<Button>(R.id.btnSaveAddress)
        btnSaveAddress.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        val edtName = findViewById<EditText>(R.id.etAddressName)
        val edtAddressLine1 = findViewById<EditText>(R.id.etAddressLine1)
        val edtAddressLine2 = findViewById<EditText>(R.id.etAddressLine2)
        val edtCity = findViewById<EditText>(R.id.etCity)

        val name = edtName.text.toString().trim()
        val addressLine1 = edtAddressLine1.text.toString().trim()
        val addressLine2 = edtAddressLine2.text.toString().trim()
        val city = edtCity.text.toString().trim()

        // Send data to server using PHP script
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.18.32/save_address.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["addressLine1"] = addressLine1
                params["addressLine2"] = addressLine2
                params["city"] = city
                return params
            }
        }

        queue.add(stringRequest)
    }
}
