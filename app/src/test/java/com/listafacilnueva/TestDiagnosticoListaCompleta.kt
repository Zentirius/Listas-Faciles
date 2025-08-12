package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser
import org.junit.Test

class TestDiagnosticoListaCompleta {
    @Test
    fun testDiagnosticoCompleto() {
        println("🔍 DIAGNÓSTICO DE DETECCIÓN - LISTA COMPLETA")
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
    @Test
    fun testDiagnosticoCompleto() {
        val output = StringBuilder()
        output.appendLine("🔍 DIAGNÓSTICO DE DETECCIÓN - LISTA COMPLETA")
        output.appendLine("=".repeat(70))
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
        val productosEsperados = listOf(
            "pastelera", "leche sin lactosa", "mantequilla", "lechugas", "carne molida",
            "tallarines", "tomates", "huevo", "leche asadas", "sandias", "zanaorias", "zapatos",
            "lechuga", "pan", "huevos", "leche", "plátanos", "manzanas", "espaguetis", 
            "zanahorias", "jugo de naranja", "atún", "papa", "sandia", "zapato", "salmon",
            "crema dental", "shampoo", "jabón líquido", "papel higiénico", "detergente",
            "esponjas", "arroz", "aceite", "champú", "cepillos", "servilletas", 
            "desodorantes", "calcetines", "azúcar", "sal", "vinagre", "pilas", "cable",
            "velas", "focos", "alcohol", "algodón", "papel aluminio", "cinta adhesiva",
            "pegamento", "bombilla", "zapallo", "madera", "desinfectante", "rollos"
        )
        val productos = QuantityParser.parse(listaCompleta)
        output.appendLine("📊 TOTAL DETECTADO: ${productos.size}")
        output.appendLine("\n📝 LISTA DE PRODUCTOS DETECTADOS:")
        productos.forEachIndexed { i, p ->
            val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
            val unidad = p.unidad?.let { " $it" } ?: ""
            output.appendLine("${i+1}. ${p.nombre} (${cantidad}${unidad})")
        }
        output.appendLine("\n" + "=".repeat(70))
        output.appendLine("🔎 PRODUCTOS ESPERADOS NO DETECTADOS:")
        val noDetectados = productosEsperados.filter { esperado ->
            !productos.any { it.nombre.contains(esperado, ignoreCase = true) }
        }
        if (noDetectados.isNotEmpty()) {
            noDetectados.forEach { output.appendLine("❌ $it") }
        } else {
            output.appendLine("✅ Todos los productos esperados fueron detectados")
        }
        output.appendLine("\n" + "=".repeat(70))
        output.appendLine("🔎 PRODUCTOS DETECTADOS QUE NO ESTÁN EN LA LISTA ESPERADA:")
        val detectadosExtra = productos.filter { p ->
            !productosEsperados.any { esperado -> p.nombre.contains(esperado, ignoreCase = true) }
        }
        if (detectadosExtra.isNotEmpty()) {
            detectadosExtra.forEach { output.appendLine("⚠️ ${it.nombre}") }
        } else {
            output.appendLine("✅ No hay productos extra detectados")
        }
        // Guardar el resultado en archivo
        val file = java.io.File("test_output_diagnostico.txt")
        file.writeText(output.toString())
        // Mostrar en consola también
        println(output.toString())
    }
            "desodorantes", "calcetines", "azúcar", "sal", "vinagre", "pilas", "cable",
            "velas", "focos", "alcohol", "algodón", "papel aluminio", "cinta adhesiva",
            "pegamento", "bombilla", "zapallo", "madera", "desinfectante", "rollos"
        )

        val productos = QuantityParser.parse(listaCompleta)
        println("📊 TOTAL DETECTADO: ${productos.size}")
        println("\n📝 LISTA DE PRODUCTOS DETECTADOS:")
        productos.forEachIndexed { i, p ->
            val cantidad = p.cantidad?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "1"
            val unidad = p.unidad?.let { " $it" } ?: ""
            println("${i+1}. ${p.nombre} (${cantidad}${unidad})")
        }
        println("\n" + "=".repeat(70))
        println("🔎 PRODUCTOS ESPERADOS NO DETECTADOS:")
        val noDetectados = productosEsperados.filter { esperado ->
            !productos.any { it.nombre.contains(esperado, ignoreCase = true) }
        }
        if (noDetectados.isNotEmpty()) {
            noDetectados.forEach { println("❌ $it") }
        } else {
            println("✅ Todos los productos esperados fueron detectados")
        }
        println("\n" + "=".repeat(70))
        println("🔎 PRODUCTOS DETECTADOS QUE NO ESTÁN EN LA LISTA ESPERADA:")
        val detectadosExtra = productos.filter { p ->
            !productosEsperados.any { esperado -> p.nombre.contains(esperado, ignoreCase = true) }
        }
        if (detectadosExtra.isNotEmpty()) {
            detectadosExtra.forEach { println("⚠️ ${it.nombre}") }
        } else {
            println("✅ No hay productos extra detectados")
        }
    }
}
