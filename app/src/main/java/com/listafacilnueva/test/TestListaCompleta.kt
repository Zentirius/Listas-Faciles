package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser

object TestListaCompleta {
    @JvmStatic
    fun main(args: Array<String>) {
        println("🧪 TESTING LISTA COMPLETA DEL USUARIO")
        println("============================================================")
        
        val listaCompleta = """
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

Tipo 4 el 1. 2. 

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

1.2metros de madera 3.8 metros de cable esta si son metros no numero de item 
1.shampoo 500ml
2.desodorantes

siete velas
diez pilas

cinco focos ocho rollos
        """.trimIndent()
        
        try {
            val productos = QuantityParser.parse(listaCompleta)
            
            println("\n📊 RESULTADOS:")
            println("Total productos detectados: ${productos.size}")
            println("============================================================")
            
            productos.forEachIndexed { index, producto ->
                println("${index + 1}. ${producto.nombre} - ${producto.cantidad ?: "Sin cantidad"} ${producto.unidad ?: ""}")
                if (producto.marcas.isNotEmpty()) {
                    println("   Marcas: ${producto.marcas.joinToString(", ")}")
                }
                if (producto.nota != null) {
                    println("   Nota: ${producto.nota}")
                }
                println("   Original: '${producto.original}'")
                println()
            }
            
            println("============================================================")
            println("TOTAL FINAL: ${productos.size} productos")
            
            // Análisis del resultado
            when {
                productos.size >= 69 -> println("🎯 ¡OBJETIVO ALCANZADO! ${productos.size} productos detectados")
                productos.size >= 65 -> println("🔥 ¡MUY CERCA! ${productos.size} productos detectados (faltan ${69 - productos.size})")
                productos.size >= 60 -> println("⚠️ PROGRESO BUENO: ${productos.size} productos detectados (faltan ${69 - productos.size})")
                else -> println("❌ NECESITA MÁS MEJORAS: ${productos.size} productos detectados (faltan ${69 - productos.size})")
            }
            
        } catch (e: Exception) {
            println("❌ ERROR: ${e.message}")
            e.printStackTrace()
        }
    }
}
