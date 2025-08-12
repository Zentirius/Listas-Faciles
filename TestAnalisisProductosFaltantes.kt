package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.model.Producto

fun main() {
    println("🔍 ANÁLISIS DE PRODUCTOS FALTANTES - EJECUTANDO PARSER")
    println("=".repeat(70))
    
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

    println("📋 LISTA ORIGINAL:")
    println(listaCompleta)
    println("\n" + "=".repeat(70))

    val productos = QuantityParser.parse(listaCompleta)
    
    println("📊 TOTAL PRODUCTOS DETECTADOS: ${productos.size}")
    println("\n📝 PRODUCTOS DETECTADOS:")
    productos.forEachIndexed { i, p ->
        val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
        val unidad = p.unidad?.let { " $it" } ?: ""
        println("${i+1}. ${p.nombre} (${cantidad}${unidad}) - Original: '${p.original}'")
    }

    println("\n" + "=".repeat(70))
    println("🎯 ANÁLISIS DE PRODUCTOS ESPERADOS:")
    
    // Lista completa de productos que deberían estar
    val productosEsperados = mapOf(
        "pastelera" to "pastelera con albaca marca",
        "leche sin lactosa" to "Leche sin lactosa 2.",
        "mantequilla" to "Mantequilla 1 barata.",
        "lechugas francesas" to "2 lechugas francesas preguntar...cual son...",
        "carne molida" to "Carne molida más barata en bandeja",
        "tallarines" to "Tallarines 4 en oferta.",
        "tomates" to "1malla tomates",
        "huevo" to "1 huevo marca yemita o el más barato",
        "leche asadas" to "leche asadas",
        "sandias" to "6sandias,8tomates,6 zanaorias 5 zapatos",
        "zanaorias" to "6sandias,8tomates,6 zanaorias 5 zapatos",
        "zapatos" to "6sandias,8tomates,6 zanaorias 5 zapatos",
        "lechuga" to "1 lechuga y pan",
        "pan" to "1 lechuga y pan (el más barato)",
        "huevos" to "6 huevos, 1 litro de leche no muy cara",
        "leche" to "1 litro de leche no muy cara",
        "plátanos" to "media docena de plátanos",
        "manzanas" to "3 manzanas",
        "espaguetis" to "1 paquete de espaguetis",
        "zanahorias" to "Zanahorias",
        "jugo de naranja" to "1.5 litros de jugo de naranja",
        "atún" to "2 latas de atún",
        "papa" to "1.1papa mediana2.sandia grande 3.zapato grande",
        "sandia" to "1.1papa mediana2.sandia grande 3.zapato grande",
        "zapato" to "1.1papa mediana2.sandia grande 3.zapato grande",
        "salmon" to "1.salmon 1kg",
        "crema dental" to "crema dental con fluor marca colgate o sensodyne",
        "shampoo" to "Shampoo anticaspa 1.",
        "jabón líquido" to "Jabón líquido",
        "papel higiénico" to "3 rollos papel higiénico preguntar...cuál es mejor...",
        "detergente" to "Detergente en polvo más barato en caja",
        "esponjas" to "Esponjas 5 en oferta.",
        "arroz" to "1bolsa arroz",
        "aceite" to "1 aceite marca chef o el más económico",
        "champú" to "champú suave",
        "cepillos" to "4cepillos,12servilletas,3 desodorantes 7 calcetines",
        "servilletas" to "4cepillos,12servilletas,3 desodorantes 7 calcetines",
        "desodorantes" to "4cepillos,12servilletas,3 desodorantes 7 calcetines",
        "calcetines" to "4cepillos,12servilletas,3 desodorantes 7 calcetines",
        "azúcar" to "3 kg de azúcar",
        "sal" to "1 sal y vinagre",
        "vinagre" to "1 sal y vinagre (el más barato)",
        "pilas" to "8 pilas, 2 metros de cable no muy caro",
        "cable" to "2 metros de cable no muy caro",
        "velas" to "media docena de velas",
        "focos" to "4 focos",
        "alcohol" to "750ml de alcohol",
        "algodón" to "1 paquete de algodón",
        "papel aluminio" to "Papel aluminio",
        "cinta adhesiva" to "2.5 metros de cinta adhesiva",
        "pegamento" to "3 tubos de pegamento",
        "bombilla" to "2.bombilla grande",
        "zapallo" to "3.zapallo chino",
        "madera" to "1.2metros de madera",
        "desinfectante" to "3.desinfectante",
        "rollos" to "ocho rollos"
    )

    val noDetectados = mutableListOf<String>()
    val detectadosCorrectamente = mutableListOf<String>()
    
    productosEsperados.forEach { (nombreEsperado, textoOriginal) ->
        val encontrado = productos.any { it.nombre.contains(nombreEsperado, ignoreCase = true) }
        if (encontrado) {
            detectadosCorrectamente.add(nombreEsperado)
        } else {
            noDetectados.add("$nombreEsperado (de: '$textoOriginal')")
        }
    }

    println("\n✅ PRODUCTOS DETECTADOS CORRECTAMENTE (${detectadosCorrectamente.size}):")
    detectadosCorrectamente.forEach { println("  ✓ $it") }

    println("\n❌ PRODUCTOS NO DETECTADOS (${noDetectados.size}):")
    noDetectados.forEach { println("  ✗ $it") }

    println("\n" + "=".repeat(70))
    println("📈 RESUMEN:")
    println("  Total esperados: ${productosEsperados.size}")
    println("  Detectados correctamente: ${detectadosCorrectamente.size}")
    println("  No detectados: ${noDetectados.size}")
    println("  Porcentaje de detección: ${(detectadosCorrectamente.size * 100 / productosEsperados.size)}%")

    if (noDetectados.isNotEmpty()) {
        println("\n🔧 PRODUCTOS CRÍTICOS PARA ARREGLAR:")
        println("Los siguientes productos requieren mejoras en el parser:")
        noDetectados.take(10).forEach { println("  🎯 $it") }
    }

    // Guardar resultado en archivo
    val output = StringBuilder()
    output.appendLine("ANÁLISIS DE PRODUCTOS FALTANTES")
    output.appendLine("=".repeat(50))
    output.appendLine("Total detectados: ${productos.size}")
    output.appendLine("Total esperados: ${productosEsperados.size}")
    output.appendLine("No detectados: ${noDetectados.size}")
    output.appendLine("")
    output.appendLine("PRODUCTOS NO DETECTADOS:")
    noDetectados.forEach { output.appendLine("- $it") }
    
    val file = java.io.File("analisis_productos_faltantes.txt")
    file.writeText(output.toString())
    println("\n💾 Resultado guardado en: analisis_productos_faltantes.txt")
}
