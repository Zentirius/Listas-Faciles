package com.example.proyectorestaurado

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectorestaurado.databinding.ActivityCameraBinding
import com.example.proyectorestaurado.utils.OCRProcessor
import com.example.proyectorestaurado.utils.ParsedItem
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var ocrProcessor: OCRProcessor

    private val TAG = "CameraActivity"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara requerido para usar esta función", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ocrProcessor = OCRProcessor()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.buttonCapture.setOnClickListener {
            takePhoto()
        }

        binding.buttonClose.setOnClickListener {
            finish()
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        binding.buttonCapture.isEnabled = false
        binding.buttonCapture.text = "Procesando..."

        val photoFile = File(
            cacheDir,
            "${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Error al capturar imagen: ${exception.message}", exception)
                    Toast.makeText(this@CameraActivity, "Error al capturar imagen", Toast.LENGTH_SHORT).show()
                    resetCaptureButton()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Imagen capturada exitosamente: ${photoFile.absolutePath}")
                    processImage(photoFile.absolutePath)
                }
            }
        )
    }

    private fun processImage(imagePath: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Iniciando procesamiento de imagen: $imagePath")
                val bitmap = loadAndResizeBitmap(imagePath)

                if (bitmap != null) {
                    val parsedItems = ocrProcessor.processImage(bitmap)
                    Log.d(TAG, "Items procesados desde OCR: ${parsedItems.size}")

                    if (parsedItems.isNotEmpty()) {
                        val resultIntent = Intent().apply {
                            val itemsAsList = ArrayList(parsedItems.map { it as ParsedItem? })
                            putExtra("ocr_results_list", itemsAsList)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@CameraActivity, "No se detectaron productos en la imagen", Toast.LENGTH_LONG).show()
                        resetCaptureButton()
                    }
                } else {
                    Toast.makeText(this@CameraActivity, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                    resetCaptureButton()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en procesamiento de imagen: ${e.message}", e)
                Toast.makeText(this@CameraActivity, "Error al procesar la imagen: ${e.message}", Toast.LENGTH_LONG).show()
                resetCaptureButton()
            }
        }
    }

    private fun loadAndResizeBitmap(imagePath: String): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(imagePath, options)

            val scaleFactor = calculateInSampleSize(options, 1024, 1024)

            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = scaleFactor
            }

            BitmapFactory.decodeFile(imagePath, finalOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar imagen: ${e.message}", e)
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun resetCaptureButton() {
        binding.buttonCapture.isEnabled = true
        binding.buttonCapture.text = "Capturar Lista"
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Error al iniciar cámara", exc)
                Toast.makeText(this, "Error al iniciar la cámara", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        ocrProcessor.cleanup()
    }

    companion object {
        const val REQUEST_CODE = 123
    }
}
