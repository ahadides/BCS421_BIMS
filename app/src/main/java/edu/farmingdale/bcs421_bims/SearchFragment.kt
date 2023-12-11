package edu.farmingdale.bcs421_bims

import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.farmingdale.bcs421_bims.databinding.FragmentSearchBinding
import java.util.Locale

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val REQUEST_CODE_SCAN_ACTIVITY = 123
    private val binding get() = _binding!!
    private var allItems = mutableListOf<Item>()
    private lateinit var databaseRef: DatabaseReference
    private lateinit var valueEventListener: ValueEventListener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val fab = binding.addButton

        fab.setOnClickListener{
            val itemEditFragment = itemeditFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            transaction.replace(R.id.fram_layout, itemEditFragment)

            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                performSearch(s.toString())  // Call performSearch whenever the text changes
            }
        })

        binding.cameraButton.setOnClickListener {
            // Redirect to the Scan activity with startActivityForResult
            val intent = Intent(requireContext(), Scan::class.java)
            startActivityForResult(intent, REQUEST_CODE_SCAN_ACTIVITY)
        }
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
            bundle.putString("itemKey", clickedItem.key)
            val itemFragment = ItemFragment()
            itemFragment.arguments = bundle

            // Replace the current fragment with ItemFragment
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fram_layout, itemFragment)
                .addToBackStack(null)
                .commit()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN_ACTIVITY && resultCode == RESULT_OK) {
            // Retrieve the scanned barcode data from the result intent
            val scannedBarcode = data?.getStringExtra(Scan.RESULT_BARCODE_DATA)
            // Update the search field with the scanned data
            binding.searchField.setText(scannedBarcode)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        databaseRef = FirebaseDatabase.getInstance().getReference("items")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear the current list to reflect latest data in database
                allItems.clear()
                for (itemSnapshot in snapshot.children) {
                    //manually create item object for each field from database document for datasnapshot
                    //uses default empty string if any fields is missing a field in the database
                    val item = Item(
                        imageUrl = itemSnapshot.child("imageUrl").getValue(String::class.java) ?: "",
                        barcodeNumber = itemSnapshot.child("barcodeNumber").getValue(String::class.java) ?: "",
                        productName = itemSnapshot.child("productName").getValue(String::class.java) ?: "",
                        quantity = itemSnapshot.child("quantity").getValue(String::class.java) ?: "",
                        location = itemSnapshot.child("location").getValue(String::class.java) ?: "",
                        //This is the Firebase key for each item which is the unique document id
                        key = itemSnapshot.key ?: ""
                    )
                    allItems.add(item)
                }
                //filter out the items with current query and update recyclerview
                performSearch(binding.searchField.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                //Place any logs here to show error message if database read operation was cancelled
            }
        }

        databaseRef.addValueEventListener(valueEventListener)

        //Add divider between items
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(::databaseRef.isInitialized && ::valueEventListener.isInitialized) {
            databaseRef.removeEventListener(valueEventListener)
        }
        _binding = null
    }
}