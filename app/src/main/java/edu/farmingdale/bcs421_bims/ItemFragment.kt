package edu.farmingdale.bcs421_bims

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import edu.farmingdale.bcs421_bims.databinding.FragmentItemBinding

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    lateinit var itemImageUrl: String
    lateinit var itemName: String
    lateinit var itemUPC: String
    lateinit var itemQuantity: String
    lateinit var itemLocation: String
    lateinit var itemKey: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Retrieve data from arguments
        val args = arguments
        itemImageUrl = args?.getString("itemImage", "") ?: ""
        itemName = args?.getString("itemName", "Default Name") ?: "Default Name"
        itemUPC = args?.getString("itemUPC", "123456789") ?: "123456789"
        itemQuantity = args?.getString("itemQuantity", "0") ?: "0"
        itemLocation = args?.getString("itemLocation", "Default Location") ?: "Default Location"
        itemKey = args?.getString("itemKey", "") ?: ""
        Log.d("ItemFragment", "Received item UPC: $itemKey")

        //Set data to views
        if (itemImageUrl.isNotEmpty()) {
            Picasso.get().load(itemImageUrl).into(binding.itemImage)
        } else {
            //Default image if URL is empty
            binding.itemImage.setImageResource(R.drawable.person)
        }
        binding.textViewName.text = itemName
        binding.textViewUpc.text = "UPC: " + itemUPC
        binding.textViewQua.setText("Quantity: " + itemQuantity)
        binding.textViewLoc.text = "Loc: "+ itemLocation

    binding.toolBar.leftIcon.setOnClickListener{
        findNavController().popBackStack()
    }
        binding.toolBar.RightIcon.setOnClickListener{
            showPopupMenu()
        }

        // TODO: Add onClickListener for addQuantity and remQuantity buttons
        binding.addQuantity.setOnClickListener {
            val quantityToEdit = binding.QuantityToEdit.text.toString().toIntOrNull()
            if (quantityToEdit == null || quantityToEdit <= 0) {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentQuantity = itemQuantity.toInt()
            val newQuantity = currentQuantity + quantityToEdit
            itemQuantity = newQuantity.toString()
            updateItemQuantity(itemKey, newQuantity)
        }

        binding.remQuantity.setOnClickListener {
            val quantityToEdit = binding.QuantityToEdit.text.toString().toIntOrNull()
            if (quantityToEdit == null || quantityToEdit <= 0) {
                Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentQuantity = itemQuantity.toInt()
            val newQuantity = currentQuantity - quantityToEdit

            if (newQuantity < 0) {
                Toast.makeText(context, "Cannot remove more than available stock.", Toast.LENGTH_SHORT).show()
            } else {
                itemQuantity = newQuantity.toString()
                updateItemQuantity(itemKey, newQuantity)
            }
        }
    }

    private fun updateItemQuantity(itemKey: String, newQuantity: Int) {
        //Create reference to specific item in the Firebase database
        Log.d("ItemFragment", "Updating Product: $itemKey with new quantity: $newQuantity")
        val itemREf = FirebaseDatabase.getInstance().getReference("items").child(itemKey)

        //Update the quantity field of the item
        itemREf.child("quantity").setValue(newQuantity.toString())
            .addOnSuccessListener {
                //Update UI to reflect new quantity
                binding.textViewQua.text = "Quantity: $newQuantity"
                Toast.makeText(context, "Quantity updated in Database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                //Handle any errors in updating the database
                Toast.makeText(context, "Failed to update Quantity in Database", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.toolBar.RightIcon)
        popupMenu.inflate(R.menu.lefticonmenu) // Use your menu resource here

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val bundle = Bundle()


                    bundle.putString("itemImage", itemImageUrl)
                    bundle.putString("itemUPC", itemUPC)
                    bundle.putString("itemName", itemName)
                    bundle.putString("itemQuantity", itemQuantity)
                    bundle.putString("itemLocation", itemLocation)
                    bundle.putString("itemKey",itemKey)

                    val itemeditFragment = itemeditFragment()
                    itemeditFragment.arguments = bundle

                    // Replace the current fragment with ItemFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fram_layout, itemeditFragment)
                        .addToBackStack(null)
                        .commit()

                    // Handle the Edit menu item click
                    // Switch to the editable layout or perform other actions
                    true
                }
                // Add more menu items as needed
                else -> false
            }
        }

        popupMenu.show()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
