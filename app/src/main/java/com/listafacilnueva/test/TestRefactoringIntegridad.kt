package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser

class TestRefactoringIntegridad {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("🔍 TEST DE INTEGRIDAD DESPUÉS DEL REFACTORING")
            println("=" * 60)
            
            val casosCriticos = listOf(
                "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
                "1.1papa mediana2.sandia grande 3.zapato grande" to 3,
                "papa, tomate, cebolla" to 3,
                "2kg arroz" to 1,
                "1.5 litros leche 2.3 kg arroz" to 2
            )
            
            var casosExitosos = 0
            
            for ((texto, esperado) in casosCriticos) {
                val productos = QuantityParser.parse(texto)
                val exito = productos.size == esperado
                
                println("\n📝 '$texto'")
                println("   Esperado: $esperado | Obtenido: ${productos.size} ${if (exito) "✅" else "❌"}")
                
                if (exito) casosExitosos++
                else {
                    println("   PRODUCTOS:")
                    productos.forEach { println("     - ${it.cantidad} ${it.nombre}") }
                }
            }
            
            println("\n" + "=" * 60)
            println("RESULTADO: $casosExitosos/${casosCriticos.size} casos exitosos")
            
            if (casosExitosos == casosCriticos.size) {
                println("🎉 REFACTORING EXITOSO - Funcionalidad preservada")
            } else {
                println("⚠️ PROBLEMA DETECTADO - Revisar funcionalidad")
            }
        }
    }
}
