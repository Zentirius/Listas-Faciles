import org.junit.Test
import org.junit.Assert.*

class SimulacionCasosExtremos {
    
    @Test
    fun analizarCasosProblematicosManualmente() {
        println("🔬 SIMULACIÓN COMPLETA DE CASOS EXTREMOS")
        println("=".repeat(60))
        
        // Caso 1: Yogur griego 2.5.
        analizar_caso_yogur()
        
        // Caso 2: Múltiples decimales complejos  
        analizar_multiples_decimales()
        
        // Caso 3: Productos técnicos
        analizar_productos_tecnicos()
        
        // Caso 4: Decimales pegados
        analizar_decimales_pegados()
        
        // Caso 5: Colonias con múltiples decimales
        analizar_colonias_complejas()
        
        mostrar_estrategia_general()
    }
    
    fun analizar_caso_yogur() {
        println("\n🔍 CASO PROBLEMÁTICO 1: 'Yogur griego 2.5.'")
        val input = "Yogur griego 2.5."
        
        // Análisis del patrón
        val tieneDecimalConPunto = Regex("\\d+\\.\\d+\\.").find(input) != null
        val esNumeracion = input.matches(Regex("^\\d+\\.\\s.*")) 
        
        println("INPUT: '$input'")
        println("¿Tiene decimal con punto final?: $tieneDecimalConPunto")
        println("¿Parece numeración?: $esNumeracion")
        println("🎯 PROBLEMA: El punto final puede confundir la detección")
        println("💡 SOLUCIÓN: Mejorar regex para distinguir cantidad decimal vs numeración")
    }
    
    fun analizar_multiples_decimales() {
        println("\n🔍 CASO PROBLEMÁTICO 2: Múltiples decimales con comas")
        val input = "verduras orgánicas ,11.5manzanas,16.8limones,7.3 cebollas rojas 12.1 peras maduras"
        
        val fragmentosPorComa = input.split(",").map { it.trim() }
        val decimalesPegados = Regex("\\d+\\.\\d+[a-zA-Z]").findAll(input).toList()
        
        println("INPUT: '$input'")
        println("Fragmentos por coma: ${fragmentosPorComa.size}")
        fragmentosPorComa.forEachIndexed { i, fragmento ->
            println("  ${i+1}. '$fragmento'")
        }
        println("Decimales pegados: ${decimalesPegados.size}")
        decimalesPegados.forEach { println("  → '${it.value}'") }
        
        println("🎯 PROBLEMA: Múltiples decimales pegados + separación incompleta")
        println("💡 SOLUCIÓN: Optimizar separación por comas + decimales pegados")
    }
    
    fun analizar_productos_tecnicos() {
        println("\n🔍 CASO PROBLEMÁTICO 3: Productos técnicos vs numeración")
        val input = "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable"
        
        val numerosDecimales = Regex("(\\d+\\.\\d+)").findAll(input).map { it.groupValues[1] }.toList()
        val palabrasTecnicas = listOf("metros", "martillo", "clavos", "acero", "náutica")
        val tieneUnidades = palabrasTecnicas.any { input.contains(it, ignoreCase = true) }
        
        println("INPUT: '$input'")
        println("Números decimales encontrados: $numerosDecimales")
        println("¿Contiene palabras técnicas?: $tieneUnidades")
        
        if (tieneUnidades) {
            println("🎯 CONCLUSIÓN: Son cantidades reales, NO numeración de lista")
            println("💡 SOLUCIÓN: Detectar contexto técnico para evitar falsa numeración")
        }
    }
    
    fun analizar_decimales_pegados() {
        println("\n🔍 CASO PROBLEMÁTICO 4: Decimales pegados sin espacio")
        val input = "1.8bolsas quinoa orgánica"
        
        val patronPegado = Regex("(\\d+\\.\\d+)([a-zA-Z]+)").find(input)
        if (patronPegado != null) {
            val cantidad = patronPegado.groupValues[1]
            val producto = patronPegado.groupValues[2]
            println("INPUT: '$input'")
            println("Cantidad extraída: '$cantidad'")
            println("Producto extraído: '$producto'")
            println("🎯 ESTADO: Se puede separar con regex mejorado")
            println("💡 SOLUCIÓN: Patrón específico para decimales+letras pegados")
        }
    }
    
    fun analizar_colonias_complejas() {
        println("\n🔍 CASO PROBLEMÁTICO 5: Caso más complejo - colonias")
        val input = "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión"
        
        val fragmentosPorComa = input.split(",").map { it.trim() }
        val decimalesEncontrados = Regex("\\d+\\.\\d+").findAll(input).map { it.value }.toList()
        val tieneEspaciosVariados = input.contains(Regex("\\d+\\.\\d+\\s+[a-zA-Z]")) && input.contains(Regex("\\d+\\.\\d+[a-zA-Z]"))
        
        println("INPUT: '$input'")
        println("Fragmentos por coma: ${fragmentosPorComa.size}")
        fragmentosPorComa.forEachIndexed { i, fragmento ->
            val tieneDecimal = Regex("\\d+\\.\\d+").containsMatchIn(fragmento)
            println("  ${i+1}. '$fragmento' (¿decimal?: $tieneDecimal)")
        }
        println("Decimales totales encontrados: ${decimalesEncontrados.size} → $decimalesEncontrados")
        println("¿Tiene espacios variados?: $tieneEspaciosVariados")
        
        println("🎯 PROBLEMA: Combinación de todos los patrones problemáticos")
        println("💡 SOLUCIÓN: Algoritmo híbrido: comas + decimales + contexto")
    }
    
    fun mostrar_estrategia_general() {
        println("\n" + "=".repeat(60))
        println("💡 ESTRATEGIA GENERAL PARA MEJORAS SEGURAS:")
        println("1. 🔒 NO modificar casos básicos que funcionan")
        println("2. 🔧 Añadir detección específica para decimales con punto final")
        println("3. 📊 Mejorar separación de múltiples decimales pegados")
        println("4. 🎯 Implementar detección de contexto técnico")
        println("5. 🧪 Cada mejora debe ser probada individualmente")
        println("6. ⚖️  Mantener balance entre robustez y simplicidad")
        
        println("\n🚀 PLAN DE IMPLEMENTACIÓN:")
        println("- FASE 1: Mejorar detección de decimales finales")
        println("- FASE 2: Optimizar separación por comas + decimales")
        println("- FASE 3: Añadir contexto técnico sin romper numeración básica")
        println("- FASE 4: Probar exhaustivamente cada fase")
    }
}
