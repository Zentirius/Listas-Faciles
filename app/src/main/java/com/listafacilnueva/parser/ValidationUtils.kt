package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * VALIDADOR DE UTILIDADES
 * 
 * Funciones para validar y filtrar líneas, fragmentos
 * y productos durante el procesamiento.
 */
object ValidationUtils {

    // Normalización básica: minúsculas, sin tildes/diacríticos, espacios colapsados
    private fun normalizarBasico(texto: String): String {
        val lower = texto.trim().lowercase()
        val decomposed = java.text.Normalizer.normalize(lower, java.text.Normalizer.Form.NFD)
        val sinDiacriticos = decomposed.replace(Regex("\\p{Mn}+"), "")
        return sinDiacriticos.replace(Regex("[\u0020\t\u00A0]+"), " ").trim()
    }

    /**
     * VALIDADOR DE LÍNEAS BASURA
     */
    fun esLineaBasura(linea: String): Boolean {
        val lineaLimpia = linea.trim().lowercase()
        return lineaLimpia.contains("no son cantidades") ||
               lineaLimpia.matches(Regex("^tipo\\s+\\d+.*")) ||
               lineaLimpia.isBlank() ||
               // Solo números (con posible punto/coma) y opcional punto final
               lineaLimpia.matches(Regex("^[0-9]+([.,][0-9]+)?\\.?$"))
    }

    /**
     * VALIDADOR DE FRAGMENTOS BASURA
     */
    fun esFragmentoBasura(fragmento: String): Boolean {
        val fragLimpio = fragmento.trim().lowercase()
        val fragNorm = normalizarBasico(fragmento)
        
        // Fragmentos muy cortos o vacíos - CORREGIDO: Permitir fragmentos de 1+ caracteres
        if (fragLimpio.isBlank() || fragLimpio.length < 1) return true
        
        // Solo puntuación/espacios
        if (fragmento.trim().matches(Regex("^[\\p{Punct}\\s]+$"))) return true

        // Solo números (con posible punto/coma) y opcional punto final
        if (fragLimpio.matches(Regex("^[0-9]+([.,][0-9]+)?\\.?$"))) return true

        // Frases comunes que no son producto (falsos positivos)
        val frasesNoProducto = setOf(
            "cual son",
            "cual es mejor",
            "cual es"
        )
        val fragSinPuntFinal = fragNorm.replace(Regex("[\\p{Punct}]+$"), "").trim()
        if (frasesNoProducto.contains(fragSinPuntFinal) || frasesNoProducto.any { fragSinPuntFinal.startsWith(it) }) return true

        // Cualquier fragmento que contenga "preguntar" se considera nota, no producto
        if (fragNorm.contains("preguntar")) return true

        // Paréntesis huérfanos (fragmentos como "(es" o "tubo de pasta dental)"), suelen ser restos de notas
        val contieneApertura = fragLimpio.contains('(')
        val contieneCierre = fragLimpio.contains(')')
        if (contieneApertura.xor(contieneCierre)) {
            // Si solo hay un lado del paréntesis y el fragmento es relativamente corto, descartarlo
            if (fragLimpio.length <= 30) return true
        }

        // MEJORA: Filtrar fragmentos que empiecen con preposiciones
        if (fragLimpio.startsWith("de ") || 
            fragLimpio.startsWith("en ") || 
            fragLimpio.startsWith("y ") ||
            fragLimpio.startsWith("o ") ||
            fragLimpio.startsWith("con ")) return true
            
        // MEJORA: Filtrar fragmentos que sean solo preposiciones + sustantivo
        val patronPreposicion = Regex("^(de|en|y|o|con)\\s+[\\p{L}]+$", RegexOption.IGNORE_CASE)
        if (patronPreposicion.matches(fragLimpio)) return true
        
        // MEJORA: Filtrar fragmentos que sean solo conjunciones
        val conjunciones = setOf("y", "o", "de", "en", "con", "para", "por")
        if (conjunciones.contains(fragLimpio)) return true
        
        return false
    }

    /**
     * VALIDADOR DE PRODUCTOS VÁLIDOS
     */
    fun esProductoValido(producto: Producto): Boolean {
        return producto.nombre.trim().isNotBlank() && producto.nombre.trim().length >= 1 // CORREGIDO: Permitir productos de 1+ caracteres
    }

    /**
     * Verifica si el nombre se considera "no producto" por frases comunes o paréntesis huérfanos.
     */
    fun esNombreNoProducto(nombre: String): Boolean {
        val n = nombre.trim().lowercase()
        val norm = normalizarBasico(nombre)
        if (n.isBlank()) return true

        // Solo puntuación/espacios
        if (nombre.trim().matches(Regex("^[\\p{Punct}\\s]+$"))) return true

        // Frases comunes no-producto
        val frasesNoProducto = setOf(
            "cual son",
            "cual es mejor",
            "cual es"
        )
        val nombreSinPuntFinal = norm.replace(Regex("[\\p{Punct}]+$"), "").trim()
        if (frasesNoProducto.contains(nombreSinPuntFinal) || frasesNoProducto.any { nombreSinPuntFinal.startsWith(it) }) return true

        // "preguntar" en el nombre implica nota
        if (norm.contains("preguntar")) return true

        // Paréntesis huérfanos
        val contieneApertura = n.contains('(')
        val contieneCierre = n.contains(')')
        if (contieneApertura.xor(contieneCierre) && n.length <= 30) return true

        return false
    }

    /**
     * Filtra una lista de productos, removiendo nombres no-producto y valores inválidos.
     */
    fun filtrarProductosBasura(productos: List<Producto>): List<Producto> {
        return productos.filter { p -> esProductoValido(p) && !esNombreNoProducto(p.nombre) }
    }
}
