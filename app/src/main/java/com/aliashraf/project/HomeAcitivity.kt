package com.aliashraf.project

import ActivitySidebar
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter1: FirebaseRecyclerAdapter<Product, ViewHolder1>
    private lateinit var adapter2: FirebaseRecyclerAdapter<Product, ViewHolder2>
    private lateinit var sidebarLayout: View
    private lateinit var database: DatabaseReference


    // Firebase Authentication instance
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tab_container)


        // Initialize RecyclerViews


        recyclerView1 = findViewById(R.id.recyclerView1)
        recyclerView2 = findViewById(R.id.recyclerView2)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Set layout managers for RecyclerViews
        recyclerView1.layoutManager = LinearLayoutManager(this)
        recyclerView2.layoutManager = LinearLayoutManager(this)

        sidebarLayout = layoutInflater.inflate(R.layout.activity_sidebar, null)
        // Add sidebar layout to the root view of the activity
        addContentView(
            sidebarLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        sidebarLayout.visibility = View.GONE


        // Set up click listener for imageThumbsupThree to toggle sidebar visibility
        findViewById<View>(R.id.imageThumbsupThree).setOnClickListener {
            toggleSidebarVisibility()
        }
        findViewById<View>(R.id.imageCloseOne).setOnClickListener {
            toggleSidebarVisibility()
        }
        findViewById<View>(R.id.txtPromocord).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        findViewById<View>(R.id.imageTelevision).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.txtPaymentmethods).setOnClickListener {
            // Start PaymentMethodActivity
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }
        findViewById<ImageView>(R.id.imageVectoriconOne).setOnClickListener {
            // Start PaymentMethodActivity
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }


        findViewById<TextView>(R.id.txtDelivery).setOnClickListener {
            // Start PaymentMethodActivity
            startActivity(Intent(this, DeliveryActivity::class.java))
        }
        findViewById<ImageView>(R.id.imageLinkedinOne).setOnClickListener {
            // Start PaymentMethodActivity
            startActivity(Intent(this, DeliveryActivity::class.java))
        }



        // Set up adapters
        val options1 = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(database.child("products"), Product::class.java)
            .build()

        adapter1 = object : FirebaseRecyclerAdapter<Product, ViewHolder1>(options1) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder1 {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile, parent, false)
                return ViewHolder1(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder1, position: Int, model: Product) {
                viewHolder.bind(model)
            }
        }

        val options2 = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(database.child("products"), Product::class.java)
            .build()

        adapter2 = object : FirebaseRecyclerAdapter<Product, ViewHolder2>(options2) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder2 {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile, parent, false)
                return ViewHolder2(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder2, position: Int, model: Product) {
                viewHolder.bind(model)
                // Add click listener for "Add to Cart" button
                viewHolder.addToCartButton.setOnClickListener {
                    // Add product to cart
                    addToCart(model)
                }
                // Add click listener for "Add to Favorites" button
                viewHolder.addToFavoritesButton.setOnClickListener {
                    // Add product to favorites
                    addToFavorites(model)
                }
            }
        }

        // Set adapters to RecyclerViews
        recyclerView1.adapter = adapter1
        recyclerView2.adapter = adapter2

        // Set current user's name to the "Name" TextView
        setCurrentUserName()
    }

    private fun toggleSidebarVisibility() {
        if (sidebarLayout.visibility == View.VISIBLE) {
            sidebarLayout.visibility = View.GONE
        } else {
            sidebarLayout.visibility = View.VISIBLE
        }
        // Get current user
        val currentUser = auth.currentUser
        currentUser?.let {
            // Get user's email
            val userEmail = it.email
            // Query the "users" table to fetch the name based on email
            val usersRef = FirebaseDatabase.getInstance().reference.child("users")
            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if user exists
                    if (dataSnapshot.exists()) {
                        // Get the first child as there should be only one match
                        val userSnapshot = dataSnapshot.children.first()
                        // Get user's name
                        val userName = userSnapshot.child("name").getValue(String::class.java)
                        // Set user's name to the "Name" TextView
                        findViewById<TextView>(R.id.txtFullname)?.text = userName
                        findViewById<TextView>(R.id.txtEmail)?.text = userEmail
                        if (userName != null) {
                            Log.d("Sidebar", userName)
                        }
                            if (userEmail != null) {
                                Log.d("Sidebar", userEmail)
                        }


                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }    }


    override fun onStart() {
        super.onStart()
        adapter1.startListening()
        adapter2.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter1.stopListening()
        adapter2.stopListening()
    }

    // Function to set current user's name to the "Name" TextView
    private fun setCurrentUserName() {
        // Get current user
        val currentUser = auth.currentUser
        currentUser?.let {
            // Get user's email
            val userEmail = it.email
            // Query the "users" table to fetch the name based on email
            val usersRef = FirebaseDatabase.getInstance().reference.child("users")
            usersRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if user exists
                    if (dataSnapshot.exists()) {
                        // Get the first child as there should be only one match
                        val userSnapshot = dataSnapshot.children.first()
                        // Get user's name
                        val userName = userSnapshot.child("name").getValue(String::class.java)
                        // Set user's name to the "Name" TextView
                        findViewById<TextView>(R.id.Name)?.text = "Hello: ${userName}"
                        findViewById<TextView>(R.id.txtFullname)?.text = "Hello: ${userName}"
                        findViewById<TextView>(R.id.txtEmail)?.text = "Email: ${userEmail}"

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }

    // Function to add product to cart
    private fun addToCart(product: Product) {
        database.child("cart").child(product.id.toString()).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add to Cart", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to add product to favorites
    private fun addToFavorites(product: Product) {
        database.child("favorites").child(product.id.toString()).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add to Favorites", Toast.LENGTH_SHORT).show()
            }
    }
}

class ViewHolder1(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val productNameTextView: TextView = itemView.findViewById(R.id.textViewName)
    private val productPriceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
    private val productDescriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
    private val productImageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
    private val addToCartButton: ImageView = itemView.findViewById(R.id.cart)
    private val addToFavoritesButton: ImageView = itemView.findViewById(R.id.favourite)

    fun bind(product: Product) {
        productNameTextView.text = product.name
        productDescriptionTextView.text = product.description
        productPriceTextView.text = "Price: Rs ${product.price}"
        Glide.with(itemView).load(product.productImageUrl).into(productImageView)

        // Set click listener for "Add to Cart" button
        addToCartButton.setOnClickListener {
            showAddToCartDialog(product)
        }

        // Set click listener for "Add to Favorites" button
        addToFavoritesButton.setOnClickListener {
            // Add product to favorites
            addToFavorites(product)
        }

    }

    private fun showAddToCartDialog(product: Product) {
        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_add_to_cart, null)
        val editTextQuantity: EditText = dialogView.findViewById(R.id.editTextQuantity)
        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonAddToCart: Button = dialogView.findViewById(R.id.buttonAddToCart)

        val dialog = AlertDialog.Builder(itemView.context)
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonAddToCart.setOnClickListener {
            val quantityStr = editTextQuantity.text.toString()
            if (quantityStr.isNotEmpty()) {
                val quantity = quantityStr.toInt()
                addToCart(product, quantity)
                dialog.dismiss()
            } else {
                Toast.makeText(itemView.context, "Please enter quantity", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun addToFavorites(product: Product) {
        // Add product to favorites
        val favoritesRef = FirebaseDatabase.getInstance().reference.child("favorites")
        favoritesRef.child(product.id.toString()).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(itemView.context, "Added to Favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(itemView.context, "Failed to add to Favorites", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addToCart(product: Product, quantity: Int) {
        // Add product to cart with quantity
        val cartRef = FirebaseDatabase.getInstance().reference.child("cart")
        val cartItem = CartItem(
            product.id,
            product.name,
            product.description,
            product.price,
            product.productImageUrl,
            quantity
        )
        cartRef.child(product.id.toString()).setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(itemView.context, "Added to Cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(itemView.context, "Failed to add to Cart", Toast.LENGTH_SHORT).show()
            }
    }
}


class ViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val productNameTextView: TextView = itemView.findViewById(R.id.textViewName)
    private val productDescriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
    private val productPriceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
    private val productImageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
    val addToCartButton: ImageView = itemView.findViewById(R.id.cart)
    val addToFavoritesButton: ImageView = itemView.findViewById(R.id.favourite)

    fun bind(product: Product) {
        productNameTextView.text = product.name
        productDescriptionTextView.text = product.description
        productPriceTextView.text = "Price: Rs ${product.price}"
        Glide.with(itemView).load(product.productImageUrl).into(productImageView)
    }
}


data class Product(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val productImageUrl: String = ""
)

data class CartItem(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val productImageUrl: String = "",
    val quantity: Int = 0
)





