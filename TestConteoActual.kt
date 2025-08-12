package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser

fun main() {
    println("🎯 ANÁLISIS PRECISO - CONTEO ACTUAL DEL PARSER")
    println("=".repeat(60))
    
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



1.1papa mediana2.sandia grande 3.zapato grande

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

1.2metros cable 2.bombilla grande 3.zapallo chino

1.2metros de madera 3.8 metros de cable 
2.desodorantes
3.desinfectante

siete velas
diez pilas

cinco focos ocho rollos
    """.trimIndent()

    val productos = QuantityParser.parse(listaCompleta)
    
    println("📊 CONTEO ACTUAL: ${productos.size} productos detectados")
    println("\nLISTA COMPLETA DE PRODUCTOS DETECTADOS:")
    println("=".repeat(50))
    
    productos.forEachIndexed { i, p ->
        val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
        val unidad = p.unidad?.let { " $it" } ?: ""
        println("${String.format("%2d", i+1)}. ${p.nombre} (${cantidad}${unidad})")
    }
    
    println("\n" + "=".repeat(60))
    println("🔍 Si el parser actual detecta 68 productos,")
    println("   entonces necesitamos identificar los 2 productos")
    println("   específicos que faltan para llegar a 70.")
    println("\n📝 REVISAR: ¿Hay algún producto obvio que debería")
    println("   estar en esta lista pero no aparece?")
    
    // Guardar la lista actual para revisión
    val output = StringBuilder()
    output.appendLine("PRODUCTOS DETECTADOS ACTUALMENTE (${productos.size} total):")
    output.appendLine("=" .repeat(50))
    productos.forEachIndexed { i, p ->
        val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
        val unidad = p.unidad?.let { " $it" } ?: ""
        output.appendLine("${String.format("%2d", i+1)}. ${p.nombre} (${cantidad}${unidad})")
    }
    
    val file = java.io.File("productos_actuales_detectados.txt")
    file.writeText(output.toString())
    println("\n💾 Lista guardada en: productos_actuales_detectados.txt")
}
