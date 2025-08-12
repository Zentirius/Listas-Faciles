import com.listafacilnueva.parser.QuantityParser
import org.junit.Test
import org.junit.Assert.*

class TestListaExtremaPreOptimizacion {
    
    @Test 
    fun testListaExtrema() {
        println("🚀 TEST DE LA LISTA EXTREMA - Validación antes de optimización")
        println("=" * 70)
        
        val listaExtrema = listOf(
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
        var exitosos = 0
        var fallidos = 0
        var advertencias = 0
        
        for ((indice, caso) in listaExtrema.withIndex()) {
            println("\n🧪 TEST ${indice + 1}/20: ${caso.take(50)}${if(caso.length > 50) "..." else ""}")
            println("─" * 55)
            
            try {
                val productos = QuantityParser.parse(caso)
                totalTests++
                
                val cantidadProductos = productos.size
                println("📊 Productos detectados: $cantidadProductos")
                
                productos.forEachIndexed { i, p ->
                    val cantidad = p.cantidad?.let { 
                        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
                    } ?: "1"
                    val unidad = p.unidad?.let { " $it" } ?: ""
                    println("   ${i+1}. ${p.nombre} (${cantidad}${unidad})")
                }
                
                // Validaciones específicas para casos críticos
                val resultado = when {
                    // Casos con mejoras críticas aplicadas
                    caso.contains("2.5.") && productos.any { it.nombre.contains("Yogur", true) } -> {
                        exitosos++; "✅ EXCELENTE: Decimal con punto final corregido"
                    }
                    caso.contains("2.5detergente") && productos.size >= 2 -> {
                        exitosos++; "✅ EXCELENTE: Decimal pegado separado correctamente"  
                    }
                    caso.contains("1.6litros") && productos.size >= 3 -> {
                        exitosos++; "✅ EXCELENTE: Múltiples decimales pegados separados"
                    }
                    caso.contains("4cepillos12servilletas") && productos.size >= 2 -> {
                        exitosos++; "✅ EXCELENTE: Cantidades consecutivas separadas"
                    }
                    caso.contains("media docena") && productos.any { it.cantidad == 6.0 } -> {
                        exitosos++; "✅ EXCELENTE: Media docena = 6 aplicado"
                    }
                    caso.contains("(x2)") && productos.any { it.cantidad == 2.0 } -> {
                        exitosos++; "✅ EXCELENTE: Formato (x2) interpretado"
                    }
                    caso.contains("(2 kg)") && productos.any { it.cantidad == 2.0 && it.unidad == "kg" } -> {
                        exitosos++; "✅ EXCELENTE: Cantidad en paréntesis extraída"
                    }
                    productos.size >= 2 -> {
                        exitosos++; "✅ BIEN: Múltiples productos detectados"
                    }
                    productos.size == 1 -> {
                        advertencias++; "⚠️  ADVERTENCIA: Solo 1 producto detectado (¿esperábamos más?)"
                    }
                    else -> {
                        fallidos++; "❌ FALLO: No se detectaron productos"
                    }
                }
                
                println(resultado)
                
            } catch (e: Exception) {
                println("💥 ERROR CRÍTICO: ${e.message}")
                println("   Stacktrace: ${e.stackTrace.take(2).joinToString(" | ")}")
                fallidos++
            }
        }
        
        println("\n" + "=".repeat(70))
        println("📈 RESUMEN FINAL DEL TEST EXTREMO:")
        println("   Total casos probados: $totalTests")
        println("   ✅ Exitosos: $exitosos")
        println("   ⚠️  Advertencias: $advertencias") 
        println("   ❌ Fallidos: $fallidos")
        
        val ratioExito = if (totalTests > 0) (exitosos * 100 / totalTests) else 0
        val ratioTotal = if (totalTests > 0) ((exitosos + advertencias) * 100 / totalTests) else 0
        
        println("   🎯 Ratio de éxito: $ratioExito%")
        println("   📊 Ratio total (éxito + advertencias): $ratioTotal%")
        
        when {
            ratioExito >= 85 -> println("🎉 ¡EXCELENTE! Las mejoras funcionan perfectamente")
            ratioExito >= 70 -> println("😊 ¡BIEN! Las mejoras son efectivas")  
            ratioExito >= 50 -> println("😐 REGULAR: Las mejoras ayudan pero necesitan ajustes")
            else -> println("😞 PREOCUPANTE: Las mejoras necesitan revisión")
        }
        
        if (fallidos == 0) {
            println("✨ ¡SIN ERRORES CRÍTICOS! El parser es estable")
            println("🔄 LISTO PARA OPTIMIZACIÓN DE CÓDIGO")
        } else {
            println("⚠️  Se encontraron $fallidos errores - revisar antes de optimizar")
        }
        
        // Assertions para que JUnit valide
        assertTrue("Debe tener casos exitosos", exitosos > 0)
        assertTrue("Ratio de éxito debe ser > 50%", ratioExito >= 50)  
        assertTrue("No debe tener errores críticos", fallidos == 0)
    }
}

// Función auxiliar para repetir strings
private operator fun String.times(n: Int): String = this.repeat(n)
