package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test

class TestCasosEspecificosNumeracion {
    @Test
    fun testCasosProblematicosEspecificos() {
        println("🔧 TEST ESPECÍFICO: Casos de numeración problemática")
        println("=".repeat(60))
        
        val casosProblematicos = mapOf(
            "Caso 1: papa-sandia-zapato" to "1.1papa mediana2.sandia grande 3.zapato grande",
            "Caso 2: cable-bombilla-zapallo" to "1.2metros cable 2.bombilla grande 3.zapallo chino"
        )
        
        casosProblematicos.forEach { (nombre, texto) ->
            println("\n🧪 $nombre")
            println("   Texto: '$texto'")
            
            val productos = QuantityParser.parse(texto)
            println("   Productos detectados: ${productos.size}")
            
            if (productos.isNotEmpty()) {
                productos.forEachIndexed { i, p ->
                    val cantidad = p.cantidad?.let { 
                        if (it % 1.0 == 0.0) it.toInt().toString() 
                        else it.toString() 
                    } ?: "1"
                    val unidad = p.unidad?.let { " $it" } ?: ""
                    println("   ${i+1}. ${p.nombre} (${cantidad}${unidad})")
                }
                
                // Verificar productos específicos esperados
                when (nombre) {
                    "Caso 1: papa-sandia-zapato" -> {
                        val tienePapa = productos.any { it.nombre.contains("papa", ignoreCase = true) }
                        val tieneSandia = productos.any { it.nombre.contains("sandia", ignoreCase = true) }
                        val tieneZapato = productos.any { it.nombre.contains("zapato", ignoreCase = true) }
                        
                        println("   ✓ Papa detectada: $tienePapa")
                        println("   ✓ Sandia detectada: $tieneSandia")
                        println("   ✓ Zapato detectado: $tieneZapato")
                        
                        val faltantes = mutableListOf<String>()
                        if (!tienePapa) faltantes.add("papa")
                        if (!tieneSandia) faltantes.add("sandia")
                        if (!tieneZapato) faltantes.add("zapato")
                        
                        if (faltantes.isNotEmpty()) {
                            println("   ❌ FALTANTES: ${faltantes.joinToString(", ")}")
                        } else {
                            println("   ✅ TODOS DETECTADOS CORRECTAMENTE")
                        }
                    }
                    
                    "Caso 2: cable-bombilla-zapallo" -> {
                        val tieneCable = productos.any { it.nombre.contains("cable", ignoreCase = true) }
                        val tieneBombilla = productos.any { it.nombre.contains("bombilla", ignoreCase = true) }
                        val tieneZapallo = productos.any { it.nombre.contains("zapallo", ignoreCase = true) }
                        
                        println("   ✓ Cable detectado: $tieneCable")
                        println("   ✓ Bombilla detectada: $tieneBombilla")
                        println("   ✓ Zapallo detectado: $tieneZapallo")
                        
                        val faltantes = mutableListOf<String>()
                        if (!tieneCable) faltantes.add("cable")
                        if (!tieneBombilla) faltantes.add("bombilla")
                        if (!tieneZapallo) faltantes.add("zapallo")
                        
                        if (faltantes.isNotEmpty()) {
                            println("   ❌ FALTANTES: ${faltantes.joinToString(", ")}")
                        } else {
                            println("   ✅ TODOS DETECTADOS CORRECTAMENTE")
                        }
                    }
                }
            } else {
                println("   ❌ NO SE DETECTÓ NINGÚN PRODUCTO")
                println("   🔍 POSIBLE PROBLEMA: La lógica de numeración no está funcionando")
            }
        }
        
        println("\n" + "=".repeat(60))
        println("🔍 ANÁLISIS DEL PROBLEMA:")
        println("Si estos casos no detectan los 3 productos esperados,")
        println("entonces los 2 productos faltantes probablemente son:")
        println("- bombilla (del caso 2)")
        println("- zapallo (del caso 2)")
        println("o")
        println("- sandia (del caso 1)")
        println("- zapato (del caso 1)")
        
        // Test adicional con versiones simples
        println("\n🧪 TEST ADICIONAL: Versiones simplificadas")
        
        val versionesSimples = listOf(
            "1.1 papa mediana",
            "2.sandia grande", 
            "3.zapato grande",
            "1.2 metros cable",
            "2.bombilla grande",
            "3.zapallo chino"
        )
        
        versionesSimples.forEach { version ->
            val resultado = QuantityParser.parse(version)
            val detectado = if (resultado.isNotEmpty()) "✅" else "❌"
            println("   $detectado '$version' → ${resultado.size} productos")
        }
        
        // Guardar resultado
        val output = StringBuilder()
        output.appendLine("TEST CASOS ESPECÍFICOS DE NUMERACIÓN")
        output.appendLine("="*40)
        
        casosProblematicos.forEach { (nombre, texto) ->
            val productos = QuantityParser.parse(texto)
            output.appendLine("$nombre:")
            output.appendLine("  Texto: '$texto'")
            output.appendLine("  Productos detectados: ${productos.size}")
            productos.forEach { p -> 
                output.appendLine("  - ${p.nombre} (${p.cantidad ?: 1})") 
            }
            output.appendLine()
        }
        
        val file = java.io.File("test_casos_numeracion_especificos.txt")
        file.writeText(output.toString())
        println("\n💾 Resultado guardado en: test_casos_numeracion_especificos.txt")
    }
}
