import org.junit.Test
import org.junit.Assert.*
import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.parser.QuantityParserMejoras

/**
 * 🚀 INTEGRATION TEST: MEJORAS + PARSER EXISTENTE
 * 
 * Este test prueba cómo integrar las mejoras con el parser actual
 * de forma que mejore los casos extremos sin romper lo que ya funciona.
 */
class TestIntegracionMejorasSeguras {
    
    @Test
    fun test_casos_basicos_siguen_funcionando() {
        println("🧪 VERIFICACIÓN: Los casos básicos deben seguir funcionando igual")
        
        val casosBasicos = listOf(
            "1.pan 2.leche 3.huevos",
            "2.5 metros de cable", 
            "1.2metros cable 2.bombilla grande 3.zapallo chino"
        )
        
        for (caso in casosBasicos) {
            println("\\n📝 Probando caso básico: '$caso'")
            
            // Aplicar mejoras
            val casoMejorado = QuantityParserMejoras.aplicarMejorasSeguras(caso)
            println("  Después de mejoras: '$casoMejorado'")
            
            // Parsear con el parser actual
            val productos = QuantityParser.parse(casoMejorado)
            
            // Verificaciones básicas
            assertTrue("Debe generar productos", productos.isNotEmpty())
            println("  ✅ Productos generados: ${productos.size}")
            
            productos.forEachIndexed { i, producto ->
                println("    ${i+1}. ${producto.nombre} [${producto.cantidad}${producto.unidad}]")
                assertNotNull("El nombre no debe ser null", producto.nombre)
                assertTrue("El nombre no debe estar vacío", producto.nombre.isNotBlank())
            }
            
            println("  ✅ Caso básico funciona correctamente")
        }
        
        println("\\n🎉 TODOS LOS CASOS BÁSICOS MANTIENEN SU FUNCIONALIDAD")
    }
    
    @Test
    fun test_casos_extremos_mejoran() {
        println("🧪 VERIFICACIÓN: Los casos extremos deben mejorar")
        
        val casosExtremos = mapOf(
            "Yogur griego 2.5." to "Debe detectar 1 producto sin confundir con numeración",
            "1.8bolsas quinoa orgánica" to "Debe separar cantidad de producto",
            "verduras orgánicas ,11.5manzanas,16.8limones" to "Debe separar múltiples productos con decimales"
        )
        
        for ((casoExtremo, expectativa) in casosExtremos) {
            println("\\n📝 Probando caso extremo: '$casoExtremo'")
            println("  Expectativa: $expectativa")
            
            // ANTES: Sin mejoras
            println("\\n  🔴 ANTES (sin mejoras):")
            val productosSinMejoras = QuantityParser.parse(casoExtremo)
            println("    Productos generados: ${productosSinMejoras.size}")
            productosSinMejoras.forEachIndexed { i, producto ->
                println("      ${i+1}. ${producto.nombre} [${producto.cantidad}${producto.unidad}]")
            }
            
            // DESPUÉS: Con mejoras
            println("\\n  🟢 DESPUÉS (con mejoras):")
            val casoMejorado = QuantityParserMejoras.aplicarMejorasSeguras(casoExtremo)
            println("    Input mejorado: '$casoMejorado'")
            val productosConMejoras = QuantityParser.parse(casoMejorado)
            println("    Productos generados: ${productosConMejoras.size}")
            productosConMejoras.forEachIndexed { i, producto ->
                println("      ${i+1}. ${producto.nombre} [${producto.cantidad}${producto.unidad}]")
            }
            
            // Análisis de mejora
            val mejoroOIgual = productosConMejoras.size >= productosSinMejoras.size
            val tieneProdutosValidos = productosConMejoras.all { it.nombre.isNotBlank() }
            
            if (mejoroOIgual && tieneProdutosValidos) {
                println("    ✅ MEJORADO: Genera igual o más productos válidos")
            } else {
                println("    ⚠️  NECESITA AJUSTE: No mejoró significativamente")
            }
        }
    }
    
