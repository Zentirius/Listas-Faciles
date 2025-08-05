package com.listafacilnueva.parser

import org.junit.Test

class CompleteListTest {

    private fun testParser(input: String, description: String) {
        println("\n=== $description ===")
        println("Input: \"$input\"")
        val productos = QuantityParser.parse(input)
        println("Productos Parseados (${productos.size}):")
        productos.forEachIndexed { index, producto ->
            println("  ${index + 1}. $producto")
        }
    }

    @Test
    fun `test todos los tipos de listas`() {
        // Tipo 1 - Marcas y notas
        testParser(
            """pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
Leche sin lactosa 2.
Mantequilla 1 barata.
2 lechugas francesas preguntar...cual son...
Carne molida más barata en bandeja
Tallarines 4 en oferta.
1malla tomates
1 huevo marca yemita o el más barato""",
            "Tipo 1 - Marcas y notas"
        )

        // Tipo 2 - Cantidades pegadas
        testParser(
            "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos",
            "Tipo 2 - Cantidades pegadas"
        )

        // Tipo 3 - Lista compleja con cantidades
        testParser(
            """1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
Zanahorias
1.5 litros de jugo de naranja,,,2 latas de atún.""",
            "Tipo 3 - Lista compleja con cantidades"
        )

        // Tipo 4 - Listas numeradas problemáticas
        testParser(
            """Tipo 4 el 1. 2. no son cantidades es orden de lista

1.1papa mediana2.sandia grande

1.salmon 1kg
2.zanaorias""",
            "Tipo 4 - Listas numeradas problemáticas"
        )

        // Tipo 5 - Números como palabras
        testParser(
            """cinco tomates
ocho zanahorias

seis tomates ocho papas""",
            "Tipo 5 - Números como palabras"
        )

        // Tipo 6 - Lista completa del usuario
        testParser(
            """crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
Shampoo anticaspa 1.
Jabón líquido 2 barato.
3 rollos papel higiénico preguntar...cuál es mejor...
Detergente en polvo más barato en caja
Esponjas 5 en oferta.
1bolsa arroz
1 aceite marca chef o el más económico

champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines

2. 3 kg de azúcar, 1 sal y vinagre (el más barato)
8 pilas, 2 metros de cable no muy caro
media docena de velas. 4 focos
750ml de alcohol 1 paquete de algodón
Papel aluminio
2.5 metros de cinta adhesiva,,,3 tubos de pegamento.

1.2metros cable2.bombilla grande

1.shampoo 500ml
2.desodorantes

siete velas
diez pilas

cinco focos ocho rollos""",
            "Tipo 6 - Lista completa del usuario"
        )
    }
}
