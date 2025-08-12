import com.listafacilnueva.parser.QuantityParser

fun main() {
    println("=== TEST DEBUG: Producto comido ===")
    
    val textoProblematico = "1.2metros cable 2.bombilla grande 3.zapallo chino"
    println("\n🔥 TEXTO: '$textoProblematico'")
    
    val productos = QuantityParser.parse(textoProblematico)
    
    println("\n📊 RESULTADOS:")
    println("Total productos detectados: ${productos.size}")
    productos.forEachIndexed { index, producto ->
        println("${index + 1}. ${producto.cantidad} ${producto.nombre}")
    }
    
    println("\n✅ ESPERADO: 3 productos")
    println("1. 2metros cable")
    println("2. 1 bombilla grande")
    println("3. 1 zapallo chino")
    
    if (productos.size != 3) {
        println("\n❌ ERROR: Se esperaban 3 productos pero se encontraron ${productos.size}")
        println("ANÁLISIS: El parser se está 'comiendo' ${3 - productos.size} producto(s)")
    } else {
        println("\n✅ ÉXITO: Se detectaron correctamente los 3 productos")
    }
    
    // Test adicional con casos similares
    println("\n\n=== CASOS ADICIONALES ===")
    
    val casos = listOf(
        "1.1papa mediana2.sandia grande 3.zapato grande",
        "1.cable usb 2.bombilla led 3.zapallo chino",
        "1.2kg arroz 2.bombilla 3.zapallo"
    )
    
    casos.forEach { caso ->
        println("\nCaso: '$caso'")
        val productosTest = QuantityParser.parse(caso)
        println("Productos detectados: ${productosTest.size}")
        productosTest.forEach { producto ->
            println("  - ${producto.cantidad} ${producto.nombre}")
        }
    }
}
