package com.listafacilnueva.parser

import android.util.Log

/**
 * PREPROCESADOR DE TEXTO
 * 
 * Funciones para limpiar, normalizar y preparar el texto
 * antes del procesamiento principal.
 */
object TextPreprocessor {
    
    private var DEBUG_MODE = false
    private const val TAG = "PREPROCESSOR"
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            Log.d(TAG, message)
        }
    }
    
    // PATRONES REGEX OPTIMIZADOS (compilados una vez)
    private val PATRON_DECIMAL_CON_PUNTO_FINAL = Regex("(\\d+\\.\\d+)\\.$")
    private val PATRON_DECIMALES_PEGADOS = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{2,})")
    private val PATRON_PRODUCTOS_CONSECUTIVOS = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)")
    private val PATRON_CANTIDADES_PEGADAS = Regex("(\\d+)([a-zA-Záéíóúüñ]{2,})(\\d+)([a-zA-Záéíóúüñ]{2,})")
    private val PATRON_CANTIDADES_ESPACIADAS = Regex("(\\d+)\\s+([a-zA-Záéíóúüñ]{2,})\\s+(\\d+)\\s+([a-zA-Záéíóúüñ]{2,})")
    private val PATRON_FRACCIONES_MIXTAS = Regex("(\\d+/\\d+)([a-zA-Záéíóúüñ]{2,})(\\d+)([a-zA-Záéíóúüñ]{2,})")
    // Importante: no colapsar saltos de línea (\n). Solo espacios/tabs/espacio no separador.
    private val PATRON_ESPACIOS_MULTIPLES = Regex("[ \t\u00A0]+")
    private val PATRON_COMAS_REPETIDAS = Regex(",+")

    /**
     * FUNCIÓN PRINCIPAL DE PREPROCESAMIENTO
     */
    fun preprocess(texto: String): String {
        var resultado = texto
        
        log("Preprocesando texto: ${texto.take(100)}...")
        
        // MEJORA CRÍTICA: Separar líneas por saltos de línea y puntos (pero no decimales)
        resultado = resultado.replace(Regex("\\n+"), "\n")
        // Solo separar por puntos que no sean parte de decimales
        resultado = resultado.replace(Regex("(?<!\\d)\\.(?!\\d)\\s*"), ".\n")
        
        // MEJORAS AVANZADAS INTEGRADAS DEL PARSER 100%:
        
        // MEJORA 1: Corregir decimales con punto final
        if (resultado.contains(PATRON_DECIMAL_CON_PUNTO_FINAL)) {
            resultado = resultado.replace(PATRON_DECIMAL_CON_PUNTO_FINAL, "$1")
            log("Corregidos decimales con punto final")
        }
        
        // MEJORA 2: Separar decimales pegados CRÍTICO para "1.1papa mediana2.sandia grande"
        if (resultado.contains(PATRON_DECIMALES_PEGADOS)) {
            resultado = resultado.replace(PATRON_DECIMALES_PEGADOS, "$1, $2")
            log("Separados decimales pegados")
        }
        
        // MEJORA 3: Separar productos consecutivos con decimales
        if (resultado.contains(PATRON_PRODUCTOS_CONSECUTIVOS)) {
            resultado = resultado.replace(PATRON_PRODUCTOS_CONSECUTIVOS, "$1 $2, $3 $4")
            log("Separados productos consecutivos con decimales")
        }
        
        // MEJORA 4: Separar cantidades pegadas sin decimales CRÍTICO para "6sandias8tomates"
        if (resultado.contains(PATRON_CANTIDADES_PEGADAS)) {
            resultado = resultado.replace(PATRON_CANTIDADES_PEGADAS, "$1 $2, $3 $4")
            log("Separadas cantidades pegadas")
        }
        
        // MEJORA 5: Separar cantidades con espacio "6 zanaorias 5 zapatos"
        resultado = resultado.replace(PATRON_CANTIDADES_ESPACIADAS, "$1 $2, $3 $4")
        
        // MEJORA NUEVA: Separar fracciones mixtas "1/2papa1limon"
        if (resultado.contains(PATRON_FRACCIONES_MIXTAS)) {
            resultado = resultado.replace(PATRON_FRACCIONES_MIXTAS, "$1 $2, $3 $4")
            log("Separadas fracciones mixtas")
        }
        
        // MEJORA 6: Normalizar números en palabras (del parser 100%)
        resultado = normalizarNumerosEnPalabras(resultado)
        
        // MEJORA 7: Limpiar espacios múltiples (sin tocar saltos de línea) y comas repetidas
        resultado = resultado.replace(PATRON_ESPACIOS_MULTIPLES, " ")
        resultado = resultado.replace(PATRON_COMAS_REPETIDAS, ",")
        resultado = resultado.trim()
        
        // MEJORA 8: Eliminar líneas basura que contengan solo puntuación/espacios
        // Evita que entren líneas como "." al pipeline tras separar por puntos no decimales
        val antesLimpieza = resultado
        resultado = resultado
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.matches(Regex("^[\\p{Punct}\\s]+$")) }
            .joinToString("\n")
        if (resultado != antesLimpieza) {
            log("Líneas de solo puntuación/espacios eliminadas")
        }
        
        log("Texto preprocesado: ${resultado.take(100)}...")
        
        return resultado
    }

    /**
     * CONVERTIR PALABRAS NUMÉRICAS A NÚMEROS
     */
    fun convertirPalabraANumero(palabra: String): Double {
        return when (palabra.lowercase()) {
            "uno" -> 1.0
            "dos" -> 2.0
            "tres" -> 3.0
            "cuatro" -> 4.0
            "cinco" -> 5.0
            "seis" -> 6.0
            "siete" -> 7.0
            "ocho" -> 8.0
            "nueve" -> 9.0
            "diez" -> 10.0
            "once" -> 11.0
            "doce" -> 12.0
            "media", "medio" -> 0.5
            else -> 1.0
        }
    }

    /**
     * NORMALIZACIÓN AVANZADA DE TEXTO (del parser 100%)
     */
    private fun normalizarTexto(texto: String): String {
        var resultado = texto
        
        // Normalizar fracciones comunes
        resultado = resultado.replace(Regex("1/2"), "0.5")
        resultado = resultado.replace(Regex("1/4"), "0.25")
        resultado = resultado.replace(Regex("3/4"), "0.75")
        resultado = resultado.replace(Regex("1/3"), "0.33")
        resultado = resultado.replace(Regex("2/3"), "0.67")
        
        // Normalizar unidades
        resultado = resultado.replace(Regex("\\bkgs?\\b", RegexOption.IGNORE_CASE), "kg")
        resultado = resultado.replace(Regex("\\bkilo(s|gramo)?(s)?\\b", RegexOption.IGNORE_CASE), "kg")
        resultado = resultado.replace(Regex("\\bgramo(s)?\\b", RegexOption.IGNORE_CASE), "g")
        resultado = resultado.replace(Regex("\\blitro(s)?\\b", RegexOption.IGNORE_CASE), "l")
        resultado = resultado.replace(Regex("\\bunidad(es)?\\b", RegexOption.IGNORE_CASE), "unidad")
        resultado = resultado.replace(Regex("\\bpieza(s)?\\b", RegexOption.IGNORE_CASE), "unidad")
        
        // Limpiar espacios y caracteres especiales
        resultado = resultado.replace(Regex("[\\-_\\*\\+]+"), " ")
        resultado = resultado.replace(Regex("\\s+"), " ")
        resultado = resultado.trim()
        
        return resultado
    }
    
    private fun normalizarNumerosEnPalabras(texto: String): String {
        var resultado = texto
        
        // Convertir números en palabras a dígitos
        val numerosEnPalabras = mapOf(
            "uno" to "1",
            "una" to "1", 
            "dos" to "2",
            "tres" to "3",
            "cuatro" to "4",
            "cinco" to "5",
            "seis" to "6",
            "siete" to "7",
            "ocho" to "8",
            "nueve" to "9",
            "diez" to "10",
            "once" to "11",
            "doce" to "12",
            "media docena" to "6",
            "medio" to "0.5",
            "media" to "0.5"
        )
        
        for ((palabra, numero) in numerosEnPalabras) {
            resultado = resultado.replace(Regex("\\b$palabra\\b", RegexOption.IGNORE_CASE), numero)
        }
        
        return resultado
    }
}
