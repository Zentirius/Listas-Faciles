package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

object QuantityParser {

    fun parse(texto: String): List<Producto> {
        val productos = mutableListOf<Producto>()
        val textoNormalizado = normalizarNumeros(texto)
        val lineas = textoNormalizado.split(Regex("[\n;]+")).filter { it.isNotBlank() }
        
        for (linea in lineas) {
            println("Procesando línea: '$linea'")
            
            // Filtrar líneas basura
            if (esLineaBasura(linea)) continue
            
            // MEJORA DEL AMIGO: Preprocesar numeración compuesta ANTES de limpiar numeración simple
            val lineaLimpia = limpiarNumeracionLista(limpiarNumeracionCompuesta(linea))
            
            // Procesar línea
            val fragmentos = dividirEnFragmentos(lineaLimpia)
            for (fragmento in fragmentos) {
                val producto = procesarFragmento(fragmento)
                if (producto != null && esProductoValido(producto)) {
                    productos.add(producto)
                }
            }
        }
        
        return productos
    }

    /**
     * MEJORA SUGERIDA POR AMIGO: Detecta y separa patrones como 1.2metros cable2.bombilla grande
     * Transforma: "1.2metros cable2.bombilla grande" → "2 metros cable, bombilla grande"
     */
    private fun limpiarNumeracionCompuesta(linea: String): String {
        // Detecta y separa patrones como 1.2metros cable2.bombilla grande
        val patron = Regex("^(\\d+)\\.(\\d+)\\s*([a-zA-Záéíóúüñ]+)\\s+(.*?)\\s*(\\d+)\\.\\s*(.+)$", RegexOption.IGNORE_CASE)
        val match = patron.find(linea)
        return if (match != null) {
            val cantidad1 = match.groupValues[2]
            val unidad1 = match.groupValues[3]
            val nombre1 = match.groupValues[4]
            val nombre2 = match.groupValues[6]
            "$cantidad1 $unidad1 $nombre1, $nombre2"
        } else {
            linea
        }
    }
    
    private fun limpiarNumeracionLista(linea: String): String {
        // Limpiar numeración simple como "1.", "2."
        return linea.replace(Regex("^\\d+\\.\\s*"), "").trim()
    }

    private fun dividirEnFragmentos(linea: String): List<String> {
        // Manejar líneas con "marca" primero
        if (linea.contains("marca", ignoreCase = true)) {
            return procesarLineaConMarca(linea)
        }
        
        // MEJORA: Segmentación inteligente por conjunciones
        var fragmentos = dividirPorSeparadores(linea)
        
        // Separar cantidades pegadas y múltiples productos
        val fragmentosExpandidos = mutableListOf<String>()
        for (frag in fragmentos) {
            val separados = separarMultiplesProductos(frag)
            fragmentosExpandidos.addAll(separados)
        }
        
        return fragmentosExpandidos
    }
    
    // NUEVA FUNCIÓN: División inteligente por separadores
    private fun dividirPorSeparadores(linea: String): List<String> {
        // Dividir por comas y punto y coma siempre
        var fragmentos = linea.split(Regex("[,;]\\s*")).map { it.trim() }.filter { it.isNotBlank() }
        
        // Para cada fragmento, verificar si debe dividirse por "y"
        val fragmentosFinales = mutableListOf<String>()
        for (fragmento in fragmentos) {
            if (fragmento.contains(" y ", ignoreCase = true)) {
                // Solo dividir por "y" si hay cantidades separadas en ambos lados
                val partesY = fragmento.split(Regex("\\s+y\\s+", RegexOption.IGNORE_CASE))
                if (partesY.size == 2 && tieneCantidadesSeparadas(partesY[0], partesY[1])) {
                    fragmentosFinales.addAll(partesY.map { it.trim() })
                } else {
                    // No dividir, es un producto compuesto como "jamón y queso"
                    fragmentosFinales.add(fragmento)
                }
            } else {
                fragmentosFinales.add(fragmento)
            }
        }
        
        return fragmentosFinales.filter { it.isNotBlank() }
    }
    
    // NUEVA FUNCIÓN: Verificar si hay cantidades separadas
    private fun tieneCantidadesSeparadas(parte1: String, parte2: String): Boolean {
        val tieneNumero1 = Regex("\\d+").containsMatchIn(parte1)
        val tieneNumero2 = Regex("\\d+").containsMatchIn(parte2)
        return tieneNumero1 && tieneNumero2
    }

