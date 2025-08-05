package com.listafacilnueva.parser

import org.junit.Test

class RobustnessTest {

    private fun testParser(input: String, description: String) {
        println("\n=== $description ===")
        println("Input: \"$input\"")
        val productos = QuantityParser.parse(input)
        println("Productos Parseados (${productos.size}):")
        productos.forEach { println("  - $it") }
    }

    @Test
    fun `test robustez con listas variadas`() {
        // Lista típica de supermercado
        testParser(
            "pan integral 2 unidades\nleche descremada 1 litro\n500gr carne molida\ntomates cherry 1 bandeja\nqueso gouda marca colun\narroz 1 kg",
            "Lista típica de supermercado"
        )

        // Lista con errores de tipeo
        testParser(
            "3 manznas rojas\n2lt aceite girasol\npapas 2kilos\n1docena huevos\nsal fina 500g",
            "Lista con errores de tipeo"
        )

        // Lista muy informal
        testParser(
            "comprar pan\nleche si hay oferta\n2 o 3 naranjas\nalgún queso rico\ncarne para el asado del domingo",
            "Lista muy informal"
        )

        // Lista con marcas específicas
        testParser(
            "yogurt griego marca danone\ncereales nesquik\ndetergente omo 1kg\nshampoo head shoulders\njugo ades de naranja 1lt",
            "Lista con marcas específicas"
        )

        // Lista mezclada con números y texto
        testParser(
            "1. pan marraqueta\n2. leche entera\n3. 500g jamón\nademás: tomates, lechuga, palta\ny también 2 botellas de agua",
            "Lista mezclada con números y texto"
        )

        // Lista con cantidades complejas
        testParser(
            "medio kilo de azúcar\nun cuarto de mantequilla\ndos docenas de huevos\ntres cuartos de litro de aceite",
            "Lista con cantidades complejas"
        )
    }
}
