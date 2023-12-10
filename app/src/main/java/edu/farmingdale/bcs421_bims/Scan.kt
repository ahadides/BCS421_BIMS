package edu.farmingdale.bcs421_bims

import android.content.pm.PackageManager
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class Scan : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var barcodeScanner: BarcodeScanner
    private var isScannerEnabled = true

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
        const val RESULT_BARCODE_DATA = "RESULT_BARCODE_DATA"
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
                    // Permission was granted, use the camera
                    binding.btnCameraPermission.visibility = View.GONE
                    startCamera()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission is required to use this feature",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
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

    @androidx.annotation.OptIn(ExperimentalGetImage::class) @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null && isScannerEnabled) {
            // Disable scanner after successful scan
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
                            }
                            val resultIntent = Intent().apply {
                                putExtra(RESULT_BARCODE_DATA, rawValue)
                            }
                            setResult(RESULT_OK, resultIntent)
                            finish()
                            // Break after first successful scan
                            break
                        } else {
                            Log.d(TAG, "Barcode value is null")
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    // Re-enable scanner after a delay
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
}
