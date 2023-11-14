package edu.farmingdale.bcs421_bims

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        // Get item details from the intent or another data source
        val itemTitle = intent.getStringExtra("ITEM_TITLE")
        val itemPrice = intent.getDoubleExtra("ITEM_PRICE", 0.0)
        // Add more item details as needed

        // Set item details to UI components
        itemTitleTextView.text = itemTitle
        itemPriceTextView.text = "Price: $itemPrice" // Format the price as needed

        // Load item image using a library like Picasso or Glide
        // Picasso.get().load("item_image_url").into(itemImageView)
    }
}