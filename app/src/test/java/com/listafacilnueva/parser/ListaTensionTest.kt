package com.listafacilnueva.parser

import org.junit.Test
import org.junit.Assert.assertEquals

class ListaTensionTest {

    @Test
    fun `test lista completa de tensión debe arrojar 68 elementos`() {
        val input = """
            pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
            Leche sin lactosa 2.
            Mantequilla 1 barata.
            2 lechugas francesas preguntar...cual son...
            Carne molida más barata en bandeja
            Tallarines 4 en oferta.
            1malla tomates
            1 huevo marca yemita o el más barato

             leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos

            1. 2 kg de tomates, 1 lechuga y pan (el más barato)
            6 huevos, 1 litro de leche no muy cara
            media docena de plátanos. 3 manzanas
            500gr de carne molida 1 paquete de espaguetis
            Zanahorias
            1.5 litros de jugo de naranja,,,2 latas de atún.

            1.1papa mediana2.sandia grande

            1.salmon 1kg
            2.zanaorias

            cinco tomates
            ocho zanahorias

            seis tomates ocho papas

            crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
            Shampoo anticaspa 1.
            Jabón líquido
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

            1.2metros cable 2.bombilla grande

            1.2metros de madera 3.8 metros de cable
            1.shampoo 500ml
            2.desodorantes

            siete velas
            diez pilas

            cinco focos ocho rollos
        """.trimIndent()

        println("=== TEST LISTA COMPLETA DE TENSIÓN ===")
        println("Parsing lista completa...")
        
        val productos = QuantityParser.parse(input)
        
        println("\n📊 RESULTADO:")
        println("Productos encontrados: ${productos.size}")
        println("Objetivo esperado: 68")
        
        println("\n📋 LISTA DETALLADA:")
        productos.forEachIndexed { index, producto ->
            println("  ${index + 1}. ${producto.nombre} (${producto.cantidad ?: 1}${producto.unidad ?: "u"})")
        }
        
        // Análisis por categorías
        val conCantidad = productos.filter { it.cantidad != null }
        val sinCantidad = productos.filter { it.cantidad == null }
        val conUnidad = productos.filter { it.unidad != null }
        val conMarcas = productos.filter { it.marcas.isNotEmpty() }
        val conNota = productos.filter { it.nota != null }
        
        println("\n📈 ANÁLISIS DETALLADO:")
        println("  - Con cantidad específica: ${conCantidad.size}")
        println("  - Sin cantidad específica: ${sinCantidad.size}")
        println("  - Con unidad: ${conUnidad.size}")
        println("  - Con marcas: ${conMarcas.size}")
        println("  - Con notas: ${conNota.size}")
        
        // Verificar que sea exactamente 68
        if (productos.size == 68) {
            println("\n✅ ÉXITO: Parser alcanzó el objetivo de 68 productos")
        } else {
            println("\n❌ DIFERENCIA: Se esperaban 68, se obtuvieron ${productos.size}")
            val diferencia = 68 - productos.size
            if (diferencia > 0) {
                println("   Faltan $diferencia productos")
            } else {
                println("   Hay ${-diferencia} productos de más")
            }
        }
        
        println("\n=== FIN DEL TEST ===")
        
        // Assertion para que falle si no son exactamente 68
        assertEquals("La lista de tensión debe generar exactamente 68 productos", 68, productos.size)
    }
}
