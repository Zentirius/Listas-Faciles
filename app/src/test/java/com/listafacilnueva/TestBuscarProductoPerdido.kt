package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test

class TestBuscarProductoPerdido {

    @Test
    fun identificarProductoPerdido() {
        println("🔍 IDENTIFICAR PRODUCTO PERDIDO - ANÁLISIS EXHAUSTIVO")
        println("Usuario reporta: antes 69, ahora 68 productos (-1)")
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

1.2metros cable 2.bombilla grande 3.zapallo chino

1.2metros de madera 3.8 metros de cable 
2.desodorantes
3.desinfectante

siete velas
diez pilas

cinco focos ocho rollos
        """.trimIndent()
        
        try {
            val productos = QuantityParser.parse(listaCompleta)
            
            println("📊 RESULTADOS ACTUALES:")
            println("   Total detectado: ${productos.size} productos")
            println("   Diferencia con reporte anterior: ${68 - productos.size}")
            println()
            
            println("📝 LISTA COMPLETA DE PRODUCTOS DETECTADOS:")
            println("-".repeat(50))
            
            productos.forEachIndexed { i, p ->
                val cantidad = p.cantidad?.let { 
                    if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
                } ?: "1"
                val unidad = p.unidad?.let { " $it" } ?: ""
                println("${String.format("%2d", i+1)}. ${p.nombre} (${cantidad}${unidad})")
                
                // Mostrar original para casos complejos
                if (p.original != null && p.original != p.nombre) {
                    println("     Original: '${p.original}'")
                }
            }
            
            println("\n" + "=".repeat(70))
            println("🔍 ANÁLISIS DE CASOS CRÍTICOS:")
            
            // Verificar casos específicos que pueden fallar
            val casosCriticos = mapOf(
                "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos" to listOf("leche", "sandias", "tomates", "zanaorias", "zapatos"),
                "champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines" to listOf("champú", "cepillos", "servilletas", "desodorantes", "calcetines"),
                "1.1papa mediana2.sandia grande" to listOf("papa", "sandia"),
                "1.2metros cable 2.bombilla grande 3.zapallo chino" to listOf("metros", "cable", "bombilla", "zapallo"),
                "cinco focos ocho rollos" to listOf("focos", "rollos")
            )
            
            casosCriticos.forEach { (caso, palabrasEsperadas) ->
                val detectados = palabrasEsperadas.filter { palabra ->
                    productos.any { it.nombre.contains(palabra, ignoreCase = true) }
                }
                val faltantes = palabrasEsperadas - detectados.toSet()
                
                println("\n📋 CASO: ${caso.take(40)}...")
                println("   ✅ Detectados: ${detectados.joinToString(", ")}")
                if (faltantes.isNotEmpty()) {
                    println("   ❌ FALTANTES: ${faltantes.joinToString(", ")}")
                }
                println("   📊 Ratio: ${detectados.size}/${palabrasEsperadas.size}")
            }
            
            println("\n" + "=".repeat(70))
            println("🎯 PRODUCTOS POTENCIALMENTE PERDIDOS:")
            
            // Lista de productos esperados manualmente
            val productosEsperados = listOf(
                "pastelera", "leche sin lactosa", "mantequilla", "lechugas", "carne molida",
                "tallarines", "tomates", "huevo", "leche asadas", "sandias", "zanaorias", "zapatos",
                "lechuga", "pan", "huevos", "leche", "plátanos", "manzanas", "espaguetis", 
                "zanahorias", "jugo de naranja", "atún", "papa", "sandia", "salmon",
                "crema dental", "shampoo", "jabón líquido", "papel higiénico", "detergente",
                "esponjas", "arroz", "aceite", "champú", "cepillos", "servilletas", 
                "desodorantes", "calcetines", "azúcar", "sal", "vinagre", "pilas", "cable",
                "velas", "focos", "alcohol", "algodón", "papel aluminio", "cinta adhesiva",
                "pegamento", "bombilla", "zapallo", "madera", "desinfectante", "rollos"
            )
            
            val noDetectados = productosEsperados.filter { esperado ->
                !productos.any { it.nombre.contains(esperado, ignoreCase = true) }
            }
            
            if (noDetectados.isNotEmpty()) {
                println("❌ PRODUCTOS ESPERADOS NO DETECTADOS:")
                noDetectados.forEach { println("   • $it") }
            } else {
                println("✅ Todos los productos esperados fueron detectados")
            }
            
            println("\n🎯 DIAGNÓSTICO:")
            when {
                productos.size == 68 -> println("✅ Detección actual coincide con reporte (68 productos)")
                productos.size == 69 -> println("📈 Detección mejoró, ahora detecta 69 productos")
                productos.size < 68 -> println("❌ Detección empeoró, ahora detecta ${productos.size} productos")
                productos.size > 69 -> println("📈 Detección mejoró significativamente: ${productos.size} productos")
            }
            
        } catch (e: Exception) {
            println("💥 ERROR: ${e.message}")
            e.printStackTrace()
        }
    }
}
