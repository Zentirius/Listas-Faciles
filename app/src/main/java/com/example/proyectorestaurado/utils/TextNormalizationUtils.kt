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

    // Elimina tildes de una cadena para comparación robusta
    fun removeAccents(text: String): String {
        val accents = "áéíóúÁÉÍÓÚñÑ"
        val replacements = "aeiouAEIOUnN"
        var result = text
        for (i in accents.indices) {
            result = result.replace(accents[i], replacements[i])
        }
        return result
    }
    // Puedes agregar aquí más funciones de limpieza y normalización
}
