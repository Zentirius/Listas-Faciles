package com.listafacilnueva.parser

import org.junit.Test
import org.junit.Assert.*

class TestListaExtremaAnalisis {

    @Test
    fun testAnalisisCompleto() {
        println("ANÁLISIS COMPLETO - LISTA EXTREMA")
        println("=".repeat(60))
        
        // Casos extremos específicos de la lista
        val casosExtremos = listOf(
            // Caso 1: Decimales complejos
            "Yogur griego 2.5." to "Debe detectar cantidad 2.5, no tratar punto como numeración",
            
            // Caso 2: Múltiples productos con decimales pegados
            "verduras orgánicas ,11.5manzanas,16.8limones,7.3 cebollas rojas 12.1 peras maduras" to "Debe separar 5 productos con cantidades decimales",
            
            // Caso 3: Numeración con decimales técnicos
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso" to "Debe detectar numeración (1,2,3) con productos que tienen decimales",
            
            // Caso 4: Productos técnicos pegados
            "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable" to "¿Es numeración (2,3,5) o cantidades reales?",
            
            // Caso 5: Cantidades decimales sin espacios
            "1.8bolsas quinoa orgánica" to "Cantidad decimal 1.8 pegada al producto",
            
            // Caso 6: Múltiples decimales en una línea
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión" to "5 productos con decimales complejos",
            
            // Caso 7: Especificaciones técnicas
            "5.1 metros de cable HDMI 4K,,,8.4 adaptadores USB-C multipuerto." to "Productos técnicos con decimales y separadores múltiples",
            
            // Caso 8: Cantidades con punto final
            "Crema hidratante 3.4." to "Cantidad con punto final - no confundir con numeración"
        )
        
        for ((index, caso) in casosExtremos.withIndex()) {
            val (input, descripcion) = caso
            println("\n🔍 CASO ${index + 1}: $descripcion")
            println("INPUT: '$input'")
            
            try {
                val productos = QuantityParser.parse(input)
                println("✅ RESULTADO: ${productos.size} productos detectados")
                
                productos.forEachIndexed { i, producto ->
                    val cantidadStr = producto.cantidad?.let { "${it}${producto.unidad ?: ""}" } ?: "sin cantidad"
                    println("   ${i + 1}. '${producto.nombre}' [$cantidadStr]")
                }
                
                // Análisis específico por caso
                when (index) {
                    0 -> { // Yogur 2.5
                        val yogur = productos.find { it.nombre.contains("yogur", ignoreCase = true) }
                        if (yogur?.cantidad == 2.5) {
                            println("   ✅ CORRECTO: Cantidad 2.5 preservada")
                        } else {
                            println("   ❌ ERROR: Cantidad 2.5 no detectada correctamente")
                        }
                    }
                    1 -> { // Verduras múltiples
                        if (productos.size >= 4) {
                            println("   ✅ CORRECTO: Múltiples productos separados")
                        } else {
                            println("   ❌ ERROR: No separó correctamente los productos")
                        }
                    }
                    2 -> { // Numeración con decimales
                        if (productos.size == 3) {
                            println("   ✅ CORRECTO: Detectada numeración de lista (3 productos)")
                        } else {
                            println("   ❌ ERROR: No detectó numeración correctamente")
                        }
                    }
                    // ... más análisis específicos
                }
                
            } catch (e: Exception) {
                println("❌ ERROR en parsing: ${e.message}")
                e.printStackTrace()
            }
            
            println("-".repeat(50))
        }
    }
    
    @Test
    fun `analizar patrones problemáticos específicos`() {
        println("\n🔬 ANÁLISIS DE PATRONES PROBLEMÁTICOS")
        println("=".repeat(60))
        
        // Patrones que pueden estar causando problemas
        val patronesProblematicos = mapOf(
            "Decimales con punto final" to listOf(
                "Yogur griego 2.5.",
                "Crema hidratante 3.4.",
                "Champú reparador 1.7."
            ),
            "Productos técnicos pegados" to listOf(
                "1.8bolsas quinoa orgánica",
                "2.4 aceite oliva extra virgen",
                "3.5bolsas garbanzos premium"
            ),
            "Numeración con decimales complejos" to listOf(
                "2.7metros cuerda náutica 3.9martillo",
                "1.6litros leche descremada2.5detergente",
                "4.6tijeras profesionales de costura"
            ),
            "Múltiples decimales separados por comas" to listOf(
                "12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes",
                "18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes"
            )
        )
        
        for ((patron, casos) in patronesProblematicos) {
            println("\n📋 PATRÓN: $patron")
            
            for ((index, caso) in casos.withIndex()) {
                println("\n  Caso ${index + 1}: '$caso'")
                
                try {
                    val productos = QuantityParser.parse(caso)
                    println("  → ${productos.size} productos: ${productos.map { "${it.nombre} (${it.cantidad ?: "N/A"})" }}")
                    
                } catch (e: Exception) {
                    println("  → ERROR: ${e.message}")
                }
            }
        }
    }
    
    @Test
    fun `proponer mejoras sin afectar funcionalidad existente`() {
        println("\n💡 ANÁLISIS PARA MEJORAS SEGURAS")
        println("=".repeat(60))
        
        // Casos que SÍ deben seguir funcionando (no tocar)
        val casosQueFuncionan = listOf(
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
            "2.5 metros de cinta adhesiva,,,3 tubos de pegamento" to 2,
            "6sandias,8tomates,6 zanaorias 5 zapatos" to 4
        )
        
        println("🔒 CASOS QUE DEBEN MANTENERSE FUNCIONANDO:")
        for ((caso, esperado) in casosQueFuncionan) {
            val productos = QuantityParser.parse(caso)
            val funcionaCorrectamente = productos.size == esperado
            println("  ${if (funcionaCorrectamente) "✅" else "❌"} '$caso' → ${productos.size} productos (esperado: $esperado)")
        }
        
        println("\n🔧 MEJORAS PROPUESTAS (sin afectar lo que funciona):")
        println("1. Mejorar detección de cantidades decimales con punto final")
        println("2. Optimizar separación de productos con decimales complejos")
        println("3. Ajustar regex para productos técnicos pegados")
        println("4. Mejorar manejo de múltiples decimales separados por comas")
    }
}
