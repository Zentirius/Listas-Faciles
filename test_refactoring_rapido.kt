import com.listafacilnueva.parser.QuantityParser

fun main() {
    println("🔍 TEST REFACTORING")
    
    val casos = listOf(
        "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
        "papa, tomate, cebolla" to 3,
        "2kg arroz" to 1
    )
    
    for ((texto, esperado) in casos) {
        val productos = QuantityParser.parse(texto)
        val ok = productos.size == esperado
        println("'$texto' -> ${productos.size} productos ${if (ok) "✅" else "❌"}")
    }
}

main()
