package edu.farmingdale.bcs421_bims

import android.content.pm.PackageManager
import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import edu.farmingdale.bcs421_bims.databinding.ActivityInventoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class Inventory : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var barcodeScanner: BarcodeScanner
    private var isScannerEnabled = true

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
        private const val PRODUCT_DETAILS_REQUEST_CODE = 1002
        private val TAG = Inventory::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        barcodeScanner = BarcodeScanning.getClient()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            binding.btnCameraPermission.visibility = View.GONE
            startCamera()
        } else {
            binding.btnCameraPermission.setOnClickListener {
                checkCameraPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Permission was granted, use the camera
                    binding.btnCameraPermission.visibility = View.GONE
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission is required to use this feature",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.camera.surfaceProvider)
                }
            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(this),
                        ImageAnalysis.Analyzer { imageProxy ->
                            processImageProxy(imageProxy)
                        })
                }
            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScannerEnabled) {
            //Disable scanner after successful scan
            isScannerEnabled = false
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            Log.d(TAG, "Barcode value: $rawValue")
                            runOnUiThread {
                                binding.textViewBarcodeData.text = rawValue
                                getProductDetails(rawValue)
                            }
                            //Break after first successful scan
                            break
                        } else {
                            Log.d(TAG, "Barcode value is null")
                        }
                    }
                }
                .addOnFailureListener {
                    //Add failure for handle if needed
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    //Re-enable scanner after a delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        isScannerEnabled = true
                    }, 5000)
                }
        } else {
            imageProxy.close()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun getProductDetails(barcode: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.upcitemdb.com/prod/trial/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UPCApiService::class.java)
        apiService.lookupUPC(barcode).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                Log.d(TAG, "API Response: ${response.raw()}")

                if (response.isSuccessful) {
                    //Extract rate limit headers
                    val rateLimit = response.headers()["X-RateLimit-Limit"]?.toInt()
                    val rateLimitRemaining = response.headers()["X-RateLimit-Remaining"]?.toInt()
                    val rateLimitReset = response.headers()["X-RateLimit-Reset"]?.toLong()
                    Log.d(
                        TAG,
                        "RateLimit: $rateLimit, Remaining: $rateLimitRemaining, Reset: $rateLimitReset"
                    )

                    val productInfo = response.body()?.items?.firstOrNull()
                    productInfo?.let {
                        val imageUrl = it.images.firstOrNull() ?: ""
                        val productName = it.title ?: ""
                        launchProductDetails(barcode, productName, imageUrl)
                    }
                } else {
                    Log.e(TAG, "API call failed with response code ${response.code()} and message: " +
                            "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                // Handle failure
                Log.e(TAG, "API call failed with error: ${t.message}")
            }
        })
    }

    private fun launchProductDetails(barcode: String, title: String, imageUrl: String) {
        val intent = Intent(this, ProductDetails::class.java).apply {
            putExtra("BARCODE_NUMBER", barcode)
            putExtra("PRODUCT_NAME", title)
            putExtra("PRODUCT_IMAGE_URL", imageUrl)
        }
        startActivityForResult(intent, PRODUCT_DETAILS_REQUEST_CODE)
        //finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PRODUCT_DETAILS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bundle = data?.extras

            val resultIntent = bundle?.let { Intent().putExtras(it) }
            setResult(Activity.RESULT_OK, resultIntent)

            // Check if the bundle is not null before using its contents
            bundle?.let {
                // Use the bundle to perform actions with the received data
                val itemImage = bundle.getString("itemImage", "")
                val itemUPC = bundle.getString("itemUPC", "")
                val itemName = bundle.getString("itemName", "")
                val itemQuantity = bundle.getString("itemQuantity", "")
                val itemLocation = bundle.getString("itemLocation", "")

                // Do something with the received data
                // ...

                // Optional: Toast to check if data is received successfully
                //Toast.makeText(
                //    this,
                //    "Received data: $itemImage, $itemUPC, $itemName, $itemQuantity, $itemLocation",
                //    Toast.LENGTH_LONG
              //  ).show()
                finish()
            }
        }
    }


}
