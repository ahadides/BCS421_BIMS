// SearchFragment.kt
package edu.farmingdale.bcs421_bims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sample data
        val items = listOf(
            Item(R.drawable.ic_test_pic_foreground, "Item 12334567759402834", 5, "Aisle 3"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 8", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2124", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 359r834", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2148u21498", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 1", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 9", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 3", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 5", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4"),
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up the adapter with a click listener
        val adapter = InventorySearchAdapter(items) { clickedItem ->
            // Handle the click event for the specific item (clickedItem)
            // Pass data to ItemFragment when an item is clicked
            val bundle = Bundle()
            bundle.putInt("itemImage", clickedItem.image)
            bundle.putString("itemName", clickedItem.name)
            bundle.putInt("itemQuantity", clickedItem.quantity)
            bundle.putString("itemLocation", clickedItem.location)

            val itemFragment = ItemFragment()
            itemFragment.arguments = bundle

            // Replace the current fragment with ItemFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fram_layout, itemFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        // Add divider between items
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}
