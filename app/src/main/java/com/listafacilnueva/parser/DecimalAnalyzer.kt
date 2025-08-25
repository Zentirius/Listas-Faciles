package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * ANALIZADOR DE CANTIDADES DECIMALES
 * 
 * Funciones para detectar y procesar cantidades decimales
 * como "1.5 litros", "2.3 kg", etc.
 */
object DecimalAnalyzer {

    private var DEBUG_MODE = false
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            println(" DECIMAL: $message")
        }
    }

    /**
     * Analiza texto en busca de múltiples cantidades decimales (VERSIÓN MEJORADA)
     */
    fun analizarCantidadesDecimales(texto: String): List<Producto> {
        val productos = mutableListOf<Producto>()
        
        // MEJORA 1: Patrones más sofisticados para decimales
        val patronesDecimales = listOf(
            // Patrón para "1.5kg pollo, 2.3l leche"
            Regex("(\\d+[.,]\\d+)\\s*(kg|g|l|ml|m|cm|u|unidades?|piezas?)\\s+([a-zA-Záéíóúüñ][a-zA-Záéíóúüñ\\s]*?)(?=[,;]|\\d+[.,]\\d+|$)", RegexOption.IGNORE_CASE),
            
            // Patrón para decimales pegados: "1.2papa"
            Regex("(\\d+[.,]\\d+)([a-zA-Záéíóúüñ]{2,})(\\s+[a-zA-Záéíóúüñ]+)?", RegexOption.IGNORE_CASE),
            
            // Patrón para "1.5 de pollo, 2.3 de leche"
            Regex("(\\d+[.,]\\d+)\\s+(?:de\\s+)?([a-zA-Záéíóúüñ][a-zA-Záéíóúüñ\\s]*?)(?=[,;]|\\d+[.,]\\d+|$)", RegexOption.IGNORE_CASE)
        )
        
        for (patron in patronesDecimales) {
            val matches = patron.findAll(texto)
            for (match in matches) {
                when (patron) {
                    patronesDecimales[0] -> { // Con unidades
                        val cantidad = match.groupValues[1].replace(",", ".").toDoubleOrNull()
                        val unidad = match.groupValues[2]
                        val nombre = match.groupValues[3].trim()
                        
                        if (cantidad != null && nombre.isNotEmpty()) {
                            productos.add(Producto(
                                nombre = nombre,
                                cantidad = cantidad,
                                unidad = normalizarUnidadAvanzada(unidad)
                            ))
                        }
                    }
                    patronesDecimales[1] -> { // Decimales pegados
                        val cantidad = match.groupValues[1].replace(",", ".").toDoubleOrNull()
                        val producto = match.groupValues[2]
                        val adjetivo = match.groupValues[3].trim()
                        
                        if (cantidad != null && producto.isNotEmpty()) {
                            val nombreCompleto = if (adjetivo.isNotEmpty()) "$producto $adjetivo" else producto
                            productos.add(Producto(
                                nombre = nombreCompleto,
                                cantidad = cantidad,
                                unidad = null
                            ))
                        }
                    }
                    patronesDecimales[2] -> { // Sin unidades explícitas
                        val cantidad = match.groupValues[1].replace(",", ".").toDoubleOrNull()
                        val nombre = match.groupValues[2].trim()
                        
                        if (cantidad != null && nombre.isNotEmpty()) {
                            productos.add(Producto(
                                nombre = nombre,
                                cantidad = cantidad,
                                unidad = detectarUnidadImplicita(nombre)
                            ))
                        }
                    }
                }
            }
            
            if (productos.size >= 2) break // Si encontramos múltiples productos, usamos el primer patrón exitoso
        }
        
        return productos.distinctBy { "${it.nombre}_${it.cantidad}_${it.unidad ?: ""}" } // Eliminar duplicados considerando unidad
    }
    
    /**
     * NORMALIZACIÓN AVANZADA DE UNIDADES
     */
    private fun normalizarUnidadAvanzada(unidad: String): String {
        return when (unidad.lowercase()) {
            "kg", "kilo", "kilos", "kilogramo", "kilogramos" -> "kg"
            "g", "gr", "gramo", "gramos" -> "g"
            "l", "litro", "litros" -> "l"
            "ml", "mililitro", "mililitros" -> "ml"
            "m", "metro", "metros" -> "m"
            "cm", "centimetro", "centimetros", "centímetro", "centímetros" -> "cm"
            "u", "unidad", "unidades", "pieza", "piezas" -> "unidad"
            else -> unidad
        }
    }
    
    /**
     * DETECCIÓN DE UNIDAD IMPLÍCITA
     */
    private fun detectarUnidadImplicita(nombre: String): String? {
        val nombreLower = nombre.lowercase()
        return when {
            nombreLower.contains("leche") || nombreLower.contains("agua") || nombreLower.contains("jugo") -> "l"
            nombreLower.contains("carne") || nombreLower.contains("pollo") || nombreLower.contains("pescado") -> "kg"
            nombreLower.contains("huevo") || nombreLower.contains("manzana") || nombreLower.contains("naranja") -> "unidad"
            else -> null
        }
    }
}
