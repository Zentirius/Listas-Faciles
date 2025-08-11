fun main() {
    println("SIMULACIÓN CASOS EXTREMOS - LISTA FÁCIL")
    println("=".repeat(60))
    
    // Casos más problemáticos identificados
    val casosProblematicos = listOf(
        // Problema 1: Decimales con punto final
        "Yogur griego 2.5." to "¿Detecta 2.5 o lo confunde con numeración?",
        
        // Problema 2: Múltiples decimales complejos
        "verduras orgánicas ,11.5manzanas,16.8limones,7.3 cebollas rojas 12.1 peras maduras" to "¿Separa los 5 productos con decimales?",
        
        // Problema 3: Productos técnicos numerados vs decimales
        "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable" to "¿Es numeración (2,3,5) o cantidades (2.7, 3.9, 5.2)?",
        
        // Problema 4: Decimales pegados
        "1.8bolsas quinoa orgánica" to "¿Detecta 1.8 bolsas correctamente?",
        
        // Problema 5: Múltiples decimales con comas
        "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión" to "¿Separa los 5 productos con decimales complejos?"
    )
    
    for ((index, caso) in casosProblematicos.withIndex()) {
        val (input, pregunta) = caso
        println("\n🔍 CASO PROBLEMÁTICO ${index + 1}:")
        println("INPUT: '$input'")
        println("PREGUNTA: $pregunta")
        
        // Simular análisis manual de patrones
        println("📋 ANÁLISIS MANUAL:")
        
        when (index) {
            0 -> { // Yogur 2.5.
                println("  - Patrón detectado: 'nombre cantidad.'")
                println("  - ¿Problema?: El punto final puede confundir con numeración")
                println("  - Regex actual: ¿Diferencia entre '2.5.' y '2.' numeración?")
                
                val tieneDecimalConPunto = Regex("\\d+\\.\\d+\\.$").find(input) != null
                println("  - ¿Tiene decimal con punto final?: $tieneDecimalConPunto")
                
                val esNumeracion = Regex("^\\d+\\.\\s").find(input) != null
                println("  - ¿Parece numeración?: $esNumeracion")
            }
            
            1 -> { // Múltiples productos con decimales
                println("  - Patrón: 'producto ,decimal1producto,decimal2producto'")
                println("  - Problema: Separar múltiples decimales pegados")
                
                val decimalesPegados = Regex("\\d+\\.\\d+[a-zA-Z]").findAll(input).toList()
                println("  - Decimales pegados encontrados: ${decimalesPegados.size}")
                decimalesPegados.forEach { println("    → '${it.value}'") }
                
                val productos = input.split(",").filter { it.trim().isNotBlank() }
                println("  - Fragmentos por coma: ${productos.size}")
                productos.forEach { println("    → '${it.trim()}'") }
            }
            
            2 -> { // Numeración vs cantidades técnicas
                println("  - Patrón: 'decimal1+texto decimal2+texto decimal3+texto'")
                println("  - Problema: ¿Es numeración de lista (2,3,5) o cantidades reales?")
                
                val numerosConPunto = Regex("(\\d+\\.\\d+)").findAll(input).map { it.groupValues[1] }.toList()
                println("  - Números decimales: $numerosConPunto")
                
                val palabrasClave = listOf("metros", "martillo", "clavos")
                val tieneUnidades = palabrasClave.any { input.contains(it, ignoreCase = true) }
                println("  - ¿Contiene unidades técnicas?: $tieneUnidades")
                
                if (tieneUnidades) {
                    println("  - CONCLUSIÓN: Parecen cantidades reales, NO numeración")
                } else {
                    println("  - CONCLUSIÓN: Podría ser numeración de lista")
                }
            }
            
            3 -> { // Decimal pegado
                println("  - Patrón: 'decimal+producto'")
                
                val patronPegado = Regex("(\\d+\\.\\d+)([a-zA-Z]+)").find(input)
                if (patronPegado != null) {
                    val cantidad = patronPegado.groupValues[1]
                    val producto = patronPegado.groupValues[2]
                    println("  - Cantidad: '$cantidad'")
                    println("  - Producto: '$producto'")
                    println("  - ¿Se puede separar fácilmente?: SÍ")
                }
            }
            
            4 -> { // Múltiples decimales con comas
                println("  - Patrón complejo: múltiples 'decimal+producto' separados por comas")
                
                val fragmentosPorComa = input.split(",").map { it.trim() }
                println("  - Fragmentos: ${fragmentosPorComa.size}")
                
                fragmentosPorComa.forEachIndexed { i, fragmento ->
                    val tieneDecimal = Regex("\\d+\\.\\d+").containsMatchIn(fragmento)
                    println("    ${i+1}. '$fragmento' (¿decimal?: $tieneDecimal)")
                }
            }
        }
        
        println("🎯 RECOMENDACIÓN PARA ESTE CASO:")
        when (index) {
            0 -> println("  - Mejorar regex para distinguir 'cantidad.' final vs numeración")
            1 -> println("  - Optimizar separación por comas + decimales pegados")
            2 -> println("  - Añadir detección de unidades técnicas para evitar falsa numeración")
            3 -> println("  - Mejorar patrón de decimales pegados sin espacios")
            4 -> println("  - Combinar separación por comas + detección de decimales múltiples")
        }
        
        println("-".repeat(50))
    }
    
    println("\n💡 ESTRATEGIA GENERAL PARA MEJORAS:")
    println("1. 🔒 NO tocar casos que ya funcionan (numeración básica)")
    println("2. 🔧 Mejorar detección de decimales con punto final")
    println("3. 📊 Optimizar separación de múltiples decimales")
    println("4. 🎯 Añadir contexto técnico para evitar falsa numeración")
    println("5. 🧪 Probar cada mejora por separado")
}
