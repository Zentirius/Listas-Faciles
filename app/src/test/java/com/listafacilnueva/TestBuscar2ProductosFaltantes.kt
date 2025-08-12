package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test

class TestBuscar2ProductosFaltantes {
    @Test
    fun testIdentificar2ProductosFaltantes() {
        println("🎯 BUSCAR LOS 2 PRODUCTOS FALTANTES")
        println("Objetivo: Identificar exactamente cuáles 2 productos faltan de los 70 esperados")
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

        // LISTA COMPLETA de los 70 productos esperados (contando duplicados como separados)
        val productosEsperados = listOf(
            "pastelera",
            "leche sin lactosa", 
            "mantequilla",
            "lechugas francesas",
            "carne molida",
            "tallarines", 
            "tomates", // #1
            "huevo",
            "leche asadas",
            "sandias",
            "tomates", // #2 (8 tomates)
            "zanaorias", // #1
            "zapatos",
            "tomates", // #3 (2 kg tomates)
            "lechuga",
            "pan",
            "huevos",
            "leche", // #2 (1 litro leche)
            "plátanos",
            "manzanas", 
            "carne molida", // #2 (500gr)
            "espaguetis",
            "zanahorias", // #2
            "jugo de naranja",
            "atún",
            "papa",
            "sandia",
            "zapato",
            "salmon",
            "zanaorias", // #3 (2 zanaorias)
            "tomates", // #4 (cinco tomates)
            "zanahorias", // #3 (ocho zanahorias)
            "tomates", // #5 (seis tomates)
            "papas",
            "crema dental",
            "shampoo",
            "jabón líquido",
            "papel higiénico", // SOSPECHOSO #1
            "detergente",
            "esponjas",
            "arroz",
            "aceite",
            "champú",
            "cepillos",
            "servilletas",
            "desodorantes", // #1
            "calcetines",
            "azúcar",
            "sal",
            "vinagre",
            "pilas", // #1
            "cable", // #1
            "velas", // #1
            "focos", // #1
            "alcohol",
            "algodón",
            "papel aluminio",
            "cinta adhesiva",
            "pegamento",
            "cable", // #2 (1.2metros cable)
            "bombilla", // SOSPECHOSO #2
            "zapallo",
            "madera",
            "cable", // #3 (3.8 metros cable)
            "desodorantes", // #2
            "desinfectante",
            "velas", // #2 (siete velas)
            "pilas", // #2 (diez pilas)
            "focos", // #2 (cinco focos)
            "rollos" // SOSPECHOSO #3
        )

        println("📊 TOTAL ESPERADO: ${productosEsperados.size} productos")
        
        val productosDetectados = QuantityParser.parse(listaCompleta)
        println("📊 TOTAL DETECTADO: ${productosDetectados.size} productos")
        
        println("\n📝 PRODUCTOS DETECTADOS:")
        productosDetectados.forEachIndexed { i, p ->
            val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
            val unidad = p.unidad?.let { " $it" } ?: ""
            println("${String.format("%2d", i+1)}. ${p.nombre} (${cantidad}${unidad})")
        }
        
        println("\n" + "=".repeat(70))
        println("🔍 ANÁLISIS DE PRODUCTOS FALTANTES:")
        
        val faltantes = mutableListOf<String>()
        
        // Verificar cada producto esperado
        productosEsperados.forEachIndexed { index, esperado ->
            val encontrado = productosDetectados.any { detectado ->
                detectado.nombre.contains(esperado, ignoreCase = true) ||
                esperado.contains(detectado.nombre, ignoreCase = true)
            }
            
            if (!encontrado) {
                faltantes.add("${index+1}. $esperado")
            }
        }
        
        println("❌ PRODUCTOS NO DETECTADOS (${faltantes.size}):")
        faltantes.forEach { println("   $it") }
        
        if (faltantes.size == 2) {
            println("\n🎯 ¡PERFECTO! Encontrados exactamente los 2 productos faltantes.")
        } else if (faltantes.size < 2) {
            println("\n⚠️ Encontrados menos de 2 faltantes. Puede haber duplicados mal contados.")
        } else {
            println("\n⚠️ Encontrados más de 2 faltantes. Revisando duplicados...")
        }
        
        println("\n" + "=".repeat(70))
        println("🧪 TESTS ESPECÍFICOS DE CASOS PROBLEMÁTICOS:")
        
        // Test casos específicos sospechosos
        val casosSospechosos = mapOf(
            "papel higiénico" to "3 rollos papel higiénico preguntar...cuál es mejor...",
            "bombilla" to "1.2metros cable 2.bombilla grande 3.zapallo chino",
            "rollos" to "cinco focos ocho rollos",
            "cepillos separados" to "4cepillos,12servilletas,3 desodorantes 7 calcetines",
            "leche asadas" to "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"
        )
        
        casosSospechosos.forEach { (nombre, caso) ->
            val resultado = QuantityParser.parse(caso)
            println("\n🔍 CASO: $nombre")
            println("   Texto: '$caso'")
            if (resultado.isNotEmpty()) {
                resultado.forEach { p ->
                    println("   ✅ Detectado: '${p.nombre}' (${p.cantidad ?: 1})")
                }
            } else {
                println("   ❌ NO DETECTADO - POSIBLE FALTANTE")
            }
        }
        
        // Guardar resultado
        val output = StringBuilder()
        output.appendLine("BÚSQUEDA DE LOS 2 PRODUCTOS FALTANTES")
        output.appendLine("="*50)
        output.appendLine("Total esperado: ${productosEsperados.size}")
        output.appendLine("Total detectado: ${productosDetectados.size}")
        output.appendLine("Faltantes: ${faltantes.size}")
        output.appendLine("")
        output.appendLine("PRODUCTOS NO DETECTADOS:")
        faltantes.forEach { output.appendLine(it) }
        
        val file = java.io.File("busqueda_2_productos_faltantes.txt")
        file.writeText(output.toString())
        println("\n💾 Resultado guardado en: busqueda_2_productos_faltantes.txt")
    }
}
