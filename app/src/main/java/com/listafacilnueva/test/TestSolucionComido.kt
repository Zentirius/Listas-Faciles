package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser

class TestSolucionComido {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("=== TEST SOLUCIÓN: Producto comido ===")
            
            val textoProblematico = "1.2metros cable 2.bombilla grande 3.zapallo chino"
            println("\n🔥 TEXTO: '$textoProblematico'")
            
            val productos = QuantityParser.parse(textoProblematico)
            
            println("\n📊 RESULTADOS:")
            println("Total productos detectados: ${productos.size}")
            productos.forEachIndexed { index, producto ->
                println("${index + 1}. Cantidad: '${producto.cantidad}' | Nombre: '${producto.nombre}' | Unidad: '${producto.unidad ?: "sin unidad"}'")
            }
            
            println("\n✅ ESPERADO: 3 productos")
            println("1. 2metros cable")
            println("2. 1 bombilla grande")
            println("3. 1 zapallo chino")
            
            if (productos.size == 3) {
                println("\n✅ ÉXITO: Se detectaron correctamente los 3 productos")
                
                // Verificar contenido específico
                val nombres = productos.map { it.nombre }
                val esperados = listOf("cable", "bombilla grande", "zapallo chino")
                
                var todosCorrectos = true
                for (esperado in esperados) {
                    if (!nombres.any { it.contains(esperado, ignoreCase = true) }) {
                        println("❌ FALTA: '$esperado' no encontrado en ${nombres}")
                        todosCorrectos = false
                    }
                }
                
                if (todosCorrectos) {
                    println("🎉 PERFECTO: Todos los productos esperados están presentes")
                } else {
                    println("⚠️ PARCIAL: Productos detectados pero con problemas de contenido")
                }
                
            } else {
                println("\n❌ ERROR: Se esperaban 3 productos pero se encontraron ${productos.size}")
                println("PRODUCTOS FALTANTES: ${3 - productos.size}")
            }
            
            // Test adicional: el otro caso problemático
            println("\n\n=== CASO ADICIONAL ===")
            val caso2 = "1.1papa mediana2.sandia grande 3.zapato grande"
            println("TEXTO: '$caso2'")
            val productos2 = QuantityParser.parse(caso2)
            println("Productos detectados: ${productos2.size}")
            productos2.forEach { producto ->
                println("  - ${producto.cantidad} ${producto.nombre}")
            }
            
            if (productos2.size == 3) {
                println("✅ Caso 2 también resuelto")
            } else {
                println("❌ Caso 2 aún tiene problemas: ${productos2.size}/3 productos")
            }
        }
    }
}
