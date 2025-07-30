package com.example.proyectorestaurado.utils

import org.junit.Assert.*
import org.junit.Test

class QuantityParserRealListsTest {
    @Test
    fun testTipoLista1() {
        val input = """
pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
Leche sin lactosa 2.
Mantequilla 1 barata.
2 lechugas francesas preguntar...cual son...
Carne molida más barata en bandeja
Tallarines 4 en oferta.
1malla tomates
1 huevo marca yemita o el más barato
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("pastelera", true) && it.brand?.contains("maipo") == true })
        assertTrue(items.any { it.name.contains("leche", true) && it.quantity == 2.0 })
        assertTrue(items.any { it.name.contains("mantequilla", true) && it.notes?.contains("barata") == true })
        assertTrue(items.any { it.name.contains("tallarines", true) && it.quantity == 4.0 })
        assertTrue(items.any { it.name.contains("huevo", true) && it.brand?.contains("yemita") == true })
    }

    @Test
    fun testTipoLista2() {
        val input = "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("leche asada", true) })
        assertTrue(items.any { it.name.contains("sandia", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("tomate", true) && it.quantity == 8.0 })
        assertTrue(items.any { it.name.contains("zanahoria", true) && it.quantity == 6.0 })
        assertFalse(items.any { it.name.contains("zapato", true) })
    }

    @Test
    fun testTipoLista3() {
        val input = """
1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
Zanahorias
1.5 litros de jugo de naranja,,,2 latas de atún.
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("tomate", true) && it.quantity == 2.0 && it.unit == "kg" })
        assertTrue(items.any { it.name.contains("pan", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("plátano", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("huevo", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("carne", true) && it.unit == "gr" })
        assertTrue(items.any { it.name.contains("jugo de naranja", true) && it.unit == "litros" })
        assertTrue(items.any { it.name.contains("atún", true) && it.unit == "latas" })
    }

    @Test
    fun testTipoLista4() {
        val input = "1.1papa mediana2.sandia grande\n1.salmon 1kg\n2.zanaorias"
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("papa", true) })
        assertTrue(items.any { it.name.contains("sandia", true) })
        assertTrue(items.any { it.name.contains("salmon", true) && it.unit == "kg" })
        assertTrue(items.any { it.name.contains("zanahoria", true) })
    }

    @Test
    fun testTipoLista5() {
        val input = "cinco tomates\nocho zanahorias\nseis tomates ocho papas"
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("tomate", true) && it.quantity == 5.0 })
        assertTrue(items.any { it.name.contains("zanahoria", true) && it.quantity == 8.0 })
        assertTrue(items.any { it.name.contains("tomate", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("papa", true) && it.quantity == 8.0 })
    }

    @Test
    fun testTipoLista6() {
        val input = """
crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
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
cinco focos ocho rollos
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("pasta de dientes", true) && it.brand?.contains("colgate") == true })
        assertTrue(items.any { it.name.contains("jabón", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("detergente", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("aceite", true) && it.brand?.contains("chef") == true })
        assertTrue(items.any { it.name.contains("arroz", true) })
        assertTrue(items.any { it.name.contains("azúcar", true) && it.unit == "kg" })
        assertTrue(items.any { it.name.contains("pilas", true) && it.quantity == 8.0 })
        assertTrue(items.any { it.name.contains("cable", true) && it.unit == "metros" })
        assertTrue(items.any { it.name.contains("velas", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("alcohol", true) && it.unit == "ml" })
        assertTrue(items.any { it.name.contains("algodón", true) && it.unit == "paquete" })
        assertTrue(items.any { it.name.contains("papel aluminio", true) })
        assertTrue(items.any { it.name.contains("cinta adhesiva", true) && it.unit == "metros" })
        assertTrue(items.any { it.name.contains("pegamento", true) && it.unit == "tubos" })
        assertTrue(items.any { it.name.contains("shampoo", true) && it.unit == "ml" })
        assertTrue(items.any { it.name.contains("desodorante", true) && it.quantity == 3.0 })
        assertTrue(items.any { it.name.contains("velas", true) && it.quantity == 7.0 })
        assertTrue(items.any { it.name.contains("pilas", true) && it.quantity == 10.0 })
        assertTrue(items.any { it.name.contains("foco", true) && it.quantity == 5.0 })
        assertTrue(items.any { it.name.contains("rollo", true) && it.quantity == 8.0 })
    }
}
