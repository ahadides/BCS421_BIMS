package edu.farmingdale.bcs421_bims

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.farmingdale.bcs421_bims.databinding.FragmentSearchBinding
import java.util.Locale

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var allItems = listOf<Item>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                performSearch(s.toString())  // Call performSearch whenever the text changes
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = allItems.filter {
            it.productName.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                    it.location.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) ||
                    it.barcodeNumber.contains(query)
        }
        // Update the RecyclerView adapter with the filtered list
        binding.recyclerView.adapter = InventorySearchAdapter(filteredList) { clickedItem ->
            //Handle the click event for the specific item (clickedItem)
            val bundle = Bundle()
            bundle.putString("itemImage", clickedItem.imageUrl)
            bundle.putString("itemUPC", clickedItem.barcodeNumber)
            bundle.putString("itemName", clickedItem.productName)
            bundle.putString("itemQuantity", clickedItem.quantity)
            bundle.putString("itemLocation", clickedItem.location)

            val itemFragment = ItemFragment()
            itemFragment.arguments = bundle

            // Replace the current fragment with ItemFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fram_layout, itemFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Define DatabaseReference
        val databaseRef = FirebaseDatabase.getInstance().getReference("items")

        //Fetch data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = ArrayList<Item>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(Item::class.java)
                    item?.let { items.add(it) }
                }
                allItems = items // update allItems with fetched data, will be used for queries
                performSearch("") // initially contains all items

                //Setup the adapter with the fetched items
//                val adapter = InventorySearchAdapter(items) { clickedItem ->
//                    //Handle the click event for the specific item (clickedItem)
//                    val bundle = Bundle()
//                    bundle.putString("itemImage", clickedItem.imageUrl)
//                    bundle.putString("itemUPC", clickedItem.barcodeNumber)
//                    bundle.putString("itemName", clickedItem.productName)
//                    bundle.putString("itemQuantity", clickedItem.quantity)
//                    bundle.putString("itemLocation", clickedItem.location)
//
//                    val itemFragment = ItemFragment()
//                    itemFragment.arguments = bundle
//
//                    // Replace the current fragment with ItemFragment
//                    requireActivity().supportFragmentManager.beginTransaction()
//                        .replace(R.id.fram_layout, itemFragment)
//                        .addToBackStack(null)
//                        .commit()
//                }
//                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })

        //Add divider between items
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
