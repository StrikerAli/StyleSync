package com.aliashraf.project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<CartItem, CartItemViewHolder>
    private lateinit var database: DatabaseReference
    private lateinit var totalPriceTextView: TextView
    private lateinit var Checkout_Button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        Checkout_Button = findViewById(R.id.btnGoToCheckout)

        Checkout_Button.setOnClickListener {
            val totalPriceString = totalPriceTextView.text.toString()
            val totalPrice = totalPriceString.substringAfter("Rs").trim() // Assuming "Rs" is the currency symbol
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("totalPrice", totalPrice)
            startActivity(intent)
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize total price TextView
        totalPriceTextView = findViewById(R.id.total_price)

        // Set up adapter
        val options = FirebaseRecyclerOptions.Builder<CartItem>()
            .setQuery(database.child("cart"), CartItem::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<CartItem, CartItemViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile1, parent, false)
                return CartItemViewHolder(view, database, totalPriceTextView)
            }

            override fun onBindViewHolder(holder: CartItemViewHolder, position: Int, model: CartItem) {
                holder.bind(model)
            }
        }

        // Set adapter to RecyclerView
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
        // Refresh total count
        refreshTotalCount()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun refreshTotalCount() {
        var newTotalPrice = 0.0
        database.child("cart").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapshot in dataSnapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        newTotalPrice += it.price * it.quantity
                    }
                }
                totalPriceTextView.text = "Total: Rs $newTotalPrice"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CartActivity", "onCancelled", databaseError.toException())
            }
        })
    }
}

class CartItemViewHolder(itemView: View, private val databaseReference: DatabaseReference, private val totalPriceTextView: TextView) : RecyclerView.ViewHolder(itemView) {
    private val productNameTextView: TextView = itemView.findViewById(R.id.textViewName)
    private val productPriceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
    private val productImageView: ImageView = itemView.findViewById(R.id.imageViewProduct)
    private val quantityTextView: TextView = itemView.findViewById(R.id.textViewQuantity)
    private val total: TextView = itemView.findViewById(R.id.Total)
    private val deleteCartImageView: ImageView = itemView.findViewById(R.id.delete_cart)
    private val editCartImageView: ImageView = itemView.findViewById(R.id.edit_cart)

    fun bind(cartItem: CartItem) {
        productNameTextView.text = cartItem.name
        productPriceTextView.text = "Price: Rs ${cartItem.price}"
        quantityTextView.text = "Quantity: ${cartItem.quantity}"
        val totalPrice = cartItem.price * cartItem.quantity
        total.text = "Total: Rs $totalPrice"
        Glide.with(itemView).load(cartItem.productImageUrl).into(productImageView)

        deleteCartImageView.setOnClickListener {
            val cartItemId = adapterPosition
            val cartItemRef = databaseReference.child("cart").child(cartItemId.toString())
            cartItemRef.removeValue()
        }

        editCartImageView.setOnClickListener {
            val dialog = AlertDialog.Builder(itemView.context)
            dialog.setTitle("Change Quantity")
            val input = EditText(itemView.context)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            dialog.setView(input)
            dialog.setPositiveButton("OK") { _, _ ->
                val newQuantity = input.text.toString().toInt()
                val productImageUrl = cartItem.productImageUrl
                databaseReference.child("cart")
                    .orderByChild("productImageUrl")
                    .equalTo(productImageUrl)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (itemSnapshot in dataSnapshot.children) {
                                val cartItemId = itemSnapshot.key
                                val cartItemRef = databaseReference.child("cart").child(cartItemId!!).child("quantity")
                                cartItemRef.setValue(newQuantity)
                                quantityTextView.text = "Quantity: $newQuantity" // Update quantity displayed in the view holder
                                val newTotalPrice = cartItem.price * newQuantity
                                total.text = "Total: Rs $newTotalPrice" // Update total price displayed in the view holder

                                // Refresh total count
                                refreshTotalCount()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("CartActivity", "onCancelled", databaseError.toException())
                        }
                    })
            }
            dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }

    private fun refreshTotalCount() {
        var newTotalPrice = 0.0
        databaseReference.child("cart").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapshot in dataSnapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    cartItem?.let {
                        newTotalPrice += it.price * it.quantity
                    }
                }
                totalPriceTextView.text = "Total: Rs $newTotalPrice"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CartActivity", "onCancelled", databaseError.toException())
            }
        })
    }
}
