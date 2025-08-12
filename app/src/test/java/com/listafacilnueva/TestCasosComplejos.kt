package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test
import org.junit.Assert.*

class TestCasosComplejos {

    @Test
    fun testCasosExtremosActual() {
        println("🎯 TEST DE CASOS EXTREMOS CON EL PARSER ACTUAL")
        println("=" * 60)
        
        val casosComplejos = listOf(
            "pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)" to "Producto con marcas y descripción",
            "2 lechugas francesas preguntar...cual son..." to "Producto con consulta integrada",
            "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos" to "Múltiples productos pegados",
            "1.1papa mediana2.sandia grande" to "Decimal con numeración compleja",
            "cinco tomates" to "Palabra numérica",
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to "Numeración compleja con unidades",
            "champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines" to "Múltiples cantidades consecutivas",
            "1.5 litros de jugo de naranja,,,2 latas de atún." to "Comas múltiples",
            "media docena de plátanos. 3 manzanas" to "Media docena + cantidad simple",
            "Zanahorias" to "Producto sin cantidad explícita"
        )
        
        var exitosos = 0
        var fallidos = 0
        
        casosComplejos.forEach { (caso, descripcion) ->
            println("\n🧪 TEST: $descripcion")
            println("   📝 Entrada: '$caso'")
            
            try {
                val productos = QuantityParser.parse(caso)
                println("   📊 Detectados: ${productos.size} productos")
                
                productos.forEachIndexed { i, p ->
                    val cantidad = p.cantidad?.let { 
                        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
                    } ?: "1"
                    val unidad = p.unidad?.let { " $it" } ?: ""
                    println("      ${i+1}. ${p.nombre} (${cantidad}${unidad})")
                }
                
                // Validaciones específicas
                val resultado = when {
                    caso.contains("pastelera") && productos.any { it.nombre.contains("pastelera", ignoreCase = true) } -> {
                        exitosos++; "✅ Producto con marca detectado"
                    }
                    caso.contains("2 lechugas francesas") && productos.any { it.cantidad == 2.0 && it.nombre.contains("lechugas", ignoreCase = true) } -> {
                        exitosos++; "✅ Cantidad con consulta manejada"
                    }
                    caso.contains("6sandias,8tomates") && productos.size >= 4 -> {
                        exitosos++; "✅ Productos múltiples separados"
                    }
                    caso.contains("1.1papa mediana2.sandia") && productos.size >= 2 -> {
                        exitosos++; "✅ Numeración compleja procesada"
                    }
                    caso.contains("cinco tomates") && productos.any { it.cantidad == 5.0 && it.nombre.contains("tomates", ignoreCase = true) } -> {
                        exitosos++; "✅ Palabra numérica convertida"
                    }
                    caso.contains("1.2metros cable") && productos.size >= 2 -> {
                        exitosos++; "✅ Numeración con unidades manejada"
                    }
                    caso.contains("4cepillos,12servilletas") && productos.size >= 4 -> {
                        exitosos++; "✅ Cantidades consecutivas separadas"
                    }
                    caso.contains("1.5 litros") && productos.size >= 2 -> {
                        exitosos++; "✅ Comas múltiples procesadas"
                    }
                    caso.contains("media docena") && productos.any { it.cantidad == 6.0 } -> {
                        exitosos++; "✅ Media docena convertida"
                    }
                    caso.equals("Zanahorias") && productos.size == 1 -> {
                        exitosos++; "✅ Producto sin cantidad detectado"
                    }
                    productos.size >= 1 -> {
                        "⚠️  Al menos detectó productos"
                    }
                    else -> {
                        fallidos++; "❌ No detectó productos"
                    }
                }
                
                println("   $resultado")
                
            } catch (e: Exception) {
                fallidos++
                println("   💥 ERROR: ${e.message}")
                e.printStackTrace()
            }
            
            println("   " + "-".repeat(45))
        }
        
        println("\n" + "=".repeat(60))
        println("📊 RESUMEN DEL TEST DE CASOS COMPLEJOS:")
        println("   ✅ Exitosos: $exitosos")
        println("   ❌ Fallidos: $fallidos")
        println("   📈 Ratio de éxito: ${(exitosos * 100) / casosComplejos.size}%")
        
        when {
            exitosos >= casosComplejos.size * 0.9 -> println("🎉 ¡EXCELENTE! El parser maneja muy bien los casos complejos")
            exitosos >= casosComplejos.size * 0.7 -> println("😊 ¡BIEN! El parser es efectivo")
            exitosos >= casosComplejos.size * 0.5 -> println("😐 REGULAR: El parser necesita mejoras")
            else -> println("😞 PROBLEMAS: El parser necesita revisión seria")
        }
        
        // Assertions para JUnit
        assertTrue("Debe tener éxito en al menos 50% de casos complejos", exitosos >= casosComplejos.size * 0.5)
    }
}

// Función auxiliar
private operator fun String.times(n: Int): String = this.repeat(n)
