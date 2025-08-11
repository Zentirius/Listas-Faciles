import org.junit.Test
import com.listafacilnueva.parser.QuantityParser

class TestCorrecciones {
    
    @Test
    fun testCasoCritico3Productos() {
        println("=== TEST: 3 productos en numeración ===")
        val input = "1.2metros cable 2.bombilla grande 3.zapallo chino"
        val productos = QuantityParser.parse(input)
        
        println("Input: '$input'")
        println("Productos detectados: ${productos.size}")
        productos.forEachIndexed { i, p -> 
            println("  ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad ?: ""}]")
        }
        
        // Debería detectar 3 productos
        assert(productos.size == 3) { "Esperado: 3 productos, obtenido: ${productos.size}" }
    }
    
    @Test
    fun testCasoCriticoDecimal() {
        println("=== TEST: Cantidad decimal 2.5 ===")
        val input = "2.5 metros de cinta adhesiva,,,3 tubos de pegamento"
        val productos = QuantityParser.parse(input)
        
        println("Input: '$input'")
        println("Productos detectados: ${productos.size}")
        productos.forEachIndexed { i, p -> 
            println("  ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad ?: ""}]")
        }
        
        // Debería detectar 2 productos y el primer producto debe tener cantidad 2.5
        assert(productos.size >= 2) { "Esperado: al menos 2 productos, obtenido: ${productos.size}" }
        val primerProducto = productos.find { it.nombre.contains("cinta") }
        assert(primerProducto?.cantidad == 2.5) { "Esperado: cantidad 2.5, obtenido: ${primerProducto?.cantidad}" }
    }
    
    @Test
    fun testCantidadesPegadas() {
        println("=== TEST: Cantidades pegadas ===")
        val input = "6sandias,8tomates,6 zanaorias 5 zapatos"
        val productos = QuantityParser.parse(input)
        
        println("Input: '$input'")
        println("Productos detectados: ${productos.size}")
        productos.forEachIndexed { i, p -> 
            println("  ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad ?: ""}]")
        }
        
        // Debería detectar al menos 4 productos
        assert(productos.size >= 4) { "Esperado: al menos 4 productos, obtenido: ${productos.size}" }
    }
}
