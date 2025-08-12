package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser

class TestVerificarSolucion {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("=== VERIFICACIÓN FINAL ===")
            
            val casosProblematicos = listOf(
                "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
                "1.1papa mediana2.sandia grande 3.zapato grande" to 3
            )
            
            var casosPasados = 0
            var totalCasos = casosProblematicos.size
            
            for ((texto, esperado) in casosProblematicos) {
                println("\n📝 CASO: '$texto'")
                println("   ESPERADO: $esperado productos")
                
                val productos = QuantityParser.parse(texto)
                println("   OBTENIDO: ${productos.size} productos")
                
                productos.forEachIndexed { index, producto ->
                    println("     ${index + 1}. [${producto.cantidad}] ${producto.nombre} ${if (producto.unidad != null) "(${producto.unidad})" else ""}")
                }
                
                if (productos.size == esperado) {
                    println("   ✅ ÉXITO")
                    casosPasados++
                } else {
                    println("   ❌ FALLO - Diferencia: ${productos.size - esperado}")
                }
            }
            
            println("\n" + "=".repeat(50))
            println("RESUMEN: $casosPasados/$totalCasos casos pasados")
            
            if (casosPasados == totalCasos) {
                println("🎉 ¡PROBLEMA SOLUCIONADO! Todos los casos pasan.")
            } else {
                println("⚠️ Aún hay ${totalCasos - casosPasados} caso(s) con problemas.")
            }
            
            // Verificación adicional: casos que deben seguir funcionando
            println("\n=== CASOS DE REGRESIÓN ===")
            val casosRegresion = listOf(
                "1.5 litros leche 2.3 kg arroz" to 2, // Cantidades decimales reales
                "papa, tomate, cebolla" to 3,  // Lista simple
                "2kg arroz" to 1 // Cantidad simple
            )
            
            var regresionPasada = 0
            for ((texto, esperado) in casosRegresion) {
                val productos = QuantityParser.parse(texto)
                if (productos.size == esperado) {
                    println("✅ REGRESIÓN OK: '$texto' → ${productos.size} productos")
                    regresionPasada++
                } else {
                    println("❌ REGRESIÓN FALLO: '$texto' → ${productos.size} productos (esperado: $esperado)")
                }
            }
            
            println("\nRESUMEN REGRESIÓN: $regresionPasada/${casosRegresion.size} casos OK")
        }
    }
}
