package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto
import org.junit.Assert.*
import org.junit.Test

class ParserTest {

    private fun printAndAssert(input: String, expectedSize: Int, assertions: (List<Producto>) -> Unit) {
        println("--- INICIO TEST ---")
        println("Input: \"$input\"")
        val productos = QuantityParser.parse(input)
        println("Productos Parseados (${productos.size}):")
        productos.forEach { println("  - $it") }
        println("--- FIN TEST ---")
        assertEquals("El número de productos no coincide.", expectedSize, productos.size)
        assertions(productos)
    }

    @Test
    fun `test ejemplo 1 - marcas, notas y cantidades`() {
        val input = "pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)\nLeche sin lactosa 2.\nMantequilla 1 barata.\n2 lechugas francesas preguntar...cual son...\nCarne molida más barata en bandeja\nTallarines 4 en oferta.\n1malla tomates\n1 huevo marca yemita o el más barato"
        printAndAssert(input, 8) { productos ->
            assertTrue(productos.any { it.nombre.contains("pastelera") && it.marcas.contains("frutos del maipo") && it.marcas.contains("minuto verde") })
            assertTrue(productos.any { it.nombre.contains("Leche sin lactosa") && it.cantidad == 2.0 })
            assertTrue(productos.any { it.nombre.contains("Mantequilla") && it.cantidad == 1.0 && it.nota?.contains("barata") == true })
            assertTrue(productos.any { it.nombre.contains("lechugas francesas") && it.cantidad == 2.0 && it.nota?.contains("preguntar") == true })
            assertTrue(productos.any { it.nombre.contains("huevo") && it.marcas.contains("yemita") && it.marcas.contains("el más barato") })
        }
    }

    @Test
    fun `test ejemplo 2 - puntuacion irregular y agrupacion`() {
        val input = "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"
        printAndAssert(input, 5) { productos ->
            assertTrue(productos.any { it.nombre.contains("leche asadas") })
            assertTrue(productos.any { it.nombre.contains("sandias") && it.cantidad == 6.0 })
            assertTrue(productos.any { it.nombre.contains("tomates") && it.cantidad == 8.0 })
            assertTrue(productos.any { it.nombre.contains("zanaorias") && it.cantidad == 6.0 })
            assertTrue(productos.any { it.nombre.contains("zapatos") && it.cantidad == 5.0 })
        }
    }

    @Test
    fun `test ejemplo 3 - palabras numericas y unidades`() {
        val input = "cinco tomates\nocho zanahorias\nplátanos (0.5 docena)\n1.5 litros de jugo de naranja,,,2 latas de atún."
        printAndAssert(input, 5) { productos ->
            assertTrue(productos.any { it.nombre.contains("tomates") && it.cantidad == 5.0 })
            assertTrue(productos.any { it.nombre.contains("zanahorias") && it.cantidad == 8.0 })
            assertTrue(productos.any { it.nombre.contains("plátanos") && it.nota?.contains("0.5 docena") == true })
            assertTrue(productos.any { it.nombre.contains("jugo de naranja") && it.cantidad == 1.5 && it.unidad == "lt" })
            assertTrue(productos.any { it.nombre.contains("atún") && it.cantidad == 2.0 && it.unidad == "lata" })
        }
    }

    @Test
    fun `test ejemplo 4 - enumeraciones y conjunciones`() {
        val input = "1.2 kg de tomates, 1 lechuga y pan (el más barato)\n6 huevos, 1 litro de leche no muy cara"
        printAndAssert(input, 5) { productos ->
            assertTrue(productos.any { it.nombre.contains("tomates") && it.cantidad == 1.2 && it.unidad == "kg" })
            assertTrue(productos.any { it.nombre.contains("lechuga") })
            assertTrue(productos.any { it.nombre.contains("pan") && it.nota?.contains("el más barato") == true })
            assertTrue(productos.any { it.nombre.contains("huevos") && it.cantidad == 6.0 })
            assertTrue(productos.any { it.nombre.contains("leche") && it.unidad == "lt" && it.nota?.contains("no muy cara") == true })
        }
    }

    @Test
    fun `test ejemplo 5 - errores de interpretacion de lista`() {
        val input = "Tipo 4 el 1. 2. no son cantidades es orden de lista\n1.1papa mediana2.sandia grande"
        printAndAssert(input, 2) { productos ->
            assertTrue(productos.any { it.nombre.contains("papa mediana") })
            assertTrue(productos.any { it.nombre.contains("sandia grande") })
            assertFalse(productos.any { it.nombre.contains("Tipo 4") })
        }
    }
}
