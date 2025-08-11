import org.junit.Test
import com.listafacilnueva.parser.QuantityParser

class TestCasoCriticoFinal {
    
    @Test
    fun testCasoCritico3ProductosDirecto() {
        println("\n" + "=".repeat(80))
        println("🧪 TEST FINAL: 1.2metros cable 2.bombilla grande 3.zapallo chino")
        println("=".repeat(80))
        
        val input = "1.2metros cable 2.bombilla grande 3.zapallo chino"
        println("📝 INPUT: '$input'")
        
        try {
            val productos = QuantityParser.parse(input)
            
            println("\n📊 RESULTADOS:")
            println("  - Productos detectados: ${productos.size}")
            
            productos.forEachIndexed { i, producto ->
                println("  ${i+1}. '${producto.nombre}' [${producto.cantidad ?: "?"}${producto.unidad ?: ""}]")
            }
            
            println("\n🎯 VERIFICACIONES:")
            val tienesCable = productos.any { it.nombre.contains("cable") || it.nombre.contains("metros") }
            val tienesBombilla = productos.any { it.nombre.contains("bombilla") }
            val tienesZapallo = productos.any { it.nombre.contains("zapallo") }
            
            println("  - ¿Cable detectado? ${if (tienesCable) "✅" else "❌"}")
            println("  - ¿Bombilla detectada? ${if (tienesBombilla) "✅" else "❌"}")
            println("  - ¿Zapallo detectado? ${if (tienesZapallo) "✅" else "❌"}")
            println("  - ¿Total = 3? ${if (productos.size == 3) "✅" else "❌"}")
            
            if (productos.size == 3 && tienesCable && tienesBombilla && tienesZapallo) {
                println("\n🎉 ¡ÉXITO COMPLETO! El caso crítico funciona perfectamente")
            } else {
                println("\n❌ AÚN HAY PROBLEMAS:")
                if (productos.size != 3) println("   - Se esperaban 3 productos, se obtuvieron ${productos.size}")
                if (!tienesCable) println("   - No se detectó el cable")
                if (!tienesBombilla) println("   - No se detectó la bombilla")
                if (!tienesZapallo) println("   - No se detectó el zapallo")
            }
            
        } catch (e: Exception) {
            println("❌ ERROR durante el parsing: ${e.message}")
            e.printStackTrace()
        }
        
        println("=".repeat(80))
    }
}
