package edu.farmingdale.bcs421_bims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class InventorySearchAdapter(private val itemList: List<Item>, private val onItemClick: (Item) -> Unit) :
    RecyclerView.Adapter<InventorySearchAdapter.InventoryViewHolder>() {

    //ViewHolder class that holds references to the views for each data item
    class InventoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val quantityView: TextView = view.findViewById(R.id.item_quantity)
        val locationView: TextView = view.findViewById(R.id.item_location)
    }

    //Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_layout, parent, false)
        return InventoryViewHolder(view)
    }

    //Replace contents of a view
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = itemList[position]
        //Load the image using Picasso or Glide if it's a URL
        Picasso.get().load(item.imageUrl).into(holder.imageView)
        holder.nameView.text = item.productName
        holder.quantityView.text = item.quantity
        holder.locationView.text = item.location

        //Set a click listener for each item view
        holder.itemView.setOnClickListener {
            onItemClick(itemList[position])
        }
    }

    //Return the size of your dataset
    override fun getItemCount() = itemList.size
}




