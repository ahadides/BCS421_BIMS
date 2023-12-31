package edu.farmingdale.bcs421_bims

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.farmingdale.bcs421_bims.databinding.FragmentDashBinding
class DashFragment : Fragment() {

    private var _binding: FragmentDashBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference
    private lateinit var lowInventoryAdapter: LowInventoryAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchInventoryData()
        return binding.root
    }

    private fun setupRecyclerView() {
        lowInventoryAdapter = LowInventoryAdapter { clickedProduct ->
            val bundle = Bundle().apply {
                putString("itemImage", clickedProduct.imageUrl)
                putString("itemUPC", clickedProduct.barcodeNumber)
                putString("itemName", clickedProduct.productName)
                putString("itemQuantity", clickedProduct.quantity)
                putString("itemLocation", clickedProduct.location)
                putString("itemKey", clickedProduct.key)
            }

            val itemFragment = ItemFragment().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fram_layout, itemFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lowInventoryAdapter
        }
    }

    private fun fetchInventoryData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("items")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lowInventoryProducts = dataSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(Product::class.java)?.takeIf {
                        //Convert the quantity to an integer before comparison
                        (it.quantity.toIntOrNull() ?: 0) < 3
                    }
                }
                lowInventoryAdapter.setProducts(lowInventoryProducts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //Handle error, could log an error or show a toast
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DashFragment()
    }
}

data class Product(
    val imageUrl: String = "",
    val barcodeNumber: String = "",
    val productName: String = "",
    val quantity: String = "",
    val location: String = "",
    val key: String = ""
)

class LowInventoryAdapter(private val onItemClick: (Product) -> Unit) : RecyclerView.Adapter<LowInventoryAdapter.ProductViewHolder>() {
    private var products = mutableListOf<Product>()

    fun setProducts(products: List<Product>) {
        this.products = products.toMutableList()
        //Notify any registered observers that the data set has changed.
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.low_inventory, parent, false) // Use the new layout here
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)

        fun bind(product: Product) {
            tvProductName.text = product.productName
            tvQuantity.text = "Quantity: ${product.quantity}"
        }
    }
}