package com.listafacilnueva.parser

/**
 * 🔧 MÓDULO: Preprocesamiento de Texto
 * 
 * Responsable de todas las operaciones de limpieza y normalización
 * del texto de entrada antes del procesamiento principal.
 */
object TextPreprocessor {

    /**
     * Aplica mejoras básicas al texto para facilitar el parsing
     */
    fun aplicarMejorasAlTexto(texto: String): String {
        var resultado = texto
        
        // MEJORA 1: Corregir decimales con punto final
        val patronDecimalConPuntoFinal = Regex("(\\d+\\.\\d+)\\.$")
        if (resultado.contains(patronDecimalConPuntoFinal)) {
            resultado = resultado.replace(patronDecimalConPuntoFinal, "$1")
        }
        
        // MEJORA 2: Separar decimales pegados
        val patronDecimalesPegados = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})")
        if (resultado.contains(patronDecimalesPegados)) {
            resultado = resultado.replace(patronDecimalesPegados, "$1, $2")
        }
        
        // MEJORA 3: Separar productos consecutivos con decimales
        val patronProductosConsecutivos = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)")
        if (resultado.contains(patronProductosConsecutivos)) {
            resultado = resultado.replace(patronProductosConsecutivos, "$1 $2, $3 $4")
        }
        
        // MEJORA 4: Separar cantidades pegadas sin decimales
        val patronCantidadesPegadas = Regex("(\\d+)([a-zA-Záéíóúüñ]{3,})(\\d+)([a-zA-Záéíóúüñ]{3,})")
        if (resultado.contains(patronCantidadesPegadas)) {
            resultado = resultado.replace(patronCantidadesPegadas, "$1 $2, $3 $4")
        }
        
        // MEJORA 5: Limpiar espacios múltiples y comas repetidas
        resultado = resultado.replace(Regex("\\s+"), " ")
        resultado = resultado.replace(Regex(",+"), ",")
        resultado = resultado.trim()
        
        return resultado
    }

    /**
     * Extrae cantidad implícita del contexto de líneas anteriores
     */
    fun extraerCantidadImplicita(linea: String, indice: Int, todasLineas: List<String>): String {
        // Caso: "2 lechugas francesas preguntar...cual son..."
        // OBJETIVO: Preservar la pregunta como nota, no eliminarla
        
        // Verificar si la línea original tiene cantidad al inicio
        val patronCantidadInicio = Regex("^(\\d+)\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchInicio = patronCantidadInicio.find(linea)
        
        if (matchInicio != null) {
            val cantidad = matchInicio.groupValues[1]
            val resto = matchInicio.groupValues[2]
            
            // Si el resto contiene indicadores de continuación como "preguntar", PRESERVAR como nota
            if (resto.contains("preguntar", ignoreCase = true) || resto.contains("...", ignoreCase = true)) {
                // Extraer la parte del producto y la pregunta por separado
                val patronProductoConPregunta = Regex("^([a-zA-Záéíóúüñ\\s]+?)\\s+(preguntar[^.]*\\.\\.\\..*)", RegexOption.IGNORE_CASE)
                val matchProductoPregunta = patronProductoConPregunta.find(resto)
                
                if (matchProductoPregunta != null) {
                    val nombreProducto = matchProductoPregunta.groupValues[1].trim()
                    val pregunta = matchProductoPregunta.groupValues[2].trim()
                    
                    if (nombreProducto.isNotEmpty()) {
                        // Devolver el producto con la pregunta entre paréntesis como nota
                        return "$cantidad $nombreProducto ($pregunta)"
                    }
                } else {
                    // Si no se puede separar la pregunta, preservar todo
                    return linea
                }
            }
        }
        
        return linea
    }

    /**
     * Normaliza números escritos como palabras a formato numérico
     */
    fun normalizarNumeros(texto: String): String {
        var resultado = texto
        
        // Normalizar números escritos como palabras
        val palabrasNumericas = mapOf(
            "uno" to "1", "una" to "1", "dos" to "2", "tres" to "3", "cuatro" to "4",
            "cinco" to "5", "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9", "diez" to "10",
            "media docena" to "6", "una docena" to "12", "medio kilo" to "0.5", "un cuarto" to "0.25"
        )
        
        for ((palabra, numero) in palabrasNumericas) {
            val patron = Regex("\\b$palabra\\b", RegexOption.IGNORE_CASE)
            resultado = resultado.replace(patron, numero)
        }
        
        return resultado
    }

    /**
     * Convierte una palabra numérica a su valor Double equivalente
     */
    fun convertirPalabraANumero(palabra: String): Double {
        return when (palabra.lowercase()) {
            "uno", "una" -> 1.0
            "dos" -> 2.0
            "tres" -> 3.0
            "cuatro" -> 4.0
            "cinco" -> 5.0
            "seis" -> 6.0
            "siete" -> 7.0
            "ocho" -> 8.0
            "nueve" -> 9.0
            "diez" -> 10.0
            "media docena" -> 6.0
            "una docena" -> 12.0
            "medio kilo" -> 0.5
            "un cuarto" -> 0.25
            else -> 1.0
        }
    }
}
