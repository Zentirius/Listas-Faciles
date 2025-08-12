package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * 🎯 MÓDULO: Extractor de Cantidades
 * 
 * Responsable de procesar fragmentos individuales y extraer
 * cantidades, unidades y nombres de productos.
 */
object QuantityExtractor {

    /**
     * Clase de datos para resultados de extracción
     */
    data class Extraccion(
        val cantidad: Double,
        val unidad: String?,
        val textoRestante: String
    )

    /**
     * Procesa un fragmento y crea un producto
     */
    fun procesarFragmento(fragmento: String): Producto? {
        if (ValidationUtils.esFragmentoBasura(fragmento)) return null
        
        var texto = fragmento.trim()
        var nota: String? = null
        var marcas = mutableListOf<String>()
        
        // MEJORA CRÍTICA: Extraer marcas con patrón mejorado para casos como "(marca: Frutos del Maipo o Minuto Verde)"
        val marcaRegexComplejo = Regex("\\(marca[s]?:\\s*([^)]+)\\)", RegexOption.IGNORE_CASE)
        val marcaMatchComplejo = marcaRegexComplejo.find(texto)
        if (marcaMatchComplejo != null) {
            val marcasTexto = marcaMatchComplejo.groupValues[1].trim()
            // Separar marcas por " o "
            val marcasList = marcasTexto.split(Regex("\\s+o\\s+")).map { it.trim() }
            marcas.addAll(marcasList)
            texto = texto.replace(marcaMatchComplejo.value, "").trim()
        } else {
            // MEJORA: Extraer marcas con patrón genérico "marca X"
            val marcaRegex = Regex("marca\\s+([\\w\\s\\-]+)", RegexOption.IGNORE_CASE)
            val marcaMatch = marcaRegex.find(texto)
            if (marcaMatch != null) {
                val marca = marcaMatch.groupValues[1].trim()
                marcas.add(marca)
                texto = texto.replace(marcaMatch.value, "").trim()
            }
        }
        
        // Extraer nota entre paréntesis (que no sea marca)
        val notaMatch = Regex("\\(([^)]+)\\)").find(texto)
        if (notaMatch != null && !notaMatch.value.contains("marca", ignoreCase = true)) {
            nota = notaMatch.groupValues[1].trim()
            texto = texto.replace(notaMatch.value, "").trim()
        }
        
        // Extraer cantidad y unidad
        val extraccion = extraerCantidadYUnidad(texto)
        val nombreProducto = extraccion.textoRestante.trim()
        
        if (nombreProducto.isEmpty()) return null
        
        // Verificar que no sea una marca conocida como producto independiente
        if (ParserUtils.marcasConocidas.any { nombreProducto.lowercase().contains(it) }) {
            return null
        }
        
        val producto = Producto(
            nombre = nombreProducto,
            cantidad = extraccion.cantidad,
            unidad = extraccion.unidad,
            nota = nota,
            marcas = if (marcas.isNotEmpty()) marcas else null
        )
        
        return if (ValidationUtils.esProductoValido(producto)) producto else null
    }

    /**
     * Extrae cantidad y unidad de un texto
     */
    fun extraerCantidadYUnidad(texto: String): Extraccion {
        var textoLimpio = texto.trim()
        
        // PATRÓN 1: Cantidad al inicio (2 kg arroz, 1.5 litros leche)
        val patronInicio = Regex("^(\\d+(?:[.,]\\d+)?)\\s*(${ParserUtils.unidades.joinToString("|")})\\s+(.+)$", RegexOption.IGNORE_CASE)
        val matchInicio = patronInicio.find(textoLimpio)
        
        if (matchInicio != null) {
            val cantidadStr = matchInicio.groupValues[1].replace(',', '.')
            val cantidad = cantidadStr.toDoubleOrNull() ?: 1.0
            val unidad = ParserUtils.normalizarUnidad(matchInicio.groupValues[2])
            val nombre = matchInicio.groupValues[3].trim()
            
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // PATRÓN 2: Cantidad integrada en el nombre (arroz 2kg, leche 1.5litros)
        val patronIntegrado = Regex("^(.+?)\\s+(\\d+(?:[.,]\\d+)?)(${ParserUtils.unidades.joinToString("|")})$", RegexOption.IGNORE_CASE)
        val matchIntegrado = patronIntegrado.find(textoLimpio)
        
        if (matchIntegrado != null) {
            val nombre = matchIntegrado.groupValues[1].trim()
            val cantidadStr = matchIntegrado.groupValues[2].replace(',', '.')
            val cantidad = cantidadStr.toDoubleOrNull() ?: 1.0
            val unidad = ParserUtils.normalizarUnidad(matchIntegrado.groupValues[3])
            
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // PATRÓN 3: Solo cantidad al inicio sin unidad (2 manzanas)
        val patronSoloCantidad = Regex("^(\\d+(?:[.,]\\d+)?)\\s+(.+)$")
        val matchSoloCantidad = patronSoloCantidad.find(textoLimpio)
        
        if (matchSoloCantidad != null) {
            val cantidadStr = matchSoloCantidad.groupValues[1].replace(',', '.')
            val cantidad = cantidadStr.toDoubleOrNull() ?: 1.0
            val nombre = matchSoloCantidad.groupValues[2].trim()
            
            return Extraccion(cantidad, null, nombre)
        }
        
        // PATRÓN 4: Cantidad con unidad pegada (2kg, 1.5litros) 
        val patronPegado = Regex("^(\\d+(?:[.,]\\d+)?)(${ParserUtils.unidades.joinToString("|")})\\s+(.+)$", RegexOption.IGNORE_CASE)
        val matchPegado = patronPegado.find(textoLimpio)
        
        if (matchPegado != null) {
            val cantidadStr = matchPegado.groupValues[1].replace(',', '.')
            val cantidad = cantidadStr.toDoubleOrNull() ?: 1.0
            val unidad = ParserUtils.normalizarUnidad(matchPegado.groupValues[2])
            val nombre = matchPegado.groupValues[3].trim()
            
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // PATRÓN 5: Palabras numéricas (dos manzanas, media docena huevos)
        val palabrasNumericas = ParserUtils.palabrasNumericas.keys.toList().sortedByDescending { it.length }
        for (palabra in palabrasNumericas) {
            val patron = Regex("^$palabra\\s+(.+)$", RegexOption.IGNORE_CASE)
            val match = patron.find(textoLimpio)
            if (match != null) {
                val cantidad = TextPreprocessor.convertirPalabraANumero(palabra)
                val nombre = match.groupValues[1].trim()
                return Extraccion(cantidad, null, nombre)
            }
        }
        
        // PATRÓN 6: Unidad al final sin cantidad (arroz kg)
        val patronUnidadFinal = Regex("^(.+?)\\s+(${ParserUtils.unidades.joinToString("|")})$", RegexOption.IGNORE_CASE)
        val matchUnidadFinal = patronUnidadFinal.find(textoLimpio)
        
        if (matchUnidadFinal != null) {
            val nombre = matchUnidadFinal.groupValues[1].trim()
            val unidad = ParserUtils.normalizarUnidad(matchUnidadFinal.groupValues[2])
            
            return Extraccion(1.0, unidad, nombre)
        }
        
        // DEFAULT: Sin cantidad explícita, asumir 1
        return Extraccion(1.0, null, textoLimpio)
    }
}
