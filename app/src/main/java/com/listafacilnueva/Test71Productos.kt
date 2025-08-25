package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser

/**
 * 🧪 TEST ESPECÍFICO - VERIFICACIÓN DE 71 PRODUCTOS (LISTA ACTUALIZADA)
 * 
 * Este test verifica que el parser detecte exactamente los 71 productos
 * de la lista actualizada del usuario.
 */
fun main() {
    println("🧪 TEST ESPECÍFICO - VERIFICACIÓN DE 71 PRODUCTOS (LISTA ACTUALIZADA)")
    println("=".repeat(80))
    println("🎯 OBJETIVO: Verificar detección exacta de 71 productos")
    println("📋 PRODUCTOS ESPERADOS: 71")
    println("=".repeat(80))
    
    // 🚀 ACTIVAR DEBUG para ver qué está pasando
    QuantityParser.setDebugMode(true)
    
    // 📝 LISTA ACTUALIZADA DEL USUARIO (71 productos)
    val listaActualizada = """pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
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
59
1.2metros cable 2.bombilla grande 3.zapallo chino

1.2metros de madera 3.8 metros de cable 3. zapallito 
2.desodorantes
3.desinfectante

siete velas
diez pilas

cinco focos ocho rollos"""

    // 📋 LISTA DE LOS 71 PRODUCTOS ESPERADOS
    val productosEsperados = listOf(
        "pastelera con albaca",
        "Leche sin lactosa 2",
        "Mantequilla 1 barata",
        "2 lechugas francesas",
        "Carne molida más barata en bandeja",
        "Tallarines 4 en oferta",
        "1malla tomates",
        "1 huevo marca yemita",
        "leche asadas",
        "6sandias",
        "8tomates",
        "6 zanaorias",
        "5 zapatos",
        "2 kg de tomates",
        "1 lechuga",
        "pan",
        "6 huevos",
        "1 litro de leche no muy cara",
        "media docena de plátanos",
        "3 manzanas",
        "500gr de carne molida",
        "1 paquete de espaguetis",
        "Zanahorias",
        "1.5 litros de jugo de naranja",
        "2 latas de atún",
        "1.1papa mediana",
        "2.sandia grande",
        "3.zapato grande",
        "1.salmon 1kg",
        "2.zanaorias",
        "cinco tomates",
        "ocho zanahorias",
        "seis tomates",
        "ocho papas",
        "crema dental con fluor marca colgate",
        "Shampoo anticaspa 1",
        "Jabón líquido",
        "3 rollos papel higiénico",
        "Detergente en polvo más barato en caja",
        "Esponjas 5 en oferta",
        "1bolsa arroz",
        "1 aceite marca chef",
        "champú suave",
        "4cepillos",
        "12servilletas",
        "3 desodorantes",
        "7 calcetines",
        "3 kg de azúcar",
        "1 sal",
        "vinagre",
        "8 pilas",
        "2 metros de cable no muy caro",
        "media docena de velas",
        "4 focos",
        "750ml de alcohol",
        "1 paquete de algodón",
        "Papel aluminio",
        "2.5 metros de cinta adhesiva",
        "3 tubos de pegamento",
        "1.2metros cable",
        "2.bombilla grande",
        "3.zapallo chino",
        "1.2metros de madera",
        "3.8 metros de cable",
        "3. zapallito",
        "2.desodorantes",
        "3.desinfectante",
        "siete velas",
        "diez pilas",
        "cinco focos",
        "ocho rollos"
    )

    println("📝 PROCESANDO LISTA ACTUALIZADA...")
    println("📏 LONGITUD DEL TEXTO: ${listaActualizada.length} caracteres")
    println("📋 PRODUCTOS ESPERADOS: ${productosEsperados.size}")
    println()
    
    try {
        val resultado = QuantityParser.parse(listaActualizada)
        
        println()
        println("🎯 RESULTADOS DEL TEST:")
        println("=".repeat(80))
        println("✅ PRODUCTOS DETECTADOS: ${resultado.size}")
        println("📋 PRODUCTOS ESPERADOS: ${productosEsperados.size}")
        println("📊 DIFERENCIA: ${resultado.size - productosEsperados.size}")
        println()
        
        // 📊 ANÁLISIS DETALLADO
        println("📊 ANÁLISIS DETALLADO:")
        println("-".repeat(80))
        
        val productosDetectados = resultado.map { it.nombre.trim().lowercase() }
        val productosEsperadosLower = productosEsperados.map { it.trim().lowercase() }
        
        // Productos detectados correctamente
        val detectadosCorrectamente = productosEsperadosLower.filter { esperado ->
            productosDetectados.any { detectado ->
                detectado.contains(esperado) || esperado.contains(detectado)
            }
        }
        
        // Productos no detectados
        val noDetectados = productosEsperadosLower.filter { esperado ->
            !productosDetectados.any { detectado ->
                detectado.contains(esperado) || esperado.contains(detectado)
            }
        }
        
        // Productos extra (falsos positivos)
        val productosExtra = productosDetectados.filter { detectado ->
            !productosEsperadosLower.any { esperado ->
                detectado.contains(esperado) || esperado.contains(detectado)
            }
        }
        
        println("✅ DETECTADOS CORRECTAMENTE: ${detectadosCorrectamente.size}")
        println("❌ NO DETECTADOS: ${noDetectados.size}")
        println("⚠️ PRODUCTOS EXTRA: ${productosExtra.size}")
        println()
        
        // Mostrar productos detectados
        println("📋 LISTA DE PRODUCTOS DETECTADOS:")
        println("-".repeat(80))
        
        for ((index, producto) in resultado.withIndex()) {
            val cantidadStr = producto.cantidad?.let { "cantidad: $it" } ?: "sin cantidad"
            val unidadStr = producto.unidad?.let { " (${producto.unidad})" } ?: ""
            val marcasStr = if (producto.marcas.isNotEmpty()) " [marcas: ${producto.marcas.joinToString(", ")}]" else ""
            
            println("${String.format("%3d", index + 1)}. '${producto.nombre}' ($cantidadStr$unidadStr)$marcasStr")
        }
        
        // Mostrar productos no detectados
        if (noDetectados.isNotEmpty()) {
            println("\n❌ PRODUCTOS NO DETECTADOS:")
            println("-".repeat(60))
            noDetectados.forEachIndexed { index, producto ->
                println("${index + 1}. '$producto'")
            }
        }
        
        // Mostrar productos extra
        if (productosExtra.isNotEmpty()) {
            println("\n⚠️ PRODUCTOS EXTRA (FALSOS POSITIVOS):")
            println("-".repeat(60))
            productosExtra.forEachIndexed { index, producto ->
                println("${index + 1}. '$producto'")
            }
        }
        
        // 📈 ESTADÍSTICAS FINALES
        println("\n📈 ESTADÍSTICAS FINALES:")
        println("=".repeat(80))
        
        val porcentajeExito = (detectadosCorrectamente.size.toDouble() / productosEsperados.size) * 100
        val precision = if (resultado.size > 0) {
            (detectadosCorrectamente.size.toDouble() / resultado.size) * 100
        } else 0.0
        
        println("📊 MÉTRICAS:")
        println("   • Productos esperados: ${productosEsperados.size}")
        println("   • Productos detectados: ${resultado.size}")
        println("   • Detectados correctamente: ${detectadosCorrectamente.size}")
        println("   • No detectados: ${noDetectados.size}")
        println("   • Productos extra: ${productosExtra.size}")
        println("   • Porcentaje de éxito: ${String.format("%.1f", porcentajeExito)}%")
        println("   • Precisión: ${String.format("%.1f", precision)}%")
        
        // 🏆 EVALUACIÓN FINAL
        println("\n🏆 EVALUACIÓN FINAL:")
        when {
            porcentajeExito >= 95 -> {
                println("   🥇 ¡EXCELENTE! (${String.format("%.1f", porcentajeExito)}%)")
                println("   ✅ Parser funciona a nivel profesional")
            }
            porcentajeExito >= 90 -> {
                println("   🥈 ¡MUY BUENO! (${String.format("%.1f", porcentajeExito)}%)")
                println("   🔧 Pequeños ajustes podrían mejorar aún más")
            }
            porcentajeExito >= 80 -> {
                println("   🥉 ¡BUENO! (${String.format("%.1f", porcentajeExito)}%)")
                println("   🔧 Necesita optimización en casos específicos")
            }
            porcentajeExito >= 70 -> {
                println("   ⚠️ ACEPTABLE (${String.format("%.1f", porcentajeExito)}%)")
                println("   🔧 Necesita mejoras significativas")
            }
            else -> {
                println("   ❌ NECESITA MEJORAS (${String.format("%.1f", porcentajeExito)}%)")
                println("   🔧 Revisar implementación completa")
            }
        }
        
        // 🎯 RECOMENDACIONES
        if (noDetectados.isNotEmpty() || productosExtra.isNotEmpty()) {
            println("\n🎯 RECOMENDACIONES:")
            println("=".repeat(80))
            
            if (noDetectados.isNotEmpty()) {
                println("🔧 PARA PRODUCTOS NO DETECTADOS:")
                println("   • Revisar patrones regex para casos específicos")
                println("   • Ajustar filtros de validación")
                println("   • Mejorar detección de productos complejos")
            }
            
            if (productosExtra.isNotEmpty()) {
                println("🔧 PARA PRODUCTOS EXTRA:")
                println("   • Ajustar filtros de palabras comunes")
                println("   • Mejorar validación de productos")
                println("   • Refinar patrones de detección")
            }
        }
        
    } catch (e: Exception) {
        println("💥 ERROR EN EL TEST:")
        println(e.message)
        e.printStackTrace()
    }
    
    println("\n🎉 TEST DE 71 PRODUCTOS COMPLETADO")
    println("=".repeat(80))
}
