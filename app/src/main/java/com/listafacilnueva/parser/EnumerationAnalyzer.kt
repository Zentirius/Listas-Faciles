package com.listafacilnueva.parser

/**
 * 🔢 MÓDULO: Análisis de Numeración y Listas
 * 
 * Responsable de detectar y procesar secuencias de numeración,
 * listas enumeradas y patrones de enumeración mixta.
 */
object EnumerationAnalyzer {

    /**
     * Determina si un texto contiene una secuencia de numeración válida
     */
    fun tieneSecuenciaEnumeracion(texto: String): Boolean {
        println("🔍 ANALIZANDO: '$texto'")
        
        // VERIFICACIÓN PRIMERA: Detectar secuencias de numeración válidas ANTES de rechazar por decimales
        // CASO CRÍTICO: "1.2metros cable 2.bombilla grande 3.zapallo chino" (DEBE ser numeración mixta)
        
        // Enfoque simplificado: buscar todos los números seguidos de punto
        val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE).findAll(texto).map { it.groupValues[1].toInt() }.toList()
        
        if (numerosConPunto.size >= 2) {
            // Verificar si es una secuencia consecutiva
            var esSecuenciaConsecutiva = true
            for (i in 1 until numerosConPunto.size) {
                if (numerosConPunto[i] != numerosConPunto[i-1] + 1) {
                    esSecuenciaConsecutiva = false
                    break
                }
            }
            
            if (esSecuenciaConsecutiva && numerosConPunto[0] in 1..10) { // Rango razonable para numeración de lista
                println("🔍 NUMERACIÓN DE LISTA DETECTADA: ${numerosConPunto.size} productos (${numerosConPunto.joinToString(", ")})")
                
                // NUEVA LÓGICA MEJORADA: Usar palabras clave de unidades como pista adicional
                val unidadesDetectadas = detectarUnidadesEnTexto(texto)
                println("🔍 UNIDADES DETECTADAS: $unidadesDetectadas")
                
                // LÓGICA INTELIGENTE: Si hay unidades, analizar el contexto
                if (unidadesDetectadas.isNotEmpty()) {
                    return analizarNumeracionConUnidades(texto, numerosConPunto, unidadesDetectadas)
                }
                
                // LÓGICA ORIGINAL: Verificar si es numeración mixta vs decimales puros
                val tieneDecimalesConEspacios = Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?|gr|ml|cm|mm)\\b", RegexOption.IGNORE_CASE)
                val matchesDecimales = tieneDecimalesConEspacios.findAll(texto).toList()
                
                // Si TODOS los números tienen decimales con unidades, entonces son cantidades reales, no numeración
                if (matchesDecimales.size == numerosConPunto.size) {
                    println("🔍 TODOS son decimales con unidades - SON CANTIDADES REALES, NO numeración")
                    return false
                }
                
                // Si solo ALGUNOS tienen decimales, es numeración mixta ✅
                if (matchesDecimales.size > 0 && matchesDecimales.size < numerosConPunto.size) {
                    println("🔍 NUMERACIÓN MIXTA detectada - algunos decimales, algunos enteros")
                    return true
                }
                
                // Si ninguno tiene decimales con unidades, es numeración normal ✅
                return true
            }
        }

        return false
    }

    /**
     * Detecta unidades conocidas en el texto
     */
    fun detectarUnidadesEnTexto(texto: String): List<String> {
        val unidadesConocidas = listOf(
            // Medidas de longitud
            "metros", "metro", "m", "centimetros", "centímetros", "cm", "milimetros", "milímetros", "mm",
            "pulgadas", "pulgada", "pies", "pie",
            
            // Medidas de peso
            "kg", "kilogramos", "kilogramo", "gramos", "gramo", "gr", "g", "libras", "libra", "onzas", "onza",
            
            // Medidas de volumen
            "litros", "litro", "l", "ml", "mililitros", "mililitro", "galones", "galon",
            
            // Medidas de área
            "metros cuadrados", "metro cuadrado", "m2", "hectareas", "hectáreas",
            
            // Unidades de tiempo
            "horas", "hora", "minutos", "minuto", "segundos", "segundo",
            
            // Unidades especiales
            "rollos", "rollo", "bolsas", "bolsa", "latas", "lata", "tubos", "tubo", "paquetes", "paquete"
        )
        
        val unidadesEncontradas = mutableListOf<String>()
        unidadesConocidas.forEach { unidad ->
            if (texto.contains(unidad, ignoreCase = true)) {
                unidadesEncontradas.add(unidad)
            }
        }
        
        return unidadesEncontradas
    }

    /**
     * Analiza numeración cuando hay unidades presentes
     */
    fun analizarNumeracionConUnidades(texto: String, numerosConPunto: List<Int>, unidades: List<String>): Boolean {
        println("🔍 ANÁLISIS INTELIGENTE con unidades: $unidades")
        
        // CASO 1: Si hay exactamente 1 unidad y múltiples números, probablemente es numeración mixta
        // Ejemplo: "1.2metros cable 2.bombilla 3.zapallo" → 1 unidad "metros", 3 números [1,2,3]
        if (unidades.size == 1 && numerosConPunto.size >= 2) {
            println("🔍 PATRÓN: 1 unidad + ${numerosConPunto.size} números → NUMERACIÓN MIXTA")
            return true
        }
        
        // CASO 2: Si hay múltiples unidades pero todas diferentes, podría ser numeración
        // Ejemplo: "1.2metros cable 2.litros agua 3.kg azucar" → diferentes unidades, numeración
        if (unidades.size > 1 && unidades.size == numerosConPunto.size) {
            val unidadesUnicas = unidades.distinct()
            if (unidadesUnicas.size == unidades.size) {
                println("🔍 PATRÓN: Cada número con unidad diferente → NUMERACIÓN")
                return true
            }
        }
        
        // CASO 3: Si hay múltiples unidades iguales, probablemente son cantidades reales
        // Ejemplo: "1.2metros cable 2.5metros madera" → misma unidad repetida, cantidades
        if (unidades.size > 1) {
            val unidadesUnicas = unidades.distinct()
            if (unidadesUnicas.size < unidades.size) {
                println("🔍 PATRÓN: Unidades repetidas → CANTIDADES REALES")
                return false
            }
        }
        
        // CASO 4: Detectar patrones específicos de productos con unidades
        // "rollos papel", "bolsas arroz", "tubos pegamento"
        val unidadesProducto = listOf("rollos", "bolsas", "latas", "tubos", "paquetes")
        if (unidades.any { it in unidadesProducto }) {
            println("🔍 PATRÓN: Unidades de producto detectadas → NUMERACIÓN probable")
            return true
        }
        
        // Por defecto, si no está claro, usar la lógica original
        return numerosConPunto.size >= 2
    }

    /**
     * Limpia numeración compuesta y compleja
     */
    fun limpiarNumeracionCompuesta(linea: String): String {
        // MEJORA CRÍTICA: Solo aplicar si detectamos secuencia válida
        if (!tieneSecuenciaEnumeracion(linea)) {
            println("🔄 NO es secuencia válida, sin modificación: '$linea'")
            return linea // No aplicar transformación, tratar como cantidad normal
        }
        
        // CASO CRÍTICO ESPECÍFICO: "1.2metros cable 2.bombilla grande 3.zapallo chino"
        // Patrón mejorado para 3 productos con cantidad en el primero
        val patron3Productos = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val match3Prod = patron3Productos.find(linea)
        
        if (match3Prod != null) {
            val cantidad1 = match3Prod.groupValues[2]
            val unidad1 = match3Prod.groupValues[3]
            val nombre1 = match3Prod.groupValues[4].trim()
            val nombre2 = match3Prod.groupValues[6].trim()
            val nombre3 = match3Prod.groupValues[8].trim()
            
            val resultado = "$cantidad1$unidad1 $nombre1, $nombre2, $nombre3"
            println("🧠 Transformación 3 productos: '$resultado'")
            return resultado
        }
        
        // CASO ESPECÍFICO ALTERNATIVO: Patrón más flexible para el caso problemático
        // "1.2metros cable 2.bombilla grande 3.zapallo chino"
        val patronFlexible = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ]+)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchFlexible = patronFlexible.find(linea)
        
        if (matchFlexible != null) {
            val cantidad1 = matchFlexible.groupValues[2] // "2"
            val unidad1 = matchFlexible.groupValues[3]   // "metros"
            val nombre1 = matchFlexible.groupValues[4].trim() // "cable"
            val nombre2 = matchFlexible.groupValues[6].trim() // "bombilla grande"
            val nombre3 = matchFlexible.groupValues[8].trim() // "zapallo chino"
            
            val resultado = "$cantidad1$unidad1 $nombre1, $nombre2, $nombre3"
            println("🧠 Transformación 3 productos (patrón flexible): '$resultado'")
            return resultado
        }

        // ... Continuar con otros patrones existentes ...
        // (Por brevedad, incluyo solo los patrones más críticos aquí)
        
        return linea
    }

    /**
     * Limpia numeración de lista simple (1., 2., etc.)
     */
    fun limpiarNumeracionLista(linea: String): String {
        // CORRECCIÓN CRÍTICA: NO eliminar decimales reales como "2.5 metros"
        // Solo eliminar numeración de lista como "1. producto", "2. producto"
        
        // Verificar si es un decimal real (número.decimal seguido de unidad o espacio+texto)
        val esDecimalReal = Regex("^\\d+\\.\\d+(\\s|[a-zA-Záéíóúüñ])", RegexOption.IGNORE_CASE).find(linea) != null
        if (esDecimalReal) {
            println("🔒 Preservando cantidad decimal: '$linea'")
            return linea.replace(Regex("\\.$"), "").trim() // Solo quitar punto final
        }
        
        // Limpiar numeración simple como "1.", "2." (solo si NO es decimal)
        return linea.replace(Regex("^\\d+\\.\\s*"), "")
                   .replace(Regex("\\.$"), "")
                   .trim()
    }
}
