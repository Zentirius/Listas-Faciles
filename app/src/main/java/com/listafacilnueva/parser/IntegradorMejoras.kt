package com.listafacilnueva.parser

/**
 * 🎯 INTEGRADOR DE MEJORAS PARA QUANTITYPARSER
 * 
 * Esta clase integra todas las mejoras de forma segura
 * en el flujo principal del QuantityParser.
 */
object IntegradorMejoras {
    
    /**
     * 🔧 FUNCIÓN PRINCIPAL: Procesar input con todas las mejoras
     * 
     * Esta función aplica todas las mejoras implementadas
     * de forma segura sin romper la funcionalidad existente.
     */
    fun procesarConMejoras(input: String): String {
        println("🔧 APLICANDO MEJORAS INTEGRADAS A: '$input'")
        
        var inputMejorado = input
        
        try {
            // FASE 1: Mejoras básicas (siempre seguras)
            inputMejorado = QuantityParserMejoras.aplicarMejorasSeguras(inputMejorado)
            println("  ✅ Mejoras básicas aplicadas")
            
            // FASE 2: Mejoras avanzadas (solo si detectamos patrones complejos)
            if (necesitaMejorasAvanzadas(inputMejorado)) {
                val inputAvanzado = QuantityParserMejorasAvanzadas.aplicarTodasLasMejorasAvanzadas(inputMejorado)
                
                // Validar que las mejoras avanzadas no rompan el input
                if (esValidoResultadoAvanzado(input, inputAvanzado)) {
                    inputMejorado = inputAvanzado
                    println("  ✅ Mejoras avanzadas aplicadas")
                } else {
                    println("  ⚠️  Mejoras avanzadas omitidas (no seguras para este caso)")
                }
            }
            
        } catch (e: Exception) {
            println("  ❌ Error aplicando mejoras: ${e.message}")
            println("  🔄 Usando input original")
            return input
        }
        
        println("  📝 Resultado final: '$inputMejorado'")
        return inputMejorado
    }
    
    /**
     * 🔍 Detecta si el input necesita mejoras avanzadas
     */
    private fun necesitaMejorasAvanzadas(input: String): Boolean {
        return listOf(
            // Múltiples decimales pegados
            Regex("\\d+\\.\\d+[a-zA-Z]+.*\\d+\\.\\d+").containsMatchIn(input),
            
            // Preguntas intercaladas
            input.contains("preguntar..."),
            
            // Comas con producto final sin coma
            input.contains(",") && Regex(",.*\\d+\\.\\d+\\s+[a-zA-Z]+\\s*$").containsMatchIn(input),
            
            // Más de 3 decimales diferentes
            Regex("\\d+\\.\\d+").findAll(input).count() > 3
            
        ).any { it }
    }
    
    /**
     * ✅ Valida que las mejoras avanzadas sean seguras
     */
    private fun esValidoResultadoAvanzado(original: String, mejorado: String): Boolean {
        // Verificaciones básicas de seguridad
        return listOf(
            mejorado.isNotBlank(),
            mejorado.length >= original.length * 0.5, // No se redujo demasiado
            mejorado.length <= original.length * 3.0, // No se expandió demasiado
            !mejorado.contains("null"),
            !mejorado.contains("error", ignoreCase = true)
        ).all { it }
    }
    
    /**
     * 🚀 VERSIÓN MEJORADA DE PARSE PARA QUANTITYPARSER
     * 
     * Esta función puede reemplazar o complementar el parse actual
     */
    fun parseConMejoras(texto: String): List<com.listafacilnueva.model.Producto> {
        println("🚀 PARSE CON MEJORAS INTEGRADAS INICIADO")
        println("📝 Input original: '$texto'")
        
        // Aplicar mejoras al input
        val textoMejorado = procesarConMejoras(texto)
        
        // Si hay separador especial, procesar por fragmentos
        if (textoMejorado.contains(" | ")) {
            println("📊 Procesando por fragmentos separados")
            val fragmentos = textoMejorado.split(" | ").filter { it.trim().isNotBlank() }
            val todosProductos = mutableListOf<com.listafacilnueva.model.Producto>()
            
            for ((i, fragmento) in fragmentos.withIndex()) {
                println("  Fragmento ${i+1}: '$fragmento'")
                val productosFragmento = QuantityParser.parse(fragmento.trim())
                todosProductos.addAll(productosFragmento)
                println("    → ${productosFragmento.size} productos")
            }
            
            println("🏁 PARSE CON MEJORAS COMPLETADO: ${todosProductos.size} productos totales")
            return todosProductos
            
        } else {
            // Procesar normalmente
            println("📊 Procesando con parser principal")
            val productos = QuantityParser.parse(textoMejorado)
            println("🏁 PARSE CON MEJORAS COMPLETADO: ${productos.size} productos")
            return productos
        }
    }
    
    /**
     * 📊 COMPARAR RESULTADOS: Con y sin mejoras
     */
    fun compararResultados(input: String): Map<String, Any> {
        println("📊 COMPARANDO RESULTADOS PARA: '$input'")
        
        // Sin mejoras
        val sinMejoras = QuantityParser.parse(input)
        
        // Con mejoras
        val conMejoras = parseConMejoras(input)
        
        val comparacion = mapOf(
            "input" to input,
            "sin_mejoras" to sinMejoras.size,
            "con_mejoras" to conMejoras.size,
            "mejora" to (conMejoras.size > sinMejoras.size),
            "productos_sin_mejoras" to sinMejoras.map { "${it.nombre} [${it.cantidad}]" },
            "productos_con_mejoras" to conMejoras.map { "${it.nombre} [${it.cantidad}]" }
        )
        
        println("  📈 Sin mejoras: ${sinMejoras.size} productos")
        println("  📈 Con mejoras: ${conMejoras.size} productos")
        println("  🎯 ¿Mejoró?: ${if (conMejoras.size > sinMejoras.size) "SÍ" else "NO"}")
        
        return comparacion
    }
}
