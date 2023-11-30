package edu.farmingdale.bcs421_bims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        // Retrieve data from arguments
        val args = arguments
        val itemImageResId = args?.getInt("itemImage", R.drawable.person) ?: R.drawable.person
        val itemName = args?.getString("itemName", "Default Name") ?: "Default Name"
        val itemQuantity = args?.getInt("itemQuantity", 0) ?: 0
        val itemLocation = args?.getString("itemLocation", "Default Location") ?: "Default Location"

        // Set data to views
        binding.itemImage.setImageResource(itemImageResId)
        binding.textViewName.text = itemName
        binding.textViewQua.setText("Quantity: " +itemQuantity.toString())
        binding.textViewLoc.text = "Loc: "+itemLocation

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
