package edu.farmingdale.bcs421_bims

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.farmingdale.bcs421_bims.R.layout.item_inventory_layout

class InventorySearchAdapter(private val itemList: List<Item>) :
    RecyclerView.Adapter<InventorySearchAdapter.InventoryViewHolder>() {

    // ViewHolder class that holds references to the views for each data item
    class InventoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val quantityView: TextView = view.findViewById(R.id.item_quantity)
        val locationView: TextView = view.findViewById(R.id.item_location)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(item_inventory_layout, parent, false)
        return InventoryViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = itemList[position]
        holder.imageView.setImageResource(item.image)
        holder.nameView.text = item.name
        holder.quantityView.text = item.quantity.toString()
        holder.locationView.text = item.location
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = itemList.size

}
