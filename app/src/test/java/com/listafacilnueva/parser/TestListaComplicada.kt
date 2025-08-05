package com.listafacilnueva.parser

import org.junit.Test
import org.junit.Assert.*

class TestListaComplicada {
    
    @Test
    fun `test lista complicada extrema - prueba de tension`() {
        val listaComplicada = """
crema dental con fluor marca colgate o sensodyne.(es 1 tubo de pasta dental)
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

6sandias,8tomates,6 zanahorias 5 zapatos
1malla tomates
ocho zanahorias
cinco tomates y pan
1 lechuga y pan
2 bolsas de arroz
3 cuartos de litro de aceite
        """.trimIndent()
        
        println("=== PRUEBA DE TENSIÓN - LISTA COMPLICADA EXTREMA ===")
        println("Input:")
        println(listaComplicada)
        println("\n" + "=".repeat(60))
        
        val productos = QuantityParser.parse(listaComplicada)
        
        println("PRODUCTOS PARSEADOS (${productos.size}):")
        productos.forEachIndexed { index, producto ->
            println("${index + 1}. ${producto.nombre}")
            if (producto.cantidad != null) println("   Cantidad: ${producto.cantidad}")
            if (producto.unidad != null) println("   Unidad: ${producto.unidad}")
            if (producto.marcas.isNotEmpty()) println("   Marcas: ${producto.marcas}")
            if (producto.nota != null) println("   Nota: ${producto.nota}")
            println("   Original: ${producto.original}")
            println()
        }
        
        println("=== ANÁLISIS DE CASOS CRÍTICOS ===")
        
        // Buscar casos específicos
        val casosEsperados = mapOf(
            "1bolsa arroz" to "bolsa",
            "3 kg de azúcar" to "kg",
            "2 metros de cable" to "m",
            "1.2metros cable2.bombilla grande" to "2 productos",
            "cinco focos ocho rollos" to "2 productos",
            "6sandias" to "6.0"
        )
        
        casosEsperados.forEach { (caso, esperado) ->
            val encontrado = productos.any { it.original?.contains(caso.split(" ")[0]) == true }
            println("✓ Caso '$caso' → $esperado: ${if (encontrado) "ENCONTRADO" else "NO ENCONTRADO"}")
        }
        
        println("\n=== RESUMEN ===")
        println("Total productos parseados: ${productos.size}")
        println("Casos con cantidad: ${productos.count { it.cantidad != null }}")
        println("Casos con unidad: ${productos.count { it.unidad != null }}")
        println("Casos con marcas: ${productos.count { it.marcas.isNotEmpty() }}")
        println("Casos con notas: ${productos.count { it.nota != null }}")
        
        // Validación básica
        assertTrue("Debe parsear al menos 25 productos", productos.size >= 25)
        assertTrue("Debe tener casos con cantidad", productos.any { it.cantidad != null })
        assertTrue("Debe tener casos con unidad", productos.any { it.unidad != null })
        
        println("\n🎉 ¡PRUEBA DE TENSIÓN COMPLETADA! 🎉")
    }
}