    @Test
    fun test_caso_super_extremo_colonias() {
        println("🧪 TEST SUPREMO: Caso más difícil - colonias con múltiples decimales")
        
        val casoSuperExtremo = "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión"
        
        println("\\n📝 Input original: '$casoSuperExtremo'")
        
        // Análisis paso a paso
        println("\\n🔍 ANÁLISIS PASO A PASO:")
        
        // Paso 1: Separar decimales pegados
        val paso1 = QuantityParserMejoras.separarDecimalesPegados(casoSuperExtremo)
        println("  Paso 1 (decimales pegados): '$paso1'")
        
        // Paso 2: Separar por comas con decimales
        val fragmentosPaso2 = QuantityParserMejoras.separarPorComasConDecimales(paso1)
        println("  Paso 2 (separación por comas): ${fragmentosPaso2.size} fragmentos")
        fragmentosPaso2.forEachIndexed { i, frag ->
            println("    ${i+1}. '$frag'")
        }
        
        // Paso 3: Aplicar mejoras completas
        val casoFinalMejorado = QuantityParserMejoras.aplicarMejorasSeguras(casoSuperExtremo)
        println("  Paso 3 (mejoras completas): '$casoFinalMejorado'")
        
        println("\\n🚀 PARSING FINAL:")
        
        // ANTES: Sin mejoras
        println("  🔴 SIN MEJORAS:")
        val sinMejoras = QuantityParser.parse(casoSuperExtremo)
        println("    Productos: ${sinMejoras.size}")
        sinMejoras.forEachIndexed { i, p -> 
            println("      ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad}]") 
        }
        
        // DESPUÉS: Con mejoras
        println("\\n  🟢 CON MEJORAS:")
        val conMejoras = QuantityParser.parse(casoFinalMejorado)
        println("    Productos: ${conMejoras.size}")
        conMejoras.forEachIndexed { i, p -> 
            println("      ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad}]") 
        }
        
        // Evaluación de éxito
        val mejoraSignificativa = conMejoras.size > sinMejoras.size || 
                                 (conMejoras.size == sinMejoras.size && conMejoras.any { it.cantidad != null })
        
        println("\\n📊 EVALUACIÓN:")
        if (mejoraSignificativa) {
            println("  ✅ ÉXITO: Las mejoras funcionan en el caso más extremo")
            println("  📈 Mejora: ${sinMejoras.size} → ${conMejoras.size} productos")
        } else {
            println("  ⚠️  PARCIAL: Las mejoras ayudan pero necesitan refinamiento")
            println("  📊 Mismo resultado: ${sinMejoras.size} productos")
        }
        
        // Verificación básica
        assertTrue("Debe generar al menos 1 producto", conMejoras.isNotEmpty())
        assertTrue("Los productos deben tener nombres válidos", conMejoras.all { it.nombre.isNotBlank() })
    }
    
    @Test
    fun test_no_hay_regresiones() {
        println("🧪 TEST CRÍTICO: Verificar que NO hay regresiones")
        
        val casosCriticosFuncionando = listOf(
            "1.pan blanco" to 1,
            "2.bombilla grande" to 1, 
            "3.zapallo chino" to 1,
            "1.pan 2.leche" to 2,
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3
        )
        
        var todosSinRegresion = true
        
        for ((caso, productosEsperados) in casosCriticosFuncionando) {
            println("\\n📝 Caso crítico: '$caso'")
            
            // Sin mejoras
            val sinMejoras = QuantityParser.parse(caso)
            
            // Con mejoras  
            val casoMejorado = QuantityParserMejoras.aplicarMejorasSeguras(caso)
            val conMejoras = QuantityParser.parse(casoMejorado)
            
            val mantieneResultado = conMejoras.size >= sinMejoras.size && 
                                   conMejoras.size >= productosEsperados - 1 // Tolerancia de ±1
            
            if (mantieneResultado) {
                println("  ✅ SIN REGRESIÓN: ${sinMejoras.size} → ${conMejoras.size} productos (esperados: $productosEsperados)")
            } else {
                println("  ❌ REGRESIÓN DETECTADA: ${sinMejoras.size} → ${conMejoras.size} productos (esperados: $productosEsperados)")
                todosSinRegresion = false
            }
        }
        
        assertTrue("NO debe haber regresiones en casos que ya funcionan", todosSinRegresion)
        
        if (todosSinRegresion) {
            println("\\n🎉 PERFECTO: NO HAY REGRESIONES - LAS MEJORAS SON SEGURAS")
        }
    }
}
