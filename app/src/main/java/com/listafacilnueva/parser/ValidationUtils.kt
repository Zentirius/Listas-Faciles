package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * ✅ MÓDULO: Utilidades de Validación
 * 
 * Responsable de todas las validaciones y filtros para
 * determinar qué texto y productos son válidos.
 */
object ValidationUtils {

    /**
     * Determina si una línea es basura y debe ser ignorada
     */
    fun esLineaBasura(linea: String): Boolean {
        val texto = linea.trim().lowercase()
        
        return texto.isEmpty() ||
               texto.length < 2 ||
               texto.all { it.isDigit() || it == '.' } ||
               texto in setOf(".", ",", ";", ":", "-", "_", "=") ||
               texto.startsWith("http") ||
               texto.matches(Regex("^[\\d\\s.,;:]+$"))
    }

    /**
     * Determina si un fragmento es basura y no debe procesarse
     */
    fun esFragmentoBasura(fragmento: String): Boolean {
        val texto = fragmento.trim().lowercase()
        
        if (texto.isEmpty() || texto.length < 2) return true
        
        // Filtrar fragmentos que son solo números, puntos o símbolos
        if (texto.matches(Regex("^[\\d\\s.,;:]+$"))) return true
        
        // Filtrar fragmentos que son solo palabras irrelevantes
        val palabrasBasura = setOf(
            "de", "del", "la", "las", "el", "los", "un", "una", "unos", "unas",
            "y", "o", "con", "sin", "para", "por", "a", "en", "que", "se", "es",
            "comprar", "lista", "supermercado", "super", "mandado", "mercado",
            "favor", "anotar", "apuntar", "recordar", "nota", "notas",
            "si", "hay", "cuando", "puedas", "despues", "después", "luego",
            "también", "tambien", "además", "ademas"
        )
        
        val palabrasEnFragmento = texto.split(Regex("\\s+"))
        val palabrasRelevantes = palabrasEnFragmento.filter { it !in palabrasBasura }
        
        // Si después de filtrar palabras basura queda muy poco, es basura
        if (palabrasRelevantes.isEmpty() || 
            (palabrasRelevantes.size == 1 && palabrasRelevantes[0].length < 3)) {
            return true
        }
        
        // Filtrar patrones específicos de basura
        val patronesBasura = listOf(
            Regex("^\\d+\\s*[.,]?\\s*$"),  // Solo números
            Regex("^[.,;:]+$"),             // Solo puntuación
            Regex("^\\s*$")                 // Solo espacios
        )
        
        return patronesBasura.any { it.matches(texto) }
    }

    /**
     * Valida si un producto es válido y debe ser incluido
     */
    fun esProductoValido(producto: Producto): Boolean {
        val nombre = producto.nombre.trim().lowercase()
        
        // Validar nombre mínimo
        if (nombre.length < 2) return false
        
        // Filtrar nombres que son solo números o símbolos
        if (nombre.matches(Regex("^[\\d\\s.,;:]+$"))) return false
        
        // Filtrar nombres que son marcas conocidas solas
        if (ParserUtils.marcasConocidas.contains(nombre)) return false
        
        // Filtrar nombres que son solo palabras irrelevantes
        val palabrasBasura = setOf(
            "de", "del", "la", "las", "el", "los", "un", "una", "unos", "unas",
            "y", "o", "con", "sin", "para", "por", "a", "en", "que", "se", "es"
        )
        
        val palabras = nombre.split(Regex("\\s+"))
        val palabrasRelevantes = palabras.filter { it !in palabrasBasura && it.length >= 2 }
        
        if (palabrasRelevantes.isEmpty()) return false
        
        // Validar cantidad
        if (producto.cantidad <= 0) return false
        
        // Validar que no sea un patrón de numeración residual
        val patronNumeracionResidual = Regex("^\\d+\\s*[a-zA-Záéíóúüñ]?$")
        if (patronNumeracionResidual.matches(nombre)) return false
        
        return true
    }

    /**
     * Verifica si dos partes tienen cantidades separadas
     */
    fun tieneCantidadesSeparadas(parte1: String, parte2: String): Boolean {
        val cantidad1 = Regex("\\d+").find(parte1)
        val cantidad2 = Regex("\\d+").find(parte2)
        
        return cantidad1 != null && cantidad2 != null
    }

    /**
     * Determina si se debe separar por puntos
     */
    fun debeSeprararPorPunto(fragmento: String): Boolean {
        // NO separar si contiene decimales obvios
        if (fragmento.contains(Regex("\\d+\\.\\d+"))) {
            return false
        }
        
        // Separar si hay múltiples puntos que podrían ser separadores
        val puntos = fragmento.count { it == '.' }
        val palabras = fragmento.split(Regex("\\s+")).size
        
        // Si hay más puntos que palabras / 2, probablemente son separadores
        return puntos >= 2 && puntos >= (palabras / 3)
    }
}
