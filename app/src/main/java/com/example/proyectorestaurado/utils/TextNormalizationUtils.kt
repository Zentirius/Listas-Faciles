package com.example.proyectorestaurado.utils

object TextNormalizationUtils {
    fun normalizeMissingSpaces(text: String): String {
        // Implementa aquí la lógica de separación número-texto
        // Ejemplo simple:
        return text.replace(Regex("(\\d)([a-zA-Zñáéíóúü])"), "$1 $2")
    }

    fun applyOcrCorrections(text: String): String {
        // Corrige errores comunes de OCR
        return text.replace("1", "l") // Ejemplo simple, ajusta según tus reglas reales
    }

    // Puedes agregar aquí más funciones de limpieza y normalización
}
