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
        lowInventoryAdapter = LowInventoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lowInventoryAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchInventoryData()
    }


    private fun fetchInventoryData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("items")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lowInventoryProducts = dataSnapshot.children.mapNotNull { snapshot ->
                    snapshot.getValue(Product::class.java)?.takeIf {
                        // Convert the quantity to an integer before comparison
                        (it.quantity.toIntOrNull() ?: 0) < 3
                    }
                }
                lowInventoryAdapter.setProducts(lowInventoryProducts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error, could log an error or show a toast
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

data class Product(val productName: String = "", val quantity: String = "")

class LowInventoryAdapter : RecyclerView.Adapter<LowInventoryAdapter.ProductViewHolder>() {
    private var products = mutableListOf<Product>()


    fun setProducts(products: List<Product>) {
        this.products = products.toMutableList()
        notifyDataSetChanged() // Notify any registered observers that the data set has changed.
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.low_inventory, parent, false) // Use the new layout here
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
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