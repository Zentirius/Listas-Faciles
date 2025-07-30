package com.example.proyectorestaurado.utils

import org.junit.Assert.*
import org.junit.Test

class QuantityParserTest {
    @Test
    fun testParseMultipleItems_tipo1() {
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
    fun testParseMultipleItems_tipo3() {
        val input = """
1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("tomate", true) && it.quantity == 2.0 && it.unit == "kg" })
        assertTrue(items.any { it.name.contains("pan", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("plátano", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("huevo", true) && it.quantity == 6.0 })
        assertTrue(items.any { it.name.contains("carne", true) && it.unit == "gr" })
    }

    @Test
    fun testParseMultipleItems_tipo6() {
        val input = """
crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
Shampoo anticaspa 1.
Jabón líquido 2 barato.
3 rollos papel higiénico preguntar...cuál es mejor...
Detergente en polvo más barato en caja
Esponjas 5 en oferta.
1bolsa arroz
1 aceite marca chef o el más económico
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(input)
        assertTrue(items.any { it.name.contains("pasta de dientes", true) && it.brand?.contains("colgate") == true })
        assertTrue(items.any { it.name.contains("jabón", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("detergente", true) && it.notes?.contains("barato") == true })
        assertTrue(items.any { it.name.contains("aceite", true) && it.brand?.contains("chef") == true })
    }
}
