package com.aliashraf.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*

public class GuestHomeActivity : AppCompatActivity() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var adapter13: FirebaseRecyclerAdapter<Product, ViewHolder3>
    private lateinit var adapter23: FirebaseRecyclerAdapter<Product, ViewHolder3>
    private lateinit var database: DatabaseReference

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

        // Set up adapters
        val options1 = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(database.child("products"), Product::class.java)
            .build()

        adapter13 = object : FirebaseRecyclerAdapter<Product, ViewHolder3>(options1) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder3 {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile3, parent, false)
                return ViewHolder3(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder3, position: Int, model: Product) {
                // Check if the position is odd
                if (position * 2 < itemCount) {
                    val product = getItem(position * 2)
                    viewHolder.bind(product)
                }
            }
        }

        val options2 = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(database.child("products"), Product::class.java)
            .build()

        adapter23 = object : FirebaseRecyclerAdapter<Product, ViewHolder3>(options2) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder3 {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_userprofile3, parent, false)
                return ViewHolder3(view)
            }

            override fun onBindViewHolder(viewHolder: ViewHolder3, position: Int, model: Product) {
                // Check if the position is even
                if (position * 2 + 1 < itemCount) {
                    val product = getItem(position * 2 + 1)
                    viewHolder.bind(product)
                }
            }
        }

        // Set adapters to RecyclerViews
        recyclerView1.adapter = adapter13
        recyclerView2.adapter = adapter23
    }

    override fun onStart() {
        super.onStart()
        adapter13.startListening()
        adapter23.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter13.stopListening()
        adapter23.stopListening()
    }
}

// ViewHolder13 and ViewHolder23 classes remain the same


class ViewHolder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val productNameTextView: TextView = itemView.findViewById(R.id.textViewName)
    private val productDescriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
    private val productPriceTextView: TextView = itemView.findViewById(R.id.textViewPrice)
    private val productImageView: ImageView = itemView.findViewById(R.id.imageViewProduct)


    fun bind(product: Product) {
        productNameTextView.text = product.name
        productDescriptionTextView.text = product.description
        productPriceTextView.text = "Price: Rs ${product.price}"
        Glide.with(itemView).load(product.productImageUrl).into(productImageView)




    }
}