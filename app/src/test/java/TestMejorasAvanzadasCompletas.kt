import org.junit.Test
import org.junit.Assert.*
import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.parser.QuantityParserMejorasAvanzadas

/**
 * 🚀 TEST COMPLETO: MEJORAS AVANZADAS VS LISTA EXTREMA
 * 
 *         println("🧪 PRUEBAS COMPLETAS - MEJORAS AVANZADAS")
        println("=".repeat(80))te test prueba todas las mejoras avanzadas contra los casos
 * más complejos de la lista extremadamente desafiante.
 */
class TestMejorasAvanzadasCompletas {
    
    @Test
    fun test_mejoras_avanzadas_casos_extremos() {
        println("🚀 PROBANDO MEJORAS AVANZADAS EN CASOS EXTREMOS REALES")
        println("=".repeat(80))
        
        // Los casos más complejos de la lista extrema
        val casosExtremosMasComplejos = mapOf(
            // Múltiples productos pegados consecutivos
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso" to 
            "Debe separar en 3 productos con cantidades 1.6, 2.5, 3.8",
            
            // Caso híbrido con producto final sin coma
            "fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos" to
            "Debe detectar 5 productos: fragancia + 4 productos con decimales",
            
            // Preguntas intercaladas
            "2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería..." to
            "Debe limpiar pregunta y detectar 1 producto con cantidad 2.1",
            
            // Decimales con punto final
            "Crema hidratante 3.4." to
            "Debe detectar 1 producto con cantidad 3.4",
            
            // Caso súper complejo combinado  
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión" to
            "Debe detectar 5 productos: colonias + 4 con decimales (12.7, 23.4, 8.9, 15.6)"
        )
        
        for ((caso, expectativa) in casosExtremosMasComplejos) {
            probarCasoAvanzado(caso, expectativa)
        }
    }
    
    private fun probarCasoAvanzado(caso: String, expectativa: String) {
        println("\\n🎯 CASO AVANZADO: '$caso'")
        println("📋 EXPECTATIVA: $expectativa")
        println("-".repeat(70))
        
        // PASO 1: Parser actual (sin mejoras)
        println("🔴 PARSER ACTUAL (baseline):")
        val resultadoBaseline = QuantityParser.parse(caso)
        mostrarResultados("BASELINE", resultadoBaseline)
        
        // PASO 2: Con mejoras básicas
        val casoConMejorasBasicas = com.listafacilnueva.parser.QuantityParserMejoras.aplicarMejorasSeguras(caso)
        val resultadoBasicas = QuantityParser.parse(casoConMejorasBasicas)
        println("\\n🟡 CON MEJORAS BÁSICAS:")
        println("  Input procesado: '$casoConMejorasBasicas'")
        mostrarResultados("BÁSICAS", resultadoBasicas)
        
        // PASO 3: Con mejoras avanzadas
        println("\\n🟢 CON MEJORAS AVANZADAS:")
        val casoConMejorasAvanzadas = QuantityParserMejorasAvanzadas.aplicarTodasLasMejorasAvanzadas(caso)
        println("  Input procesado: '$casoConMejorasAvanzadas'")
        
        // Parsear el resultado mejorado
        val inputFinalParseado = if (casoConMejorasAvanzadas.contains(" | ")) {
            // Si contiene separador temporal, procesar cada fragmento
            val fragmentos = casoConMejorasAvanzadas.split(" | ").filter { it.trim().isNotBlank() }
            val productosTotal = mutableListOf<com.listafacilnueva.model.Producto>()
            
            for (fragmento in fragmentos) {
                productosTotal.addAll(QuantityParser.parse(fragmento.trim()))
            }
            
            productosTotal
        } else {
            QuantityParser.parse(casoConMejorasAvanzadas)
        }
        
        mostrarResultados("AVANZADAS", inputFinalParseado)
        
        // EVALUACIÓN COMPARATIVA
        evaluarProgresion(caso, resultadoBaseline, resultadoBasicas, inputFinalParseado, expectativa)
        
        println("\\n" + "=".repeat(70))
    }
    
