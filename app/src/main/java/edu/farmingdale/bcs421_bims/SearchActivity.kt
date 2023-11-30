package edu.farmingdale.bcs421_bims

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
            Item(R.drawable.ic_test_pic_foreground, "Item 2", 3, "Aisle 4"),
        )


        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = InventorySearchAdapter(items)

        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}