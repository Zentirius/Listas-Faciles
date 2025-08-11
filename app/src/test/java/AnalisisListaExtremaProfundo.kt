import org.junit.Test
import org.junit.Assert.*

/**
 * 🔬 ANÁLISIS DETALLADO DE FALLAS EN LISTA EXTREMA
 * 
 * Este test identifica exactamente por qué fallan los casos extremos
 * sin modificar el código que ya funciona correctamente.
 */
class AnalisisListaExtremaProfundo {
    
    @Test
    fun diagnosticar_fallas_especificas() {
        println("🎯 DIAGNÓSTICO PROFUNDO DE FALLAS EN LISTA EXTREMA")
        println("=".repeat(70))
        
        // Los casos más problemáticos identificados
        val casosProblematicos = mapOf(
            "Decimales con punto final" to "Yogur griego 2.5.",
            "Múltiples decimales pegados" to "verduras orgánicas ,11.5manzanas,16.8limones,7.3 cebollas rojas 12.1 peras maduras",
            "Productos técnicos complejos" to "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable",
            "Decimales sin espacios" to "1.8bolsas quinoa orgánica",
            "Caso híbrido complejo" to "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión"
        )
        
        casosProblematicos.forEach { (categoria, input) ->
            analizarCasoEspecifico(categoria, input)
        }
        
        proponer_soluciones_seguras()
    }
    
    private fun analizarCasoEspecifico(categoria: String, input: String) {
        println("\n🔍 CATEGORÍA: $categoria")
        println("INPUT: '$input'")
        println("-".repeat(50))
        
        // Análisis de patrones actuales
        analizarPatronesActuales(input)
        
        // Detección de problemas específicos
        detectarProblemasEspecificos(input, categoria)
        
        // Propuesta de mejora para este caso
        proponerMejoraPorCategoria(categoria)
        
        println()
    }
    
    private fun analizarPatronesActuales(input: String) {
        println("📊 ANÁLISIS DE PATRONES ACTUALES:")
        
        // Patrón de numeración actual
        val patronNumeracion = Regex("^\\d+\\.\\s")
        val pareceNumeracion = patronNumeracion.find(input) != null
        println("  ¿Detectado como numeración?: $pareceNumeracion")
        
        // Decimales encontrados
        val decimales = Regex("\\d+\\.\\d+").findAll(input).map { it.value }.toList()
        println("  Decimales detectados: ${decimales.size} → $decimales")
        
        // Separación por comas
        val fragmentosPorComa = input.split(",").filter { it.trim().isNotBlank() }
        println("  Fragmentos por coma: ${fragmentosPorComa.size}")
        fragmentosPorComa.forEachIndexed { i, fragmento ->
            println("    ${i+1}. '${fragmento.trim()}'")
        }
        
        // Detección de productos pegados
        val productosPegados = Regex("\\d+\\.\\d+[a-zA-ZáéíóúÁÉÍÓÚñÑ]+").findAll(input).map { it.value }.toList()
        println("  Productos pegados: ${productosPegados.size} → $productosPegados")
        
        // Espacios después de números
        val numerosConEspacio = Regex("\\d+\\.\\d+\\s+[a-zA-Z]").findAll(input).map { it.value }.toList()
        println("  Números con espacio: ${numerosConEspacio.size} → $numerosConEspacio")
    }
    
    private fun detectarProblemasEspecificos(input: String, categoria: String) {
        println("⚠️  PROBLEMAS ESPECÍFICOS DETECTADOS:")
        
        when (categoria) {
            "Decimales con punto final" -> {
                val puntoFinal = input.endsWith(".")
                val esDecimalConPunto = Regex("\\d+\\.\\d+\\.$").find(input) != null
                println("  ❌ Termina en punto: $puntoFinal")
                println("  ❌ Es decimal + punto: $esDecimalConPunto")
                println("  💥 PROBLEMA: Se confunde con numeración de lista")
            }
            
            "Múltiples decimales pegados" -> {
                val sinSeparadores = !input.contains(" ") && input.contains(",")
                val decimalesPegados = Regex("\\d+\\.\\d+[a-zA-Z]").findAll(input).count()
                println("  ❌ Decimales pegados a palabras: $decimalesPegados")
                println("  ❌ Separación incompleta por comas: $sinSeparadores")
                println("  💥 PROBLEMA: No separa correctamente múltiples productos")
            }
            
            "Productos técnicos complejos" -> {
                val numerosConsecutivos = extraerNumerosEnteros(input)
                val palabrasTecnicas = listOf("metros", "martillo", "clavos", "acero", "náutica")
                val esTecnico = palabrasTecnicas.any { input.contains(it, ignoreCase = true) }
                println("  ❌ Números parecen consecutivos: $numerosConsecutivos")
                println("  ✅ Contexto técnico: $esTecnico")
                println("  💥 PROBLEMA: Se interpreta como numeración cuando son cantidades reales")
            }
            
            "Decimales sin espacios" -> {
                val pegadoSinEspacio = Regex("\\d+\\.\\d+[a-zA-Z]").find(input) != null
                println("  ❌ Decimal pegado sin espacio: $pegadoSinEspacio")
                println("  💥 PROBLEMA: No se separa la cantidad del producto")
            }
            
            "Caso híbrido complejo" -> {
                val tieneTodoLosProblemas = listOf(
                    input.contains(","),
                    Regex("\\d+\\.\\d+[a-zA-Z]").find(input) != null,
                    Regex("\\d+\\.\\d+\\s+[a-zA-Z]").find(input) != null,
                    input.split(",").size > 3
                ).all { it }
                println("  ❌ Combina todos los problemas: $tieneTodoLosProblemas")
                println("  💥 PROBLEMA: Caso más difícil - múltiples patrones juntos")
            }
        }
    }
    
