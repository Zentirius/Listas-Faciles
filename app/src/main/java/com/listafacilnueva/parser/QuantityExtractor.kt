package com.listafacilnueva.parser

import android.util.Log
import com.listafacilnueva.model.Producto
import java.util.Locale

/**
 * EXTRACTOR DE CANTIDADES Y UNIDADES
 * 
 * Funciones para extraer cantidades, unidades y nombres
 * de productos de fragmentos de texto.
 */
object QuantityExtractor {

    private var DEBUG_MODE = false
    private const val TAG = "EXTRACTOR"
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            Log.d(TAG, message)
        }
    }

    // Normaliza el nombre del producto eliminando prefijos como "de " y puntuación final
    private fun normalizarNombre(nombre: String): String {
        var n = nombre.trim()
        n = n.replace(Regex("^de\\s+", RegexOption.IGNORE_CASE), "")
        n = n.replace(Regex("[\u0020\t\u00A0]+"), " ")
        n = n.replace(Regex("[\\.,;:]+$"), "").trim()
        // Capitalizar primera letra del nombre (preferencia del usuario)
        if (n.isNotEmpty()) {
            n = n.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
            }
        }
        return n
    }

    /**
     * PROCESAR FRAGMENTO - VERSIÓN MEJORADA
     */
    fun procesarFragmento(fragmento: String): Producto? {
        if (ValidationUtils.esFragmentoBasura(fragmento)) return null
        
        log("Procesando fragmento: '$fragmento'")
        
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
            log("Marcas extraídas: ${marcas.joinToString(", ")}")
        } else {
            // MEJORA: Extraer marcas con patrón genérico "marca X" deteniéndose en separadores ", . \n"
            // Ejemplos: "marca yemita", "marca frutos del maipo o minuto verde."
            val marcaRegex = Regex("marca\\s+([^,\\.\\n]+)", RegexOption.IGNORE_CASE)
            val marcaMatch = marcaRegex.find(texto)
            if (marcaMatch != null) {
                val marca = marcaMatch.groupValues[1].trim()
                marcas.add(marca)
                texto = texto.replace(marcaMatch.value, "").trim()
                log("Marca extraída: $marca")
            }
        }
        
        // Extraer nota entre paréntesis (que no sea marca)
        val notaMatch = Regex("\\(([^)]+)\\)").find(texto)
        if (notaMatch != null && !notaMatch.value.contains("marca", ignoreCase = true)) {
            nota = notaMatch.groupValues[1].trim()
            texto = texto.replace(notaMatch.value, "").trim()
            log("Nota extraída: $nota")
        }
        
        // Extraer cantidad y unidad
        val extraccion = extraerCantidadYUnidad(texto)

        // Evitar crear productos con nombre vacío/no-producto o solo puntuación
        val nombreOk = extraccion.nombre.trim()
        if (nombreOk.isEmpty() || nombreOk.matches(Regex("^[\\p{Punct}\\s]+$")) || ValidationUtils.esNombreNoProducto(nombreOk)) {
            log("Fragmento descartado por nombre inválido/no-producto: '${extraccion.nombre}'")
            return null
        }

        val producto = Producto(
            nombre = extraccion.nombre,
            cantidad = extraccion.cantidad,
            unidad = extraccion.unidad,
            marcas = marcas,
            nota = nota,
            original = fragmento
        )
        
        log("Producto creado: ${producto.nombre} - ${producto.cantidad}${producto.unidad ?: ""}")
        
        return producto
    }

    /**
     * EXTRAER CANTIDAD Y UNIDAD - VERSIÓN MEJORADA
     */
    private fun extraerCantidadYUnidad(texto: String): Extraccion {
        var nombre = texto
        var cantidad: Double? = null
        var unidad: String? = null
        
        log("Extrayendo cantidad y unidad de: '$texto'")
        
        // MEJORA CRÍTICA: Patrón para 'x2', 'x6', etc. (ej: "Leche sin lactosa (x2)")
        val patronX = Regex("^(.+?)\\s*\\(x(\\d+)(?:\\s+([\\p{L}]+))?\\)", RegexOption.IGNORE_CASE)
        val matchX = patronX.find(texto)
        if (matchX != null) {
            nombre = matchX.groupValues[1].trim()
            cantidad = matchX.groupValues[2].toDoubleOrNull()
            unidad = matchX.groupValues[3].takeIf { it.isNotBlank() }
            log("Patrón X detectado: $cantidad $unidad")
            return Extraccion(cantidad, unidad, normalizarNombre(nombre))
        }
        
        // MEJORA CRÍTICA: Patrón cantidad + unidad + "de" + nombre (ej: "2 metros de cable")
        val patronConDe = Regex("^(\\d+(?:\\.\\d+)?)\\s+([a-zA-Záéíóúüñ]+)\\s+de\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchConDe = patronConDe.find(texto)
        if (matchConDe != null && ParserUtils.esUnidadConocida(matchConDe.groupValues[2])) {
            cantidad = matchConDe.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchConDe.groupValues[2])
            nombre = matchConDe.groupValues[3].trim()
            log("Patrón 'de' detectado: $cantidad $unidad")
            return Extraccion(cantidad, unidad, normalizarNombre(nombre))
        }
        
        // MEJORA: Patrón cantidad + unidad + nombre (sin "de") (ej: "2 bolsas arroz")
        val patronCompleto = Regex("^(\\d+(?:\\.\\d+)?)\\s+([a-zA-Záéíóúüñ]+)\\s+(.*)")
        val matchCompleto = patronCompleto.find(texto)
        if (matchCompleto != null && ParserUtils.esUnidadConocida(matchCompleto.groupValues[2])) {
            cantidad = matchCompleto.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchCompleto.groupValues[2])
            nombre = matchCompleto.groupValues[3].trim()
            log("Patrón completo detectado: $cantidad $unidad")
            return Extraccion(cantidad, unidad, normalizarNombre(nombre))
        }
        
        // MEJORA CRÍTICA: Patrón para números escritos en palabras (ej: "cinco tomates", "ocho zanahorias")
        val patronPalabras = Regex("^(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce|media|medio)\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchPalabras = patronPalabras.find(texto)
        if (matchPalabras != null) {
            val cantidadPalabra = matchPalabras.groupValues[1].lowercase()
            nombre = matchPalabras.groupValues[2].trim()
            cantidad = TextPreprocessor.convertirPalabraANumero(cantidadPalabra)
            
            // CORRECCIÓN ESPECÍFICA: "media docena" = 6, no 12
            if (cantidadPalabra == "media" && nombre.startsWith("docena")) {
                cantidad = 6.0
                nombre = nombre.replace("docena de ", "").replace("docena", "").trim()
            }
            
            log("Patrón palabras detectado: $cantidad")
            return Extraccion(cantidad, unidad, normalizarNombre(nombre))
        }
        
        // MEJORA: Patrón cantidad entre paréntesis (ej: "Tomates (2 kg)")
        val patronParentesis = Regex("^(.+?)\\s*\\((\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ]+)?\\)", RegexOption.IGNORE_CASE)
        val matchParentesis = patronParentesis.find(texto)
        if (matchParentesis != null) {
            nombre = matchParentesis.groupValues[1].trim()
            cantidad = matchParentesis.groupValues[2].toDoubleOrNull()
            val unidadParentesis = matchParentesis.groupValues[3]
            if (unidadParentesis.isNotBlank() && ParserUtils.esUnidadConocida(unidadParentesis)) {
                unidad = ParserUtils.normalizarUnidad(unidadParentesis)
            }
            log("Patrón paréntesis detectado: $cantidad $unidad")
            return Extraccion(cantidad, unidad, normalizarNombre(nombre))
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
            log("Patrón pegado detectado: $cantidad $unidad")
        } else {
            // CORRECCIÓN CRÍTICA MEJORADA: Patrón cantidad al final (ej: "Leche sin lactosa 2", "Shampoo anticaspa 1.", "Esponjas 5 en oferta")
            val patronCantidadAlFinal = Regex("^(.+?)\\s+(\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ]*)\\s*\\.?.*$")
            val matchAlFinal = patronCantidadAlFinal.find(texto)
            if (matchAlFinal != null) {
                val nombreBase = matchAlFinal.groupValues[1].trim()
                val cant = matchAlFinal.groupValues[2].toDoubleOrNull()
                val posibleUnidad = matchAlFinal.groupValues[3].trim()
                
                // Verificar que el nombre base no sea muy corto y la cantidad sea válida
                if (nombreBase.length >= 3 && cant != null && cant > 0) {
                    // Verificar si es unidad conocida
                    if (posibleUnidad.isNotEmpty() && ParserUtils.esUnidadConocida(posibleUnidad)) {
                        cantidad = cant
                        unidad = ParserUtils.normalizarUnidad(posibleUnidad)
                        nombre = nombreBase
                    } else {
                        cantidad = cant
                        nombre = nombreBase
                        // Para casos como "Esponjas 5 en oferta", preservar toda la descripción adicional
                        if (posibleUnidad.isNotEmpty() && !ParserUtils.esUnidadConocida(posibleUnidad)) {
                            nombre = nombreBase  // Solo el nombre base, la descripción se maneja en otro lugar
                        }
                    }
                    log("Patrón cantidad al final detectado: $cantidad $unidad")
                    return Extraccion(cantidad, unidad, normalizarNombre(nombre))
                }
            }
            
            // Patrón: cantidad al inicio (sin unidad clara)
            val patronInicio = Regex("^(\\d+(?:\\.\\d+)?)\\s+(.+)")
            val matchInicio = patronInicio.find(texto)
            if (matchInicio != null) {
                cantidad = matchInicio.groupValues[1].toDoubleOrNull()
                nombre = matchInicio.groupValues[2]
                log("Patrón cantidad al inicio detectado: $cantidad")
            }
        }
        
        // Normalización final del nombre: quitar prefijos como "de " y puntuación final
        var nombreNormalizado = nombre.trim()
        nombreNormalizado = nombreNormalizado.replace(Regex("^de\\s+", RegexOption.IGNORE_CASE), "")
        nombreNormalizado = nombreNormalizado.replace(Regex("[\\.,;:]+$"), "").trim()

        // DEFENSIVO: si el nombre queda siendo solo una unidad conocida (contenedor),
        // intentar recuperar el nombre real desde el texto original.
        // Casos: "1 malla tomates" -> nombre no debe ser "malla" sino "tomates".
        if (ParserUtils.esUnidadConocida(nombreNormalizado)) {
            // Buscar patrón: cantidad + unidad + (opcional "de") + nombre
            val mDe = Regex("^\\d+(?:\\.\\d+)?\\s+[\\p{L}áéíóúüñ]+\\s+de\\s+(.+)$", RegexOption.IGNORE_CASE).find(texto)
            val mSimple = Regex("^\\d+(?:\\.\\d+)?\\s+[\\p{L}áéíóúüñ]+\\s+(.+)$", RegexOption.IGNORE_CASE).find(texto)
            val candidato = when {
                mDe != null -> mDe.groupValues[1].trim()
                mSimple != null -> mSimple.groupValues[1].trim()
                else -> null
            }
            if (!candidato.isNullOrBlank()) {
                val recuperado = normalizarNombre(candidato)
                if (recuperado.isNotBlank() && !ParserUtils.esUnidadConocida(recuperado)) {
                    log("Nombre recuperado desde contenedor: '$nombreNormalizado' -> '$recuperado'")
                    nombreNormalizado = recuperado
                }
            }
        }
        log("Extracción final: $cantidad $unidad - $nombreNormalizado")
        return Extraccion(cantidad, unidad, nombreNormalizado)
    }

    /**
     * CLASE DE DATOS PARA EXTRACCIÓN
     */
    data class Extraccion(
        val cantidad: Double?,
        val unidad: String?,
        val nombre: String
    )
}
