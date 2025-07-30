package com.example.proyectorestaurado.utils

import android.graphics.Bitmap
import android.util.Log
import com.example.proyectorestaurado.data.ShoppingItem
import com.example.proyectorestaurado.utils.ParsedItem
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OCRProcessor {

    private val TAG = "OCRProcessor"
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImage(bitmap: Bitmap): List<ShoppingItem> = suspendCoroutine { continuation ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    try {
                        Log.d(TAG, "OCR completado. Texto: ${visionText.text}")
                        val parsedItems = processDetectedText(visionText.text)
                        Log.d(TAG, "Se procesaron ${parsedItems.size} items.")
                        continuation.resume(parsedItems)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al procesar texto detectado.", e)
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error en OCR.", e)
                    continuation.resumeWithException(e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear InputImage.", e)
            continuation.resumeWithException(e)
        }
    }

    private fun processDetectedText(text: String): List<ShoppingItem> {
        try {
            if (text.isBlank()) {
                return emptyList()
            }

            Log.d(TAG, "Texto para procesar con QuantityParser: $text")

            val parsedItems: List<ParsedItem> = QuantityParser.parseMultipleItems(text)
            Log.d(TAG, "Items parseados: ${parsedItems.size}")

            // Convertimos la lista de ParsedItem a ShoppingItem
            val shoppingItems = parsedItems.map { parsedItem ->
                ShoppingItem(
                    name = parsedItem.name,
                    quantity = parsedItem.quantity ?: 1.0,
                    unit = parsedItem.unit ?: "u",
                    brand = parsedItem.brand,
                    notes = parsedItem.notes,
                    category = parsedItem.category ?: "General",
                    priceRange = parsedItem.priceRange ?: com.example.proyectorestaurado.data.PriceRange.NORMAL
                )
            }
            
            // Filtrar duplicados por nombre (case insensitive) para evitar items repetidos
            val uniqueItems = shoppingItems.distinctBy { it.name.lowercase().trim() }
            Log.d(TAG, "Total de items únicos procesados: ${uniqueItems.size}")
            
            return uniqueItems

        } catch (e: Exception) {
            Log.e(TAG, "Error crítico procesando texto con QuantityParser.", e)
            return emptyList()
        }
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
