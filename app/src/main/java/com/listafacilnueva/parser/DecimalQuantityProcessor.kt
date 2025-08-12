package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * 🔢 MÓDULO: Procesador de Cantidades Decimales
 * 
 * Responsable de detectar y procesar cantidades decimales con unidades,
 * distinguiendo entre cantidades reales y numeración de listas.
 */
object DecimalQuantityProcessor {

    /**
     * Analiza y procesa cantidades decimales en el texto
     * CRÍTICO: Verifica numeración de lista antes de procesar como decimales
     */
    fun analizarCantidadesDecimales(texto: String): List<Producto> {
        println("🔢 Analizando cantidades decimales en: '$texto'")
        
        // VERIFICACIÓN CRÍTICA: Antes de procesar como decimales, verificar si es numeración mixta
        val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE)
            .findAll(texto)
            .map { it.groupValues[1].toInt() }
            .toList()
        
        // Si hay múltiples números en secuencia consecutiva (1, 2, 3...), probablemente es numeración de lista
        if (numerosConPunto.size >= 2) {
            var esSecuenciaConsecutiva = true
            for (i in 1 until numerosConPunto.size) {
                if (numerosConPunto[i] != numerosConPunto[i-1] + 1) {
                    esSecuenciaConsecutiva = false
                    break
                }
            }
            
            if (esSecuenciaConsecutiva && numerosConPunto[0] in 1..10) {
                println("🔍 NUMERACIÓN DE LISTA DETECTADA: ${numerosConPunto.size} productos (${numerosConPunto.joinToString(", ")})")
                println("🔍 NO procesar como cantidades decimales - delegar a numeración")
                return emptyList() // Dejar que lo procese limpiarNumeracionCompuesta
            }
        }
        
        // CASO CRÍTICO: "1.2metros de madera 3.8 metros de cable" 
        // Patrón mejorado para detectar múltiples cantidades decimales con unidades
        val patronDecimalesConUnidades = Regex("(\\d+\\.\\d+)\\s*(metros?|kg|gramos?|g|litros?|l|ml|cm|mm|bolsas?|paquetes?)\\s+(?:de\\s+)?([a-zA-Záéíóúüñ\\s]+?)(?=\\s+\\d+\\.\\d+|,|$)", RegexOption.IGNORE_CASE)
        val matchesConUnidades = patronDecimalesConUnidades.findAll(texto).toList()
        
        if (matchesConUnidades.size >= 2) {
            val productos = mutableListOf<Producto>()
            println("🎯 Detectadas ${matchesConUnidades.size} cantidades decimales con unidades")
            
            for ((index, match) in matchesConUnidades.withIndex()) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].lowercase()
                val nombre = match.groupValues[3].trim()
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = when(unidad) {
                            "kg", "kilogramos" -> "kg"
                            "g", "gramos", "gr" -> "g"
                            "l", "litros" -> "l"
                            "m", "metros" -> "m"
                            "ml", "mililitros" -> "ml"
                            "cm", "centímetros" -> "cm"
                            else -> unidad
                        }
                    )
                    productos.add(producto)
                    println("✅ Producto decimal ${index + 1}: ${producto.nombre} - ${producto.cantidad}${producto.unidad}")
                }
            }
            
            if (productos.size >= 2) {
                return productos
            }
        }
        
        // CASO ADICIONAL: Detectar cantidades decimales separadas por comas con múltiples puntos como separadores
        // "1.5 litros de jugo de naranja,,,2 latas de atún."
        val patronComaSeparador = Regex("(\\d+\\.\\d+)\\s*(\\w*)\\s+(?:de\\s+)?([^,;]+?)(?:[,;]{1,3}|$)", RegexOption.IGNORE_CASE)
        val matchesComa = patronComaSeparador.findAll(texto).toList()
        
        if (matchesComa.size >= 2) {
            val productos = mutableListOf<Producto>()
            println("🎯 Detectadas ${matchesComa.size} cantidades decimales separadas por comas")
            
            for ((index, match) in matchesComa.withIndex()) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].takeIf { it.isNotBlank() }
                val nombre = match.groupValues[3].trim().removeSuffix(".")
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = unidad?.let { ParserUtils.normalizarUnidad(it) }
                    )
                    productos.add(producto)
                    println("✅ Producto coma-separado ${index + 1}: ${producto.nombre} - ${producto.cantidad}${producto.unidad ?: ""}")
                }
            }
            
            if (productos.size >= 2) {
                return productos
            }
        }
        
        // Patrón original para compatibilidad
        val patronDecimales = Regex("(\\d+\\.\\d+)\\s*(kg|gramos?|g|litros?|l|metros?|m)\\s+([a-zA-Záéíóúüñ\\s]+?)(?=\\s*\\d+\\.\\d+|$)", RegexOption.IGNORE_CASE)
        val matches = patronDecimales.findAll(texto).toList()
        
        if (matches.size >= 2) {
            val productos = mutableListOf<Producto>()
            
            for (match in matches) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].lowercase()
                val nombre = match.groupValues[3].trim()
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = when(unidad) {
                            "kg", "kilogramos" -> "kg"
                            "g", "gramos", "gr" -> "g"
                            "l", "litros" -> "l"
                            "m", "metros" -> "m"
                            else -> unidad
                        }
                    )
                    productos.add(producto)
                    println("✅ Producto decimal original: ${producto.nombre} - ${producto.cantidad}${producto.unidad}")
                }
            }
            
            if (productos.size >= 2) {
                println("🎯 Múltiples productos decimales detectados: ${productos.size}")
                return productos
            }
        }
        
        return emptyList()
    }
}
