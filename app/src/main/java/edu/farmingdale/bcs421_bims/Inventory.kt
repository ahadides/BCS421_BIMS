package edu.farmingdale.bcs421_bims

import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXThreads.TAG
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

class Inventory : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var barcodeScanner: BarcodeScanner

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
        private val TAG = Inventory::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        barcodeScanner = BarcodeScanning.getClient()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.btnCameraPermission.visibility = View.GONE
            startCamera()
        } else {
            binding.btnCameraPermission.setOnClickListener {
                checkCameraPermission()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //Permission was granted, use the camera
                    binding.btnCameraPermission.visibility = View.GONE
                    startCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show()
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
                    it.setAnalyzer(ContextCompat.getMainExecutor(this), ImageAnalysis.Analyzer { imageProxy ->
                        processImageProxy(imageProxy)
                    })
                }
            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class) private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
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
                        } else {
                            Log.d(TAG, "Barcode value is null")
                        }
                    }
                }
                .addOnFailureListener {
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun getProductDetails(barcode: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.upcitemdb.com/prod/trial/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UPCApiService::class.java)
        apiService.lookupUPC(barcode).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                Log.d(TAG, "API Response: ${response.raw()}")

                if (response.isSuccessful) {
                    // Extract rate limit headers
                    val rateLimit = response.headers()["X-RateLimit-Limit"]?.toInt()
                    val rateLimitRemaining = response.headers()["X-RateLimit-Remaining"]?.toInt()
                    val rateLimitReset = response.headers()["X-RateLimit-Reset"]?.toLong()
                    Log.d(TAG, "RateLimit: $rateLimit, Remaining: $rateLimitRemaining, Reset: $rateLimitReset")

                    val productInfo = response.body()?.items?.firstOrNull()
                    productInfo?.let {
                        Log.d(TAG, "Product name: ${it.title}")
                        // Now you have your product info. Launch the ProductDetailsActivity with this info
                        val intent = Intent(this@Inventory, ProductDetails::class.java).apply {
                            putExtra("BARCODE_NUMBER", barcode) // Pass the barcode number
                            putExtra("PRODUCT_NAME", it.title)
                            putExtra("PRODUCT_IMAGE_URL", it.images.firstOrNull())
                        }
                        startActivity(intent)
                    } ?: run {
                        Log.d(TAG, "Product info is null or empty")
                    }
                } else {
                    Log.e(TAG, "API call failed with response code ${response.code()} and message: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                // Handle failure
                Log.e(TAG, "API call failed with error: ${t.message}")
            }
        })
    }
}