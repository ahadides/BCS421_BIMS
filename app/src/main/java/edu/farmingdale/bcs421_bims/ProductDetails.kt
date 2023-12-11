package edu.farmingdale.bcs421_bims

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import edu.farmingdale.bcs421_bims.databinding.ActivityProductDetailsBinding
import java.io.ByteArrayOutputStream
import java.util.*

class ProductDetails : AppCompatActivity(){

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



        // Set the listener

        binding.bnSubmit.setOnClickListener {

            val quantity = binding.etQuantity.text.toString()
            val location = binding.etLocation.text.toString()

            Toast.makeText(this, "step2", Toast.LENGTH_LONG).show()

            val bundle = Bundle().apply {
                putString("itemImage", productImageUrl)
                putString("itemUPC", barcodeNumber)
                putString("itemName", productName)
                putString("itemQuantity", quantity)
                putString("itemLocation", location)
            }

            finish()


        }
    }




    /*private fun uploadData(barcode: String?, productName: String?, imageUrl: String?, quantity: String, location: String) {
        Picasso.get().load(imageUrl).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                //Convert bitmap to byte array
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                //Upload to Firebase
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val firebaseImageUrl = uri.toString()
                        saveItemDetails(barcode, productName, firebaseImageUrl, quantity, location)
                    }
                }.addOnFailureListener {
                    //Handle unsuccessful uploads
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
        })
    }

    private fun saveItemDetails(barcode: String?, productName: String?, imageUrl: String?, quantity: String, location: String) {
        val item = Item(imageUrl ?: "", barcode ?: "", productName ?: "", quantity, location)
        val databaseRef = FirebaseDatabase.getInstance().getReference("items")
        databaseRef.push().setValue(item).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Handle successful upload
                val homeIntent = Intent(this, MainActivity::class.java)
                startActivity(homeIntent)
                //closes ProductDetails activity
                finish()
            } else {
                //Handle failure
                task.exception?.let {
                    Toast.makeText(this, "Failed to save item: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }*/


}