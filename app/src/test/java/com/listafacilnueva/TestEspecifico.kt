import org.junit.Test
import com.listafacilnueva.parser.QuantityParser

class TestEspecifico {
    
    @Test
    fun testCaso3ProductosCritico() {
        println("🧪 TESTE ESPECÍFICO: 1.2metros cable 2.bombilla grande 3.zapallo chino")
        
        val entrada = "1.2metros cable 2.bombilla grande 3.zapallo chino"
        val productos = QuantityParser.parse(entrada)
        
        println("📝 INPUT: '$entrada'")
        println("📊 PRODUCTOS DETECTADOS: ${productos.size}")
        
        productos.forEachIndexed { index, producto ->
            println("  ${index + 1}. '${producto.nombre}' - cantidad: ${producto.cantidad} - unidad: ${producto.unidad}")
        }
        
        println("\n🎯 ANÁLISIS:")
        println("  - ¿Se detectaron 3 productos? ${if (productos.size == 3) "✅ SÍ" else "❌ NO (${productos.size})"}")
        
        val cablesDetectados = productos.filter { it.nombre.contains("cable") || it.nombre.contains("metros") }
        val bombillasDetectadas = productos.filter { it.nombre.contains("bombilla") }
        val zapallosDetectados = productos.filter { it.nombre.contains("zapallo") }
        
        println("  - ¿Se detectó el cable? ${if (cablesDetectados.isNotEmpty()) "✅ SÍ" else "❌ NO"}")
        println("  - ¿Se detectó la bombilla? ${if (bombillasDetectadas.isNotEmpty()) "✅ SÍ" else "❌ NO"}")  
        println("  - ¿Se detectó el zapallo? ${if (zapallosDetectados.isNotEmpty()) "✅ SÍ" else "❌ NO"}")
        
        if (productos.size == 3) {
            println("🎉 ¡ÉXITO! El caso crítico ahora funciona correctamente")
        } else {
            println("❌ FALLA: Se esperaban 3 productos, se obtuvieron ${productos.size}")
        }
    }
}
