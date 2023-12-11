package edu.farmingdale.bcs421_bims

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import edu.farmingdale.bcs421_bims.databinding.FragmentItemeditBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class itemeditFragment : Fragment() {



    private var _binding: FragmentItemeditBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private val REQUEST_SCAN = 3
    private var currentPhotoPath: String? = null
    lateinit var itemImageUrl: String
    lateinit var itemName: String
    lateinit var itemUPC: String
    lateinit var itemQuantity: String
    lateinit var itemLocation: String
    lateinit var itemKey: String


    companion object {

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_PICK_IMAGE = 2
        const val REQUEST_SCAN = 3

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemeditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolBar.ToolBarText.text = "Edit Item"
        binding.toolBar.RightIcon.visibility = View.GONE

        binding.toolBar.leftIcon.setOnClickListener{
            requireActivity().onBackPressed()
        }
        // Retrieve data from arguments
        val args = arguments


        itemImageUrl = args?.getString("itemImage", "") ?: ""
        itemName = args?.getString("itemName", "Default Name") ?: "Default Name"
        itemUPC = args?.getString("itemUPC", "123456789") ?: "123456789"
        itemQuantity = args?.getString("itemQuantity", "0") ?: "0"
        itemLocation = args?.getString("itemLocation", "Default Location") ?: "Default Location"
        itemKey = args?.getString("itemKey", "") ?: ""
        // Load image using Picasso
        if (itemImageUrl.isNotEmpty()) {
            Picasso.get().load(itemImageUrl).into(binding.itemImage)
        } else {
            // Default image if URL is empty
            binding.itemImage.setImageResource(R.drawable.person)
        }

        // Set initial values for EditText elements
        binding.textViewName.setText(itemName)
        binding.textViewUpc.setText(itemUPC)
        binding.textViewLoc.setText(itemLocation)
        binding.textViewQua.setText(itemQuantity)

        // Handle ADD button click
        binding.Submit.setOnClickListener {
            itemName = binding.textViewName.text.toString()
            itemUPC = binding.textViewUpc.text.toString()
            itemQuantity = binding.textViewQua.text.toString()
            itemLocation = binding.textViewLoc.text.toString()

            val item = Item(itemImageUrl ?: "", itemUPC ?: "", itemName ?: "", itemQuantity, itemLocation,itemKey)
            Toast.makeText(context, item.key, Toast.LENGTH_LONG).show()
            uploadData(item)

            // Use FragmentManager to pop the back stack and navigate back to the previous fragment



        }
        binding.itemImage.setOnClickListener {
            showPopupMenu()
        }




        // Observe changes in the sharedViewModel.dataToPass

        // Add more logic here for other buttons or actions
    }

    private fun updateItem(newItem: Item) {
        // Create reference to specific item in the Firebase database
        Log.d("ItemFragment", "Updating Product: ${newItem.key} with new name: ${newItem.productName}")
        val itemRef = FirebaseDatabase.getInstance().getReference("items").child(newItem.key)

        // Update the entire item
        itemRef.setValue(newItem)
            .addOnSuccessListener {
                // Update UI or perform any other tasks after successful update

                Toast.makeText(context, "Item updated in Database", Toast.LENGTH_SHORT).show()

                requireActivity().supportFragmentManager.popBackStack()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                // Handle any errors in updating the database
                Toast.makeText(context, "Failed to update Item in Database", Toast.LENGTH_SHORT).show()
            }
    }



    //barcode: String?, productName: String?, imageUrl: String?, quantity: String, location: String
    private fun uploadData(newItem: Item) {
        Picasso.get().load(newItem.imageUrl).into(object : Target {
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
                        if(newItem.key == "") {
                            saveItemDetails(
                                newItem.barcodeNumber,
                                newItem.productName,
                                firebaseImageUrl,
                                newItem.quantity,
                                newItem.location
                            )
                        }else{
                            updateItem(newItem)
                        }
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
                //val homeIntent = Intent(context, MainActivity::class.java)
                //startActivity(homeIntent)
                //closes ProductDetails activity
                //finish()
                onDestroyView()
            } else {
                //Handle failure
                task.exception?.let {
                    Toast.makeText(context, "Failed to save item: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }




    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.itemImage)
        popupMenu.inflate(R.menu.imagepopupmenu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.camera -> {
                    dispatchTakePictureIntent()
                    true
                }
                R.id.upload -> {
                    openGallery()
                    true
                }
                R.id.Scan -> {
                    startScan()

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

        fun startScan() {
            // Start Inventory activity from the fragment for scanning
            val intent = Intent(requireContext(), Inventory::class.java)
            Toast.makeText(context, "Scanning...", Toast.LENGTH_LONG).show()
            startActivityForResult(intent, REQUEST_SCAN)
        }








    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireActivity().packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Handle the captured image from the camera (currentPhotoPath will contain the path)
                    val imageFile = File(currentPhotoPath)
                    // Get the URL of the captured image
                    val imageUri = Uri.fromFile(imageFile)
                    // Load the image into the ImageView
                    Picasso.get().load(imageUri).into(binding.itemImage)
                    // Save the image URL for later use
                    itemImageUrl = imageUri.toString()
                }
                REQUEST_PICK_IMAGE -> {
                    // Handle the selected image from the gallery
                    val selectedImage: Uri? = data?.data
                    // Load the image into the ImageView
                    Picasso.get().load(selectedImage).into(binding.itemImage)
                    // Save the image URL for later use
                    itemImageUrl = selectedImage.toString()
                }
                REQUEST_SCAN -> {
                    // Handle the result from the Inventory activity
                    // You can access data from the scan if needed
                    val bundle: Bundle? = data?.extras
                    bundle?.let { updateUI(it) }
                    val scannedData = data?.getStringExtra("SCANNED_DATA")
                    Toast.makeText(context, "Data Scanned", Toast.LENGTH_LONG).show()
                    // Handle the scanned data as needed
                }
            }
        }
    }


    private fun updateUI(bundle: Bundle) {
        // Update UI with the received data
        bundle?.let {
            // Use the bundle to perform actions with the received data
            itemImageUrl = bundle.getString("itemImage", "")
            itemUPC = bundle.getString("itemUPC", "")
            itemName = bundle.getString("itemName", "")
            itemQuantity = bundle.getString("itemQuantity", "")
            itemLocation = bundle.getString("itemLocation", "")
            if (itemImageUrl.isNotEmpty()) {
                Picasso.get().load(itemImageUrl).into(binding.itemImage)
            } else {
                // Default image if URL is empty
                binding.itemImage.setImageResource(R.drawable.logo)
            }

            // Set initial values for EditText elements
            binding.textViewName.setText(itemName)
            binding.textViewUpc.setText(itemUPC)
            binding.textViewLoc.setText(itemLocation)
            binding.textViewQua.setText(itemQuantity)
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

   /* fun onDataSubmitted(bundle: Bundle) {


        itemImageUrl = bundle.getString("itemImage", "")
        itemName = bundle.getString("itemName", "Default Name") ?: "Default Name"
        itemUPC = bundle.getString("itemUPC", "123456789") ?: "123456789"
        itemQuantity = bundle.getString("itemQuantity", "0") ?: "0"
        itemLocation = bundle.getString("itemLocation", "Default Location") ?: "Default Location"
        Toast.makeText(context, itemName, Toast.LENGTH_LONG).show()

        updateUI()
    }*/


}



