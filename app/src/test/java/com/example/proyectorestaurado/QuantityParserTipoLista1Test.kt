package com.example.proyectorestaurado

import com.example.proyectorestaurado.utils.QuantityParser
import com.example.proyectorestaurado.utils.ParseMode
import org.junit.Test

class QuantityParserTipoLista1Test {
    @Test
    fun testTipoLista1() {
        val lista = """
pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
Leche sin lactosa 2.
Mantequilla 1 barata.
2 lechugas francesas preguntar...cual son...
Carne molida más barata en bandeja
Tallarines 4 en oferta.
1malla tomates
1 huevo marca yemita o el más barato
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(lista, ParseMode.LIBRE)
        println("Productos detectados: ${items.size}")
        items.forEachIndexed { i, item -> println("${i+1}. $item") }
        // No hacemos assert, solo mostramos resultado real para depuración
    }
}
