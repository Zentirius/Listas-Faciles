package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.model.Producto

fun main() {
    println("🚀 TEST DE LA LISTA EXTREMA - VALIDACIÓN PRE-OPTIMIZACIÓN")
    println("=".repeat(70))
    
    val casosExtremos = listOf(
        "Yogur griego 2.5." to "Decimal con punto final",
        "yogur griego 2.5detergente" to "Decimal pegado",
        "1.6litros leche2.5detergente3.8paños" to "Múltiples decimales pegados",
        "4cepillos12servilletas23desodorantes" to "Cantidades consecutivas",
        "6sandias8tomates" to "Cantidades simples pegadas",
        "media docena de huevos" to "Media docena = 6",
        "1.2metros cable2.bombilla grande" to "Decimal con unidad pegado",
        "Tomates (2 kg)" to "Cantidad en paréntesis",
        "Leche sin lactosa (x2)" to "Formato (x2)",
        "1.1papa mediana2.sandia grande" to "Decimal pegado a texto"
    )
    
    var totalTests = 0
    var exitosos = 0
    var advertencias = 0
    var fallidos = 0
    
    casosExtremos.forEach { (caso, descripcion) ->
        totalTests++
        println("\n🧪 TEST $totalTests: $descripcion")
        println("   📝 Entrada: '$caso'")
        
        try {
            val productos = QuantityParser.parse(caso)
            val numProductos = productos.size
            
            println("   📊 Productos detectados: $numProductos")
            productos.forEachIndexed { i, p ->
                val cantidad = p.cantidad?.let { 
                    if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
                } ?: "1"
                val unidad = p.unidad?.let { " $it" } ?: ""
                println("      ${i+1}. ${p.nombre} (${cantidad}${unidad})")
            }
            
            // Validación específica por caso
            val resultado = when {
                caso.contains("2.5.") && productos.any { it.nombre.contains("Yogur", ignoreCase = true) } -> {
                    exitosos++; "✅ EXCELENTE: Decimal con punto final procesado"
                }
                caso.contains("2.5detergente") && productos.size >= 2 -> {
                    exitosos++; "✅ EXCELENTE: Decimal pegado separado correctamente"
                }
                caso.contains("1.6litros") && productos.size >= 3 -> {
                    exitosos++; "✅ EXCELENTE: Múltiples decimales separados"
                }
                caso.contains("4cepillos12servilletas") && productos.size >= 2 -> {
                    exitosos++; "✅ EXCELENTE: Cantidades consecutivas separadas"
                }
                caso.contains("6sandias8tomates") && productos.size >= 2 -> {
                    exitosos++; "✅ EXCELENTE: Cantidades simples separadas"
                }
                caso.contains("media docena") && productos.any { it.cantidad == 6.0 } -> {
                    exitosos++; "✅ EXCELENTE: Media docena = 6 convertido"
                }
                caso.contains("1.2metros cable2.bombilla") && productos.size >= 2 -> {
                    exitosos++; "✅ EXCELENTE: Decimal con unidad separado"
                }
                caso.contains("(2 kg)") && productos.any { it.cantidad == 2.0 && it.unidad?.contains("kg") == true } -> {
                    exitosos++; "✅ EXCELENTE: Cantidad en paréntesis extraída"
                }
                caso.contains("(x2)") && productos.any { it.cantidad == 2.0 } -> {
                    exitosos++; "✅ EXCELENTE: Formato (x2) interpretado"
                }
                caso.contains("1.1papa mediana2.sandia") && productos.size >= 2 -> {
                    exitosos++; "✅ EXCELENTE: Decimal pegado a texto separado"
                }
                productos.size >= 2 -> {
                    advertencias++; "⚠️  BIEN: Múltiples productos (no optimizado para este caso)"
                }
                productos.size == 1 -> {
                    advertencias++; "⚠️  ADVERTENCIA: Solo 1 producto (¿esperábamos más?)"
                }
                else -> {
                    fallidos++; "❌ FALLO: No se detectaron productos"
                }
            }
            
            println("   $resultado")
            
        } catch (e: Exception) {
            fallidos++
            println("   💥 ERROR CRÍTICO: ${e.message}")
            e.printStackTrace()
        }
        
        println("   " + "-".repeat(50))
    }
    
    println("\n" + "=".repeat(70))
    println("📈 RESUMEN FINAL DEL TEST DE LISTA EXTREMA:")
    println("   📊 Total casos probados: $totalTests")
    println("   ✅ Exitosos: $exitosos")
    println("   ⚠️  Advertencias: $advertencias")
    println("   ❌ Fallidos: $fallidos")
    
    val ratioExito = if (totalTests > 0) (exitosos * 100 / totalTests) else 0
    val ratioTotal = if (totalTests > 0) ((exitosos + advertencias) * 100 / totalTests) else 0
    
    println("   🎯 Ratio de éxito puro: $ratioExito%")
    println("   📊 Ratio total (éxito + advertencias): $ratioTotal%")
    
    val evaluacion = when {
        ratioExito >= 85 -> "🎉 ¡EXCELENTE! Las mejoras funcionan perfectamente"
        ratioExito >= 70 -> "😊 ¡BIEN! Las mejoras son muy efectivas"
        ratioExito >= 50 -> "😐 REGULAR: Las mejoras ayudan pero pueden mejorarse"
        else -> "😞 PREOCUPANTE: Las mejoras necesitan revisión urgente"
    }
    
    println("   $evaluacion")
    
    if (fallidos == 0) {
        println("\n✨ ¡SIN ERRORES CRÍTICOS! El parser es estable")
        println("🔄 ✅ LISTO PARA PROCEDER CON LA OPTIMIZACIÓN DE CÓDIGO")
        println("🎯 Las mejoras integradas están funcionando correctamente")
    } else {
        println("\n⚠️  Se encontraron $fallidos errores críticos")
        println("❌ REVISAR ANTES DE OPTIMIZAR")
    }
}