    private fun procesarLineaConMarca(linea: String): List<String> {
        val partes = linea.split(Regex("marca", RegexOption.IGNORE_CASE), 2)
        if (partes.size == 2) {
            val primeraParte = partes[0].trim().removeSuffix(",").trim()
            val marcasTexto = partes[1].trim().removePrefix(",").trim()
            
            // Extraer nota de marcas si existe
            val notaMatch = Regex("\\(([^)]+)\\)").find(marcasTexto)
            val marcasLimpias = if (notaMatch != null) {
                marcasTexto.replace(notaMatch.value, "").trim()
            } else {
                marcasTexto
            }
            
            val nota = notaMatch?.groupValues?.get(1)?.trim()
            val marcas = marcasLimpias.split(Regex("\\s+o\\s+|\\s*,\\s*"))
                .map { it.trim().removeSuffix(".") }
                .filter { it.isNotBlank() }
            
            // Crear producto con marcas
            val productoConMarcas = if (nota != null) {
                "$primeraParte (marcas: ${marcas.joinToString(", ")}) ($nota)"
            } else {
                "$primeraParte (marcas: ${marcas.joinToString(", ")})"
            }
            
            return listOf(productoConMarcas)
        }
        return listOf(linea)
    }

    private fun separarMultiplesProductos(fragmento: String): List<String> {
        // MEJORA: No dividir si contiene construcciones "X de Y"
        val patronConDe = Regex("\\d+(?:\\.\\d+)?\\s+\\w+\\s+de\\s+\\w+", RegexOption.IGNORE_CASE)
        if (patronConDe.containsMatchIn(fragmento)) {
            return listOf(fragmento) // No dividir, es una construcción completa
        }
        
        // Patrón para detectar múltiples productos como "cinco focos ocho rollos"
        val patronMultiple = Regex("(\\d+|[a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ]+)\\s+(\\d+|[a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ]+)", RegexOption.IGNORE_CASE)
        val matchMultiple = patronMultiple.find(fragmento)
        
        if (matchMultiple != null) {
            val cantidad1 = matchMultiple.groupValues[1]
            val nombre1 = matchMultiple.groupValues[2]
            val cantidad2 = matchMultiple.groupValues[3]
            val nombre2 = matchMultiple.groupValues[4]
            
            // Verificar si son realmente dos productos diferentes
            if (nombre1 != nombre2) {
                return listOf("$cantidad1 $nombre1", "$cantidad2 $nombre2")
            }
        }
        
        // Buscar patrones como "4cepillos" o "1.2metros"
        val patron = Regex("(\\d+(?:\\.\\d+)?)([a-zA-Záéíóúüñ]+)")
        val matches = patron.findAll(fragmento).toList()
        
        if (matches.size > 1) {
            // Múltiples patrones, separarlos
            val resultado = mutableListOf<String>()
            var ultimoIndice = 0
            
            for (match in matches) {
                val inicio = match.range.first
                val fin = match.range.last + 1
                
                // Agregar texto antes del match si existe
                if (inicio > ultimoIndice) {
                    val textoAntes = fragmento.substring(ultimoIndice, inicio).trim()
                    if (textoAntes.isNotBlank()) resultado.add(textoAntes)
                }
                
                // Agregar el match
                resultado.add(match.value)
                ultimoIndice = fin
            }
            
            // Agregar texto después del último match si existe
            if (ultimoIndice < fragmento.length) {
                val textoDespues = fragmento.substring(ultimoIndice).trim()
                if (textoDespues.isNotBlank()) resultado.add(textoDespues)
            }
            
            return resultado
        }
        
        return listOf(fragmento)
    }

    private fun procesarFragmento(fragmento: String): Producto? {
        if (esFragmentoBasura(fragmento)) return null
        
        var texto = fragmento.trim()
        var nota: String? = null
        var marcas = mutableListOf<String>()
        
        // MEJORA: Extraer marcas con patrón genérico "marca X"
        val marcaRegex = Regex("marca\\s+([\\w\\s\\-]+)", RegexOption.IGNORE_CASE)
        val marcaMatch = marcaRegex.find(texto)
        if (marcaMatch != null) {
            val marca = marcaMatch.groupValues[1].trim()
            marcas.add(marca)
            texto = texto.replace(marcaMatch.value, "").trim()
        }
        
        // Extraer nota entre paréntesis
        val notaMatch = Regex("\\((.*?)\\)").find(texto)
        if (notaMatch != null) {
            nota = notaMatch.groupValues[1].trim()
            texto = texto.replace(notaMatch.value, "").trim()
        }
        
        // Extraer cantidad y unidad
        val extraccion = extraerCantidadYUnidad(texto)
        
        return Producto(
            nombre = extraccion.nombre,
            cantidad = extraccion.cantidad,
            unidad = extraccion.unidad,
            marcas = marcas,
            nota = nota,
            original = fragmento
        )
    }

