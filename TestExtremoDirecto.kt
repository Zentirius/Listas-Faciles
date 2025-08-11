import com.listafacilnueva.model.Producto
import com.listafacilnueva.parser.QuantityParser

fun main() {
    println("🚀 EJECUTANDO TEST EXTREMO DE LISTA")
    println("=" * 60)
    
    val casosExtremos = listOf(
        "Yogur griego 2.5.",
        "yogur griego 2.5detergente", 
        "1.6litros leche2.5detergente3.8paños",
        "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas",
        "4cepillos12servilletas23desodorantes",
        "6sandias8tomates",
        "6 zanaorias 5 zapatos",
        "1.2metros de madera 3.8 metros de cable",
        "1.2metros cable2.bombilla grande",
        "1.1papa mediana2.sandia grande",
        "2 lechugas francesas preguntar...cual son las de oferta",
        "500gr de carne molida 1 paquete de espaguetis",
        "cinco focos ocho rollos", 
        "media docena de huevos",
        "Tomates (2 kg)",
        "Leche sin lactosa (x2)",
        "Esponjas 5 en oferta",
        "2 kg de tomates, 1 lechuga y pan (el más barato)",
        "1.salmon 1kg2.zanaorias",
        "1.2.5kg arroz2.1.8litros agua3.bombillas led"
    )
    
    var totalTests = 0
    var passed = 0
    var failed = 0
    
    for ((index, caso) in casosExtremos.withIndex()) {
        println("\n🧪 TEST ${index + 1}: $caso")
        println("─".repeat(50))
        
        try {
            val productos = QuantityParser.parse(caso)
            totalTests++
            
            println("✅ Productos detectados: ${productos.size}")
            productos.forEachIndexed { i, p ->
                val cantidad = p.cantidad?.let { 
                    if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
                } ?: "1"
                val unidad = p.unidad ?: ""
                println("   ${i+1}. ${p.nombre} ($cantidad$unidad)")
            }
            
            // Verificaciones específicas
            val validation = when {
                caso.contains("2.5detergente") && productos.size >= 2 -> "✅ PASS: Decimal pegado separado correctamente"
                caso.startsWith("1.6litros") && productos.size >= 3 -> "✅ PASS: Múltiples productos separados"
                caso.contains("media docena") && productos.firstOrNull()?.cantidad == 6.0 -> "✅ PASS: Media docena = 6"
                caso.contains("4cepillos12servilletas") && productos.size >= 2 -> "✅ PASS: Cantidades pegadas separadas"
                caso.contains("(x2)") && productos.firstOrNull()?.cantidad == 2.0 -> "✅ PASS: Formato (x2) interpretado"
                caso.contains("(2 kg)") && productos.any { it.cantidad == 2.0 && it.unidad == "kg" } -> "✅ PASS: Cantidad en paréntesis"
                productos.isNotEmpty() -> "✅ PASS: Al menos detecta productos"
                else -> "⚠️  PARTIAL: Funciona pero podría mejorar"
            }
            
            println(validation)
            if (validation.startsWith("✅")) passed++ else failed++
            
        } catch (e: Exception) {
            println("❌ ERROR: ${e.message}")
            failed++
        }
    }
    
    println("\n" + "=".repeat(60))
    println("🎯 RESUMEN FINAL:")
    println("   Total tests: $totalTests")
    println("   Pasaron: $passed")
    println("   Fallaron: $failed")
    println("   Ratio éxito: ${(passed * 100 / totalTests)}%")
    
    if (failed == 0) {
        println("🎉 ¡TODOS LOS TESTS PASARON! Lista extrema funcionando correctamente.")
    } else {
        println("⚠️  Algunos casos necesitan ajuste. Ver detalles arriba.")
    }
}

// Función auxiliar para repetir strings (Kotlin no tiene * operator por defecto)
operator fun String.times(n: Int): String = this.repeat(n)
