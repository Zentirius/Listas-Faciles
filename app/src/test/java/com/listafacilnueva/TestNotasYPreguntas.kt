package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test

class TestNotasYPreguntas {
    @Test
    fun testConservacionDeNotas() {
        println("🔍 TEST: CONSERVACIÓN DE NOTAS Y PREGUNTAS")
        println("=".repeat(60))
        
        val casosConNotas = listOf(
            "2 lechugas francesas preguntar...cual son...",
            "3 rollos papel higiénico preguntar...cuál es mejor...",
            "1 aceite marca chef o el más económico",
            "pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)",
            "crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)"
        )
        
        casosConNotas.forEach { caso ->
            println("\n📝 CASO: '$caso'")
            val productos = QuantityParser.parse(caso)
            
            if (productos.isNotEmpty()) {
                productos.forEach { p ->
                    println("   ✅ Producto: '${p.nombre}'")
                    println("   📊 Cantidad: ${p.cantidad ?: "1"}")
                    if (p.unidad != null) println("   📏 Unidad: ${p.unidad}")
                    if (p.marcas.isNotEmpty()) println("   🏷️ Marcas: ${p.marcas.joinToString(", ")}")
                    if (p.nota != null) println("   📝 Nota: '${p.nota}'")
                    if (p.original != null) println("   🔤 Original: '${p.original}'")
                }
            } else {
                println("   ❌ NO SE DETECTÓ NINGÚN PRODUCTO")
            }
        }
        
        println("\n" + "=".repeat(60))
        println("🎯 VERIFICACIÓN ESPECÍFICA DE LOS 2 PRODUCTOS FALTANTES:")
        
        // Test específico para lechugas francesas
        val lechugas = QuantityParser.parse("2 lechugas francesas preguntar...cual son...")
        println("\n🥬 LECHUGAS FRANCESAS:")
        if (lechugas.isNotEmpty()) {
            lechugas.forEach { p ->
                println("   ✅ DETECTADO: '${p.nombre}' (${p.cantidad ?: 1})")
                if (p.nota != null) println("   📝 Nota preservada: '${p.nota}'")
            }
        } else {
            println("   ❌ NO DETECTADO - ESTE PODRÍA SER 1 DE LOS 2 FALTANTES")
        }
        
        // Test específico para papel higiénico
        val papel = QuantityParser.parse("3 rollos papel higiénico preguntar...cuál es mejor...")
        println("\n🧻 PAPEL HIGIÉNICO:")
        if (papel.isNotEmpty()) {
            papel.forEach { p ->
                println("   ✅ DETECTADO: '${p.nombre}' (${p.cantidad ?: 1})")
                if (p.nota != null) println("   📝 Nota preservada: '${p.nota}'")
            }
        } else {
            println("   ❌ NO DETECTADO - ESTE PODRÍA SER 1 DE LOS 2 FALTANTES")
        }
        
        // Guardar resultado
        val output = StringBuilder()
        output.appendLine("TEST DE CONSERVACIÓN DE NOTAS Y PREGUNTAS")
        output.appendLine("="*50)
        
        var notasConservadas = 0
        var totalCasosConNotas = 0
        
        casosConNotas.forEach { caso ->
            val productos = QuantityParser.parse(caso)
            totalCasosConNotas++
            
            if (productos.any { it.nota != null }) {
                notasConservadas++
                output.appendLine("✅ '$caso' - Nota conservada")
            } else {
                output.appendLine("❌ '$caso' - Nota perdida")
            }
        }
        
        output.appendLine("")
        output.appendLine("RESUMEN:")
        output.appendLine("Total casos con notas: $totalCasosConNotas")
        output.appendLine("Notas conservadas: $notasConservadas")
        output.appendLine("Porcentaje conservación: ${(notasConservadas * 100 / totalCasosConNotas)}%")
        
        val file = java.io.File("test_conservacion_notas.txt")
        file.writeText(output.toString())
        println("\n💾 Resultado guardado en: test_conservacion_notas.txt")
    }
}
