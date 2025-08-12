package com.listafacilnueva.parser

/**
 * ✂️ MÓDULO: Fragmentador de Texto
 * 
 * Responsable de dividir texto en fragmentos procesables,
 * separar múltiples productos y manejar diferentes separadores.
 */
object TextFragmenter {

    /**
     * Divide una línea en fragmentos procesables
     */
    fun dividirEnFragmentos(linea: String): List<String> {
        // MEJORA CRÍTICA: Primera verificación - dividir por separadores obvios
        val fragmentosPrelimin = dividirPorSeparadores(linea)
        
        // Si ya se dividió correctamente, devolver fragmentos
        if (fragmentosPrelimin.size > 1) {
            return fragmentosPrelimin.filter { it.isNotBlank() }
        }
        
        // Si no se pudo dividir, intentar separar múltiples productos
        val fragmentosFinales = separarMultiplesProductos(linea)
        return fragmentosFinales.filter { it.isNotBlank() }
    }

    /**
     * Divide texto por separadores comunes (comas, puntos y comas, etc.)
     */
    fun dividirPorSeparadores(linea: String): List<String> {
        // CASO ESPECIAL: Manejar marcas entre paréntesis ANTES de dividir
        val fragmentosConMarca = procesarLineaConMarca(linea)
        if (fragmentosConMarca.size > 1) {
            return fragmentosConMarca
        }
        
        // MEJORA: Detectar puntos que NO son decimales para separar productos
        var textoParaProcesar = linea
        
        // Separar por puntos solo si NO son decimales
        if (debeSeprararPorPunto(linea)) {
            textoParaProcesar = linea.replace(Regex("(?<!\\d)\\.(?!\\d)"), ",")
        }
        
        // Dividir por separadores múltiples: comas, punto y coma, múltiples espacios
        val separadores = Regex("[,;]{1,3}|\\s{3,}")
        val fragmentos = textoParaProcesar.split(separadores)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        // MEJORA ADICIONAL: Si un fragmento contiene múltiples productos evidentes, separarlo
        val fragmentosRefinados = mutableListOf<String>()
        for (fragmento in fragmentos) {
            val subFragmentos = separarMultiplesProductos(fragmento)
            fragmentosRefinados.addAll(subFragmentos)
        }
        
        return fragmentosRefinados.filter { it.isNotBlank() }
    }

    /**
     * Procesa líneas que contienen marcas entre paréntesis
     */
    fun procesarLineaConMarca(linea: String): List<String> {
        // CASO: "Papa (marca: Frutos del Maipo), Tomate (marca: Super Fresh)"
        
        // Detectar si hay marcas múltiples
        val patronMarcasMultiples = Regex("\\([^)]*marca[^)]*\\)", RegexOption.IGNORE_CASE)
        val marcas = patronMarcasMultiples.findAll(linea).toList()
        
        if (marcas.size >= 2) {
            // Separar por las marcas, manteniendo cada producto con su marca
            var textoRestante = linea
            val fragmentos = mutableListOf<String>()
            
            for ((index, marca) in marcas.withIndex()) {
                val inicio = textoRestante.indexOf(marca.value)
                if (inicio > 0) {
                    val antes = textoRestante.substring(0, inicio).trim()
                    val marcaCompleta = marca.value
                    
                    if (antes.isNotEmpty()) {
                        fragmentos.add("$antes $marcaCompleta")
                    }
                    
                    textoRestante = textoRestante.substring(inicio + marcaCompleta.length).trim()
                    if (textoRestante.startsWith(",")) {
                        textoRestante = textoRestante.substring(1).trim()
                    }
                }
            }
            
            // Agregar el último fragmento si queda algo
            if (textoRestante.isNotEmpty()) {
                fragmentos.add(textoRestante)
            }
            
            return fragmentos.filter { it.isNotBlank() }
        }
        
        return listOf(linea)
    }

    /**
     * Separa múltiples productos en un fragmento
     */
    fun separarMultiplesProductos(fragmento: String): List<String> {
        // PATRÓN 1: Productos separados por números (1kg arroz2litros leche)
        val patron1 = Regex("(\\d+(?:\\.\\d+)?\\s*(?:kg|gr?|litros?|l|m|cm)\\s+[a-zA-Záéíóúüñ\\s]+?)(\\d+)", RegexOption.IGNORE_CASE)
        val match1 = patron1.find(fragmento)
        
        if (match1 != null) {
            val parte1 = match1.groupValues[1].trim()
            val resto = fragmento.substring(match1.range.last).trim()
            
            if (parte1.isNotEmpty() && resto.isNotEmpty()) {
                return listOf(parte1, resto)
            }
        }
        
        // PATRÓN 2: Detectar múltiples cantidades seguidas
        val patronCantidades = Regex("(\\d+(?:\\.\\d+)?\\s*(?:kg|gr?|litros?|l|m|cm|unidades?|u)?\\s+[a-zA-Záéíóúüñ\\s]+?)(?=\\d+(?:\\.\\d+)?\\s*(?:kg|gr?|litros?|l|m|cm|unidades?|u)|$)", RegexOption.IGNORE_CASE)
        val coincidencias = patronCantidades.findAll(fragmento).toList()
        
        if (coincidencias.size >= 2) {
            return coincidencias.map { it.value.trim() }.filter { it.isNotEmpty() }
        }
        
        // PATRÓN 3: Productos simplemente pegados (lechugaromanatomates)
        val patronPegados = Regex("([a-zA-Záéíóúüñ]{4,})([a-zA-Záéíóúüñ]{4,})", RegexOption.IGNORE_CASE)
        val matchPegados = patronPegados.find(fragmento)
        
        if (matchPegados != null && !fragmento.contains(" ")) {
            // Solo si no hay espacios y las palabras son largas
            val parte1 = matchPegados.groupValues[1]
            val parte2 = matchPegados.groupValues[2]
            
            if (parte1.length >= 4 && parte2.length >= 4) {
                return listOf(parte1, parte2)
            }
        }
        
        return listOf(fragmento)
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

    /**
     * Verifica si dos partes tienen cantidades separadas
     */
    fun tieneCantidadesSeparadas(parte1: String, parte2: String): Boolean {
        val cantidad1 = Regex("\\d+").find(parte1)
        val cantidad2 = Regex("\\d+").find(parte2)
        
        return cantidad1 != null && cantidad2 != null
    }
}
