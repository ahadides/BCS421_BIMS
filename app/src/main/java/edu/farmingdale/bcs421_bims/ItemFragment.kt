package edu.farmingdale.bcs421_bims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import edu.farmingdale.bcs421_bims.databinding.FragmentItemBinding

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

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
        val itemImageUrl = args?.getString("itemImage", "") ?: ""
        val itemName = args?.getString("itemName", "Default Name") ?: "Default Name"
        val itemUPC = args?.getString("itemUPC", "123456789") ?: "123456789"
        val itemQuantity = args?.getString("itemQuantity", "0") ?: "0"
        val itemLocation = args?.getString("itemLocation", "Default Location") ?: "Default Location"

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

        // TODO: Add onClickListener for addQuantity and remQuantity buttons
        binding.addQuantity.setOnClickListener {
            // Handle addQuantity button click
            // Implement your logic here
        }

        binding.remQuantity.setOnClickListener {
            // Handle remQuantity button click
            // Implement your logic here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