    private fun extraerNumerosEnteros(input: String): List<Int> {
        return Regex("(\\d+)\\.\\d+").findAll(input)
            .map { it.groupValues[1].toInt() }
            .toList()
    }
    
    private fun proponerMejoraPorCategoria(categoria: String) {
        println("💡 PROPUESTA DE MEJORA ESPECÍFICA:")
        
        when (categoria) {
            "Decimales con punto final" -> {
                println("  🔧 Mejorar regex: distinguir 'cantidad.' vs '1. numeración'")
                println("  📝 Código: if (input.matches(Regex(\".*\\\\d+\\\\.\\\\d+\\\\.$\"))) // es cantidad, no numeración")
            }
            
            "Múltiples decimales pegados" -> {
                println("  🔧 Pre-procesar: separar decimales pegados antes de análisis principal")
                println("  📝 Código: input.replace(Regex(\"(\\\\d+\\\\.\\\\d+)([a-zA-Z])\"), \"$1 $2\")")
            }
            
            "Productos técnicos complejos" -> {
                println("  🔧 Detectar contexto técnico para evitar falsa numeración")
                println("  📝 Código: if (tieneUnidadesTecnicas(input)) // son cantidades, no numeración")
            }
            
            "Decimales sin espacios" -> {
                println("  🔧 Separación automática de decimales pegados")
                println("  📝 Código: Regex pattern para separar cantidad+producto")
            }
            
            "Caso híbrido complejo" -> {
                println("  🔧 Algoritmo híbrido: combinar todas las mejoras anteriores")
                println("  📝 Código: Aplicar pre-procesamiento + contexto + separación mejorada")
            }
        }
    }
    
    private fun proponer_soluciones_seguras() {
        println("\n" + "=".repeat(70))
        println("🚀 PLAN DE MEJORAS SEGURAS - NO ROMPER FUNCIONALIDAD EXISTENTE")
        println("=".repeat(70))
        
        println("\n🔒 REGLAS DE SEGURIDAD:")
        println("1. ✅ Mantener casos básicos: '1.pan 2.leche 3.huevos' → 3 productos")
        println("2. ✅ Mantener decimales simples: '2.5 metros' → cantidad 2.5")
        println("3. ✅ NO modificar funciones que ya funcionan correctamente")
        println("4. ✅ Añadir SOLO mejoras incrementales y probadas")
        
        println("\n📋 IMPLEMENTACIÓN POR FASES:")
        
        println("\n🎯 FASE 1: Mejora de decimales con punto final")
        println("   - Detectar patrón 'cantidad.cantidad.' vs '1. numeración'")
        println("   - Modificar SOLO la detección, no la lógica principal")
        println("   - Probar con casos existentes + nuevos casos")
        
        println("\n🎯 FASE 2: Pre-procesamiento de decimales pegados")
        println("   - Añadir función de limpieza ANTES del análisis principal")
        println("   - Separar '1.5productos' → '1.5 productos'")
        println("   - NO modificar el algoritmo principal")
        
        println("\n🎯 FASE 3: Detección de contexto técnico")
        println("   - Añadir lista de palabras técnicas/unidades")
        println("   - Evitar interpretar como numeración si hay contexto técnico")
        println("   - Mantener numeración básica intacta")
        
        println("\n🎯 FASE 4: Integración y pruebas exhaustivas")
        println("   - Combinar todas las mejoras")
        println("   - Probar TODOS los casos existentes + extremos")
        println("   - Rollback si hay regresiones")
        
        println("\n✨ RESULTADO ESPERADO:")
        println("   - ✅ Casos básicos siguen funcionando perfectamente")
        println("   - ✅ Casos extremos mejoran significativamente")
        println("   - ✅ Sin regresiones en funcionalidad existente")
        println("   - ✅ Parser más robusto y confiable")
    }
}