    private fun extraerCantidadYUnidad(texto: String): Extraccion {
        var nombre = texto
        var cantidad: Double? = null
        var unidad: String? = null
        
        // MEJORA CRÍTICA: Patrón cantidad + unidad + "de" + nombre (ej: "2 metros de cable")
        val patronConDe = Regex("^(\\d+(?:\\.\\d+)?)\\s+(\\w+)\\s+de\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchConDe = patronConDe.find(texto)
        if (matchConDe != null && ParserUtils.esUnidadConocida(matchConDe.groupValues[2])) {
            cantidad = matchConDe.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchConDe.groupValues[2])
            nombre = matchConDe.groupValues[3].trim()
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // MEJORA: Patrón cantidad + unidad + nombre (sin "de") (ej: "2 bolsas arroz")
        val patronCompleto = Regex("^(\\d+(?:\\.\\d+)?)\\s+(\\w+)\\s+(.*)")
        val matchCompleto = patronCompleto.find(texto)
        if (matchCompleto != null && ParserUtils.esUnidadConocida(matchCompleto.groupValues[2])) {
            cantidad = matchCompleto.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchCompleto.groupValues[2])
            nombre = matchCompleto.groupValues[3].trim()
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // Patrón: cantidad+unidad pegadas (ej: "1bolsa", "500ml")
        val patronPegado = Regex("^(\\d+(?:\\.\\d+)?)([a-zA-Záéíóúüñ]+)\\s*(.*)")
        val matchPegado = patronPegado.find(texto)
        if (matchPegado != null) {
            val cant = matchPegado.groupValues[1].toDoubleOrNull()
            val unid = matchPegado.groupValues[2]
            val resto = matchPegado.groupValues[3]
            
            // Verificar si es unidad conocida
            if (ParserUtils.esUnidadConocida(unid)) {
                cantidad = cant
                unidad = ParserUtils.normalizarUnidad(unid)
                nombre = resto.ifBlank { unid }
            } else {
                nombre = "$unid $resto".trim()
                cantidad = cant
            }
        } else {
            // Patrón: cantidad al inicio (sin unidad clara)
            val patronInicio = Regex("^(\\d+(?:\\.\\d+)?)\\s+(.+)")
            val matchInicio = patronInicio.find(texto)
            if (matchInicio != null) {
                cantidad = matchInicio.groupValues[1].toDoubleOrNull()
                nombre = matchInicio.groupValues[2]
            }
        }
        
        return Extraccion(cantidad, unidad, nombre.trim())
    }

    // Funciones esUnidadConocida y normalizarUnidad ahora están en ParserUtils

    private fun esLineaBasura(linea: String): Boolean {
        val lineaLimpia = linea.trim().lowercase()
        return lineaLimpia.contains("no son cantidades") ||
               lineaLimpia.matches(Regex("^tipo\\s+\\d+.*")) ||
               lineaLimpia.isBlank()
    }

    private fun esFragmentoBasura(fragmento: String): Boolean {
        val fragLimpio = fragmento.trim().lowercase()
        
        // Fragmentos muy cortos o vacíos
        if (fragLimpio.isBlank() || fragLimpio.length <= 2) return true
        
        // MEJORA: Filtrar fragmentos que empiecen con preposiciones
        if (fragLimpio.startsWith("de ") || 
            fragLimpio.startsWith("en ") || 
            fragLimpio.startsWith("y ") ||
            fragLimpio.startsWith("o ") ||
            fragLimpio.startsWith("con ")) return true
            
        // MEJORA: Filtrar fragmentos que sean solo preposiciones + sustantivo
        val patronPreposicion = Regex("^(de|en|y|o|con)\\s+\\w+$")
        if (patronPreposicion.matches(fragLimpio)) return true
        
        // MEJORA: Filtrar fragmentos que sean solo conjunciones
        val conjunciones = setOf("y", "o", "de", "en", "con", "para", "por")
        if (conjunciones.contains(fragLimpio)) return true
        
        return false
    }

    private fun esProductoValido(producto: Producto): Boolean {
        return producto.nombre.trim().isNotBlank() && producto.nombre.trim().length > 2
    }

    private fun normalizarNumeros(texto: String): String {
        val numerosTexto = mapOf(
            "medio" to "0.5", "media" to "0.5",
            "un" to "1", "una" to "1", "uno" to "1",
            "dos" to "2", "tres" to "3", "cuatro" to "4", "cinco" to "5",
            "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9", "diez" to "10"
        )
        
        var resultado = texto
        for ((palabra, numero) in numerosTexto) {
            resultado = resultado.replace(Regex("\\b$palabra\\b", RegexOption.IGNORE_CASE), numero)
        }
        return resultado
    }

    private data class Extraccion(
        val cantidad: Double?,
        val unidad: String?,
        val nombre: String
    )
}
