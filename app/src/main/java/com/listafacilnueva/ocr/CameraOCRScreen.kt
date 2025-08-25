@file:OptIn(ExperimentalGetImage::class)
package com.listafacilnueva.ocr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.annotation.SuppressLint
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraOCRScreen(
    onTextRecognized: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var isProcessing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Apunta la cámara hacia el texto") }
    var useCaptureMode by remember { mutableStateOf(false) } // false = stream, true = captura
    var cameraError by remember { mutableStateOf<String?>(null) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            statusMessage = "Permiso de cámara denegado"
        }
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Escanear Lista") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                // Botón para cambiar modo
                TextButton(
                    onClick = { 
                        useCaptureMode = !useCaptureMode
                        statusMessage = if (useCaptureMode) {
                            "Modo Captura - Presiona el botón para tomar foto"
                        } else {
                            "Modo Stream - Apunta hacia el texto"
                        }
                    }
                ) {
                    Text(
                        if (useCaptureMode) "Stream" else "Captura",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        
        if (hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Camera Preview con manejo de errores
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    useCaptureMode = useCaptureMode,
                    onTextRecognized = onTextRecognized,
                    onProcessingChanged = { isProcessing = it },
                    onStatusChanged = { statusMessage = it },
                    onError = { error -> cameraError = error }
                )
                
                // Status overlay
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = cameraError ?: statusMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (cameraError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                // Capture button - Solo visible en modo captura
                if (useCaptureMode) {
                    FloatingActionButton(
                        onClick = {
                            statusMessage = "Capturando foto..."
                            // Aquí se ejecutará la captura de foto
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Capturar Foto")
                    }
                }
            }
        } else {
            // Permission denied
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Se necesita permiso de cámara para escanear listas",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { launcher.launch(Manifest.permission.CAMERA) }
                ) {
                    Text("Conceder Permiso")
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    useCaptureMode: Boolean = false,
    onTextRecognized: (String) -> Unit,
    onProcessingChanged: (Boolean) -> Unit,
    onStatusChanged: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val textRecognizer = remember {
        try {
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        } catch (e: Exception) {
            onError("Error al inicializar OCR: ${e.message}")
            null
        }
    }
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraExecutor.shutdown()
                textRecognizer?.close()
            } catch (e: Exception) {
                // ✅ SILENCIOSO: No logging para evitar congelamiento
            }
        }
    }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    // Intentar cámara trasera primero, luego frontal (mejor para emulador)
                    val cameraSelector = try {
                        if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }
                    } catch (e: Exception) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    }
                    
                    if (useCaptureMode) {
                        // MODO CAPTURA: Solo ImageCapture (mejor para emulador/webcam)
                        val imageCapture = ImageCapture.Builder().build()
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                            onStatusChanged("Modo Captura - Cámara lista para fotos")
                        } catch (exc: Exception) {
                            onError("Error en modo captura: ${exc.message}")
                        }
                        
                    } else {
                        // MODO STREAM: Solo ImageAnalysis (mejor para dispositivos reales)
                        val imageAnalyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    try {
                                        processImageForText(
                                            imageProxy = imageProxy,
                                            textRecognizer = textRecognizer,
                                            onTextRecognized = onTextRecognized,
                                            onProcessingChanged = onProcessingChanged,
                                            onStatusChanged = onStatusChanged
                                        )
                                    } catch (e: Exception) {
                                        onStatusChanged("Error procesando imagen")
                                        imageProxy.close()
                                    }
                                }
                            }
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                            onStatusChanged("Modo Stream - Apunta hacia el texto")
                        } catch (exc: Exception) {
                            onError("Error en modo stream: ${exc.message}")
                        }
                    }
                    
                } catch (e: Exception) {
                    onError("Error al configurar cámara: ${e.message}")
                }
                
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalGetImage::class)
@SuppressLint("UnsafeOptInUsageError")
private fun processImageForText(
    imageProxy: ImageProxy,
    textRecognizer: com.google.mlkit.vision.text.TextRecognizer?,
    onTextRecognized: (String) -> Unit,
    onProcessingChanged: (Boolean) -> Unit,
    onStatusChanged: (String) -> Unit
) {
    if (textRecognizer == null) {
        onStatusChanged("OCR no disponible")
        imageProxy.close()
        return
    }
    
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        onProcessingChanged(true)

        try {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            // Usar callback en lugar de await()
            textRecognizer.process(image)
                .addOnSuccessListener { result ->
                    try {
                        val processedText = preprocessOCRText(result.text)
                        
                        if (processedText.isNotBlank()) {
                            onTextRecognized(processedText)
                            onStatusChanged("✅ Texto procesado: ${processedText.take(50)}...")
                        }
                    } catch (e: Exception) {
                        onStatusChanged("Error procesando texto")
                    } finally {
                        onProcessingChanged(false)
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->
                    onStatusChanged("⚠️ Error OCR: ${e.message?.take(40)}")
                    onProcessingChanged(false)
                    imageProxy.close()
                }
        } catch (e: Exception) {
            onStatusChanged("⚠️ Error imagen: ${e.message?.take(40)}")
            onProcessingChanged(false)
            imageProxy.close()
        }
    } else {
        onProcessingChanged(false)
        imageProxy.close()
    }
}

private fun preprocessOCRText(rawText: String): String {
    return try {
        rawText
            .replace(Regex("[^\\p{L}0-9 ]")) { " " } // Eliminar caracteres especiales
            .replace(Regex("\\s+")) { " " } // Unificar espacios
            .trim()
    } catch (e: Exception) {
        rawText.trim() // ✅ SILENCIOSO: No logging para evitar congelamiento
    }
}