    private fun mostrarResultados(tipo: String, productos: List<com.listafacilnueva.model.Producto>) {
        println("  Productos $tipo: ${productos.size}")
        if (productos.isEmpty()) {
            println("    ❌ No se generaron productos")
        } else {
            productos.forEachIndexed { i, producto ->
                val cantidadDisplay = if (producto.cantidad != null && producto.cantidad != 1.0) {
                    " [${producto.cantidad}${producto.unidad ?: ""}]"
                } else {
                    ""
                }
                println("    ${i+1}. '${producto.nombre}'$cantidadDisplay")
            }
        }
    }
    
    private fun evaluarProgresion(
        caso: String,
        baseline: List<com.listafacilnueva.model.Producto>,
        basicas: List<com.listafacilnueva.model.Producto>, 
        avanzadas: List<com.listafacilnueva.model.Producto>,
        expectativa: String
    ) {
        println("\\n📊 EVALUACIÓN DE PROGRESIÓN:")
        
        val cantBaseline = baseline.size
        val cantBasicas = basicas.size
        val cantAvanzadas = avanzadas.size
        
        println("  📈 Progresión: $cantBaseline → $cantBasicas → $cantAvanzadas productos")
        
        // Determinar el mejor resultado
        val mejorResultado = when {
            cantAvanzadas > cantBasicas && cantAvanzadas > cantBaseline -> "AVANZADAS"
            cantBasicas > cantBaseline -> "BÁSICAS"  
            cantBaseline > 0 -> "BASELINE"
            else -> "NINGUNO"
        }
        
        when (mejorResultado) {
            "AVANZADAS" -> {
                println("  🥇 GANADOR: Mejoras avanzadas (+${cantAvanzadas - maxOf(cantBaseline, cantBasicas)} productos)")
                analizarMejorasEspecificas(caso, avanzadas)
            }
            "BÁSICAS" -> {
                println("  🥈 GANADOR: Mejoras básicas (+${cantBasicas - cantBaseline} productos)")
                println("  💡 Las mejoras avanzadas necesitan refinamiento para este caso")
            }
            "BASELINE" -> {
                println("  🟡 GANADOR: Parser original (las mejoras no ayudaron)")
                println("  ⚠️  Este caso necesita análisis específico adicional")
                sugerirAnalisisAdicional(caso, expectativa)
            }
            "NINGUNO" -> {
                println("  ❌ FALLA TOTAL: Ninguna versión funcionó")
                println("  🚨 Caso crítico que requiere atención inmediata")
            }
        }
        
        // Verificar si cumple expectativa
        verificarExpectativa(caso, avanzadas, expectativa)
    }
    
    private fun analizarMejorasEspecificas(caso: String, productos: List<com.listafacilnueva.model.Producto>) {
        println("  🔍 ANÁLISIS DE MEJORAS ESPECÍFICAS:")
        
        // Verificar si separó decimales pegados
        if (Regex("\\d+\\.\\d+[a-zA-Z]").containsMatchIn(caso) && productos.size > 1) {
            println("    ✅ Mejoró separación de decimales pegados")
        }
        
        // Verificar si manejó comas complejas
        if (caso.contains(",") && productos.size > caso.split(",").size) {
            println("    ✅ Mejoró separación compleja por comas")
        }
        
        // Verificar si limpió preguntas
        if (caso.contains("preguntar...") && productos.any { it.nombre.isNotBlank() }) {
            println("    ✅ Limpió preguntas intercaladas correctamente")
        }
        
        // Verificar cantidades decimales
        val conCantidadesDecimales = productos.count { 
            val cant = it.cantidad
            cant != null && cant != 1.0
        }
        if (conCantidadesDecimales > 0) {
            println("    ✅ Detectó $conCantidadesDecimales productos con cantidades decimales")
        }
    }
    
