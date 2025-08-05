package com.listafacilnueva.parser

import org.junit.Test

class UserListTest {

    @Test
    fun `test lista específica del usuario`() {
        val input = """crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
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

cinco focos ocho rollos"""

        println("=== LISTA ESPECÍFICA DEL USUARIO ===")
        println("Input: \"$input\"")
        val productos = QuantityParser.parse(input)
        println("Productos Parseados (${productos.size}):")
        productos.forEachIndexed { index, producto ->
            println("  ${index + 1}. $producto")
        }
        println("=== FIN ===")
    }
}
