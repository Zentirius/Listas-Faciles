package com.example.proyectorestaurado.utils

import android.graphics.Bitmap
import android.util.Log
import com.example.proyectorestaurado.utils.ParsedItem
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRProcessor {
    private val TAG = "OCRProcessor"
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val quantityParser = QuantityParser()
    
    suspend fun processImage(bitmap: Bitmap): List<ParsedItem> = suspendCancellableCoroutine { continuation ->
        try {
            Log.d(TAG, "Iniciando procesamiento OCR de imagen")
            val image = InputImage.fromBitmap(bitmap, 0)
            
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    try {
                        Log.d(TAG, "OCR completado exitosamente")
                        Log.d(TAG, "Texto detectado: ${visionText.text}")
                        
                        val parsedItems = processDetectedText(visionText.text)
                        Log.d(TAG, "Se procesaron ${parsedItems.size} items de la imagen")
                        
                        continuation.resume(parsedItems)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al procesar texto detectado: ${e.message}")
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error en OCR: ${e.message}")
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear InputImage: ${e.message}")
            continuation.resumeWithException(e)
        }
    }
    
    private fun processDetectedText(text: String): List<ParsedItem> {
        Log.d(TAG, "Procesando texto detectado: $text")
        
        // Dividir el texto en líneas y limpiar
        val lines = text.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() && it.length > 1 }
        
        Log.d(TAG, "Líneas detectadas: ${lines.size}")
        
        val parsedItems = mutableListOf<ParsedItem>()
        
        lines.forEach { line ->
            Log.d(TAG, "Procesando línea: '$line'")
            
            // Filtrar líneas que probablemente no sean productos
            if (isValidProductLine(line)) {
                val parsedItem = quantityParser.parseItem(line)
                if (parsedItem != null) {
                    parsedItems.add(parsedItem)
                    Log.d(TAG, "Item procesado: nombre='${parsedItem.name}', cantidad=${parsedItem.quantity}, unidad='${parsedItem.unit}'")
                } else {
                    Log.d(TAG, "No se pudo parsear la línea: '$line'")
                }
            } else {
                Log.d(TAG, "Línea descartada (no parece ser un producto): '$line'")
            }
        }
        
        Log.d(TAG, "Total de items válidos procesados: ${parsedItems.size}")
        return parsedItems
    }
    
    private fun isValidProductLine(line: String): Boolean {
        // Filtrar líneas que probablemente no sean productos de una lista de compras
        val invalidPatterns = listOf(
            "^\\d{1,2}/\\d{1,2}".toRegex(), // Fechas como "12/01"
            "^\\d{1,2}:\\d{2}".toRegex(), // Horas como "14:30"
            "^[A-Z]{2,}$".toRegex(), // Palabras en mayúsculas solas como "LISTA"
            "^\\$\\d+".toRegex(), // Precios como "$15"
            "^\\d+\\.\\d{2}$".toRegex(), // Números decimales solos como "15.50"
            "^-+$".toRegex(), // Líneas de guiones
            "^=+".toRegex(), // Líneas de igual
            "^\\*+$".toRegex() // Líneas de asteriscos
        )
        
        // Verificar si la línea coincide con algún patrón inválido
        if (invalidPatterns.any { it.matches(line) }) {
            return false
        }
        
        // Verificar longitud mínima y máxima razonable
        if (line.length < 2 || line.length > 100) {
            return false
        }
        
        // Verificar que no sea solo números
        if (line.all { it.isDigit() || it.isWhitespace() }) {
            return false
        }
        
        return true
    }
    
    fun cleanup() {
        try {
            textRecognizer.close()
            Log.d(TAG, "OCR resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up OCR resources: ${e.message}")
        }
    }
}