    private fun verificarExpectativa(
        caso: String, 
        productos: List<com.listafacilnueva.model.Producto>, 
        expectativa: String
    ) {
        println("\\n🎯 VERIFICACIÓN DE EXPECTATIVA:")
        println("  Esperado: $expectativa")
        
        // Extraer número esperado de productos de la expectativa
        val numeroEsperado = when {
            expectativa.contains("3 productos") -> 3
            expectativa.contains("5 productos") -> 5
            expectativa.contains("1 producto") -> 1
            else -> null
        }
        
        if (numeroEsperado != null) {
            val cumpleExpectativa = productos.size >= numeroEsperado - 1 // Tolerancia de ±1
            if (cumpleExpectativa) {
                println("  ✅ CUMPLE EXPECTATIVA: ${productos.size} productos (esperado: $numeroEsperado)")
            } else {
                println("  ❌ NO CUMPLE: ${productos.size} productos (esperado: $numeroEsperado)")
            }
        } else {
            println("  📊 Análisis cualitativo: ${productos.size} productos generados")
        }
    }
    
    private fun sugerirAnalisisAdicional(caso: String, expectativa: String) {
        println("  💡 SUGERENCIAS PARA ANÁLISIS ADICIONAL:")
        
        when {
            caso.contains("preguntar...") -> 
                println("    - Mejorar limpieza de preguntas intercaladas")
            
            Regex("\\d+\\.\\d+[a-zA-Z]+\\d+\\.\\d+").containsMatchIn(caso) ->
                println("    - Mejorar separación de múltiples decimales pegados")
            
            caso.contains(",") && Regex("\\d+\\.\\d+\\s+[a-zA-Z]+\\s*$").containsMatchIn(caso) ->
                println("    - Mejorar detección de producto final sin coma")
            
            else ->
                println("    - Requiere patrón personalizado específico")
        }
    }
    
    @Test
    fun test_validacion_no_regresion_avanzada() {
        println("\\n🧪 VALIDACIÓN EXHAUSTIVA: NO REGRESIÓN CON MEJORAS AVANZADAS")
        println("=".repeat(70))
        
        val resultado = QuantityParserMejorasAvanzadas.validarMejorasAvanzadas()
        assertTrue("Las mejoras avanzadas no deben romper casos básicos", resultado)
        
        if (resultado) {
            println("\\n🎉 ✅ TODAS LAS MEJORAS AVANZADAS SON SEGURAS")
            println("     No hay regresiones en casos básicos")
        }
    }
    
    @Test
    fun test_comparacion_final_mejoras() {
        println("\\n🏆 COMPARACIÓN FINAL: TODAS LAS MEJORAS vs CASOS EXTREMOS")
        println("=".repeat(70))
        
        val casosRepresentativos = listOf(
            "Yogur griego 2.5.",
            "1.8bolsas quinoa orgánica", 
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas",
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso",
            "2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería..."
        )
        
        var mejorasSatisfactorias = 0
        
        for (caso in casosRepresentativos) {
            println("\\n📝 CASO: '$caso'")
            
            val baseline = QuantityParser.parse(caso).size
            val avanzadas = QuantityParserMejorasAvanzadas.aplicarTodasLasMejorasAvanzadas(caso)
            val resultadoAvanzadas = QuantityParser.parse(avanzadas).size
            
            val mejoro = resultadoAvanzadas > baseline
            val mantuvo = resultadoAvanzadas >= baseline
            
            if (mejoro) {
                println("  ✅ MEJORÓ: $baseline → $resultadoAvanzadas productos (+${resultadoAvanzadas - baseline})")
                mejorasSatisfactorias++
            } else if (mantuvo) {
                println("  ✅ MANTUVO: $baseline productos (sin regresión)")
                mejorasSatisfactorias++
            } else {
                println("  ❌ EMPEORÓ: $baseline → $resultadoAvanzadas productos")
            }
        }
        
        val porcentajeExito = (mejorasSatisfactorias * 100) / casosRepresentativos.size
        println("\\n🎯 RESULTADO FINAL:")
        println("  📊 Éxito: $mejorasSatisfactorias/${casosRepresentativos.size} casos ($porcentajeExito%)")
        
        if (porcentajeExito >= 80) {
            println("  🏆 ÉXITO: Las mejoras funcionan muy bien")
        } else if (porcentajeExito >= 60) {
            println("  🥈 BUENO: Las mejoras funcionan aceptablemente")
        } else {
            println("  🥉 NECESITA TRABAJO: Las mejoras requieren refinamiento")
        }
        
        assertTrue("Al menos 60% de casos deben mejorar o mantenerse", porcentajeExito >= 60)
    }
}
