package edu.farmingdale.bcs421_bims

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import edu.farmingdale.bcs421_bims.databinding.ActivityProductDetailsBinding

class ProductDetails : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val barcodeNumber = intent.getStringExtra("BARCODE_NUMBER")
        val productName = intent.getStringExtra("PRODUCT_NAME")
        val productImageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL")
        productImageUrl?.let { url ->
            Picasso.get().load(url).into(binding.ivProductImage)
        }

        binding.tvBarcodeNumber.text = "Barcode: $barcodeNumber"
        binding.tvProductName.text = "Product Name: $productName"

    }

}