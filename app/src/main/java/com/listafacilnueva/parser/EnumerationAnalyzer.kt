package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * ANALIZADOR DE NUMERACIÓN DE LISTA
 * 
 * Funciones para detectar y limpiar numeración de lista
 * como "1. producto", "2. producto", etc.
 */
object EnumerationAnalyzer {

    private var DEBUG_MODE = false
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            println(" ENUMERATION: $message")
        }
    }

    fun tieneSecuenciaEnumeracion(texto: String): Boolean {
        if (texto.isBlank()) return false
        
        // Optimización: casos críticos de cantidades decimales primero
        if (contieneCantidadesDecimalesReales(texto)) {
            return false
        }
        
        // Analizar patrones complejos
        if (analizarPatronesComplejos(texto)) {
            return true
        }
        
        // Extraer números de secuencia
        val numeros = extraerNumerosDeSecuencia(texto)
        
        if (numeros.isNotEmpty()) {
            return esSecuenciaConsecutivaValida(numeros)
        }
        
        return false
    }

    private fun extraerNumerosDeSecuencia(texto: String): List<Int> {
        val numeros = mutableListOf<Int>()
        
        // Patrón para numeración: "1. item", "2) item", "1- item"
        val patronNumeracion = Regex("(?:^|\\n)\\s*(\\d+)\\s*[.)\\-]\\s*[a-zA-Záéíóúüñ]", RegexOption.MULTILINE)
        
        patronNumeracion.findAll(texto).forEach { match ->
            match.groupValues[1].toIntOrNull()?.let { numero ->
                numeros.add(numero)
            }
        }
        
        // Patrón para listas simples: "1canela", "2nueces" (sin espacios)
        val patronListaSimple = Regex("(?:^|\\n)\\s*(\\d+)([a-zA-Záéíóúüñ]+)", RegexOption.MULTILINE)
        
        patronListaSimple.findAll(texto).forEach { match ->
            val numero = match.groupValues[1].toIntOrNull()
            val palabra = match.groupValues[2]
            
            // Solo si es una palabra válida (no decimal)
            if (numero != null && palabra.length >= 2 && !palabra.contains(".")) { // CORREGIDO: Permitir palabras de 2+ caracteres
                numeros.add(numero)
            }
        }
        
        return numeros.distinct().sorted()
    }

    private fun esSecuenciaConsecutivaValida(numeros: List<Int>): Boolean {
        if (numeros.size < 2) return false
        
        // Verificar si es una secuencia consecutiva válida
        for (i in 1 until numeros.size) {
            if (numeros[i] != numeros[i - 1] + 1) {
                return false
            }
        }
        
        // Debe empezar desde 1 o ser una secuencia razonable
        return numeros.first() <= 3 && numeros.size >= 2
    }

    private fun contieneCantidadesDecimalesReales(texto: String): Boolean {
        // Patrones de cantidades decimales reales
        val patronesDecimales = listOf(
            Regex("\\d+\\.\\d+\\s*(kg|gr|gramos|litros?|metros?|ml|cm)\\b", RegexOption.IGNORE_CASE),
            Regex("\\d+\\.\\d+\\s*(de\\s+)?[a-zA-Záéíóúüñ]+", RegexOption.IGNORE_CASE),
            Regex("\\b\\d+\\.\\d+\\s*[a-zA-Záéíóúüñ]+", RegexOption.IGNORE_CASE)
        )
        
        return patronesDecimales.any { it.containsMatchIn(texto) }
    }

    private fun analizarPatronesComplejos(texto: String): Boolean {
        // CASO: "1.canela" vs "1.5 tomates"
        val lineas = texto.split(Regex("[\\n;]+")).map { it.trim() }.filter { it.isNotBlank() }
        
        var contadorNumeracion = 0
        
        for (linea in lineas) {
            // Patrón específico para numeración: "1.item" (sin espacio después del punto)
            val patronNum = Regex("^(\\d+)\\.([\\p{L}]+)", RegexOption.IGNORE_CASE)
            val match = patronNum.find(linea.trim())
            
            if (match != null) {
                val numero = match.groupValues[1].toIntOrNull()
                val palabra = match.groupValues[2]
                
                // Es numeración si: número pequeño + palabra larga + sin decimales en el resto
                if (numero != null && numero <= 10 && palabra.length >= 2 && // CORREGIDO: Permitir palabras de 2+ caracteres
                    !linea.contains(Regex("\\d+\\.\\d+"))) {
                    contadorNumeracion++
                }
            }
        }
        
        return contadorNumeracion >= 2
    }

    fun limpiarNumeracionCompuesta(linea: String): String {
        // Limpiar numeraciones como "1.item" → "item"
        val patronLimpieza = Regex("^\\s*(\\d+)\\s*[.)\\-]\\s*", RegexOption.IGNORE_CASE)
        return patronLimpieza.replace(linea.trim(), "").trim()
    }

    fun limpiarNumeracionLista(linea: String): String {
        // Limpiar listas simples como "1item" → "1 item" (conservar número y añadir espacio)
        val patronSimple = Regex("^\\s*(\\d+)([a-zA-Záéíóúüñ]+)(.*)$", RegexOption.IGNORE_CASE)
        val match = patronSimple.find(linea.trim())

        return if (match != null) {
            val numero = match.groupValues[1].toIntOrNull()
            val palabra = match.groupValues[2]
            val resto = match.groupValues[3] // conserva el resto original (incluye espacios si los había)

            // Solo normalizar si parece numeración (número pequeño)
            if (numero != null && numero <= 20) {
                // Conservar el número e insertar espacio entre número y palabra, p.ej. "1malla tomates" -> "1 malla tomates"
                ("${match.groupValues[1]} " + palabra + resto).trim()
            } else {
                linea.trim()
            }
        } else {
            linea.trim()
        }
    }

    /**
     * PROCESAR NUMERACIÓN DE LISTA - VERSIÓN MEJORADA
     */
    fun procesarNumeracion(texto: String): List<Producto> {
        log("Procesando numeración: '$texto'")
        
        val productos = mutableListOf<Producto>()
        
        // CASO CRÍTICO: "1.1papa mediana2.sandia grande3.zapallo chino"
        val patronDecimalPegado = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s*([a-zA-Záéíóúüñ\\s]*?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchDecimalPegado = patronDecimalPegado.find(texto)
        if (matchDecimalPegado != null) {
            val cantidad1 = matchDecimalPegado.groupValues[2]
            val nombre1 = matchDecimalPegado.groupValues[3]
            val descripcion1 = matchDecimalPegado.groupValues[4].trim()
            val nombre2 = matchDecimalPegado.groupValues[6].trim()
            val nombre3 = matchDecimalPegado.groupValues[8].trim()
            
            val producto1 = Producto(
                nombre = if (descripcion1.isNotEmpty()) "$nombre1 $descripcion1" else nombre1,
                cantidad = cantidad1.toDoubleOrNull() ?: 1.0,
                original = "$cantidad1.$nombre1 $descripcion1"
            )
            
            val producto2 = Producto(
                nombre = nombre2,
                cantidad = 1.0,
                original = "2.$nombre2"
            )
            
            val producto3 = Producto(
                nombre = nombre3,
                cantidad = 1.0,
                original = "3.$nombre3"
            )
            
            productos.addAll(listOf(producto1, producto2, producto3))
            log("Procesados 3 productos con numeración decimal: ${productos.size}")
            return productos
        }
        
        // CASO: "1.salmon 1kg2.zanaorias"
        val patronDosProductos = Regex("^(\\d+)\\.([^\\d]*\\d*[^\\d]*?)(\\d+)\\.(.+)$", RegexOption.IGNORE_CASE)
        val matchDos = patronDosProductos.find(texto)
        if (matchDos != null) {
            val producto1 = matchDos.groupValues[2].trim()
            val producto2 = matchDos.groupValues[4].trim()
            
            // Extraer cantidad del primer producto si tiene
            val patronCantidad = Regex("(\\d+(?:\\.\\d+)?)\\s*([\\p{L}]+)").find(producto1)
            if (patronCantidad != null) {
                val cantidad = patronCantidad.groupValues[1].toDoubleOrNull() ?: 1.0
                val nombre = patronCantidad.groupValues[2]
                productos.add(Producto(nombre = nombre, cantidad = cantidad, original = producto1))
            } else {
                productos.add(Producto(nombre = producto1, cantidad = 1.0, original = producto1))
            }
            
            productos.add(Producto(nombre = producto2, cantidad = 1.0, original = producto2))
            log("Procesados 2 productos con numeración: ${productos.size}")
            return productos
        }
        
        // CASO GENERAL: Dividir por números de lista
        val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE).findAll(texto).toList()
        if (numerosConPunto.size >= 2) {
            for (i in numerosConPunto.indices) {
                val inicioActual = numerosConPunto[i].range.first
                val finActual = if (i < numerosConPunto.size - 1) {
                    numerosConPunto[i + 1].range.first
                } else {
                    texto.length
                }
                
                val contenidoProducto = texto.substring(inicioActual, finActual).trim()
                val productoLimpio = contenidoProducto.replace(Regex("^\\d+\\.\\s*"), "").trim()
                
                if (productoLimpio.isNotEmpty()) {
                    // Intentar extraer cantidad del contenido
                    val patronCantidadEnContenido = Regex("(\\d+(?:\\.\\d+)?)\\s*([\\p{L}]+)").find(productoLimpio)
                    if (patronCantidadEnContenido != null) {
                        val cantidad = patronCantidadEnContenido.groupValues[1].toDoubleOrNull() ?: 1.0
                        val nombre = patronCantidadEnContenido.groupValues[2]
                        productos.add(Producto(nombre = nombre, cantidad = cantidad, original = productoLimpio))
                    } else {
                        productos.add(Producto(nombre = productoLimpio, cantidad = 1.0, original = productoLimpio))
                    }
                }
            }
        }
        
        log("Procesados ${productos.size} productos con numeración general")
        return productos
    }
}
