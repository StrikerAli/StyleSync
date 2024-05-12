// AddProductActivity.kt

package com.aliashraf.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddProductActivity : AppCompatActivity() {
    private lateinit var etProductName: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductId: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var btnUploadImage: Button
    private lateinit var btnAddProduct: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var productsRef: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addproduct)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        productsRef = database.getReference("products")

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Find views by their IDs
        etProductName = findViewById(R.id.etProductName)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductId = findViewById(R.id.etProductId)
        etProductPrice = findViewById(R.id.etProductPrice)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        btnAddProduct = findViewById(R.id.btnAddProduct)

        // Set OnClickListener for btnUploadImage
        btnUploadImage.setOnClickListener {
            // Open image picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
        }

        // Set OnClickListener for btnAddProduct
        btnAddProduct.setOnClickListener {
            // Get product data from EditText fields
            val productName = etProductName.text.toString().trim()
            val productDescription = etProductDescription.text.toString().trim()
            val productId = etProductId.text.toString().trim()
            val productPrice = etProductPrice.text.toString().trim()

            // Check if all fields are filled
            if (productName.isNotEmpty() && productDescription.isNotEmpty() &&
                productId.isNotEmpty() && productPrice.isNotEmpty() && selectedImageUri != null) {

                // Upload image to Firebase Storage
                val imageRef = storageRef.child("product_images/${System.currentTimeMillis()}")
                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image uploaded successfully, get download URL
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Image URL retrieved, create Product object with image URL
                            val productImageUrl = uri.toString()
                            val product = Product(
                                id = productId.toInt(),
                                name = productName,
                                description = productDescription,
                                price = productPrice.toDouble(),
                                productImageUrl = productImageUrl
                            )

                            // Store Product object in Firebase Realtime Database
                            productsRef.child(productId).setValue(product)
                                .addOnSuccessListener {
                                    // Product added successfully
                                    Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                                    // Finish the activity
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    // Error occurred while adding product
                                    Toast.makeText(this, "Failed to add product: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Error occurred while uploading image
                        Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            } else {
                // Display a toast message if any field is empty
                Toast.makeText(this, "Please fill in all fields and upload an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the image URI
            selectedImageUri = data.data
        }
    }

    companion object {
        private const val IMAGE_PICK_REQUEST_CODE = 100
    }
}
