// Simulación del parsing del caso problemático
fun main() {
    val texto = "1.2metros cable 2.bombilla grande 3.zapallo chino"
    println("ANÁLISIS: '$texto'")
    
    // Simulamos lo que hace tieneSecuenciaEnumeracion
    val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE)
        .findAll(texto)
        .map { it.groupValues[1].toInt() }
        .toList()
    
    println("Números encontrados: $numerosConPunto")
    
    // Verificar si es secuencia consecutiva
    var esSecuenciaConsecutiva = true
    for (i in 1 until numerosConPunto.size) {
        if (numerosConPunto[i] != numerosConPunto[i-1] + 1) {
            esSecuenciaConsecutiva = false
            break
        }
    }
    
    println("¿Es secuencia consecutiva? $esSecuenciaConsecutiva")
    println("¿Rango válido? ${numerosConPunto[0] in 1..10}")
    println("¿Tiene suficientes números? ${numerosConPunto.size >= 2}")
    
    val esEnumeracion = esSecuenciaConsecutiva && numerosConPunto[0] in 1..10 && numerosConPunto.size >= 2
    println("¿ES ENUMERACIÓN? $esEnumeracion")
    
    if (esEnumeracion) {
        println("\nAPLICANDO TRANSFORMACIÓN...")
        
        // Probar patrón flexible
        val patronFlexible = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ]+)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchFlexible = patronFlexible.find(texto)
        
        if (matchFlexible != null) {
            println("✅ PATRÓN FLEXIBLE COINCIDE")
            println("Grupos capturados:")
            matchFlexible.groupValues.forEachIndexed { index, value ->
                println("  $index: '$value'")
            }
            
            val cantidad1 = matchFlexible.groupValues[2] // "2"
            val unidad1 = matchFlexible.groupValues[3]   // "metros"
            val nombre1 = matchFlexible.groupValues[4].trim() // "cable"
            val nombre2 = matchFlexible.groupValues[6].trim() // "bombilla grande"
            val nombre3 = matchFlexible.groupValues[8].trim() // "zapallo chino"
            
            val resultado = "$cantidad1$unidad1 $nombre1, $nombre2, $nombre3"
            println("RESULTADO TRANSFORMADO: '$resultado'")
            
            // Simular división por comas
            val productos = resultado.split(",").map { it.trim() }
            println("\nPRODUCTOS FINALES: ${productos.size}")
            productos.forEachIndexed { index, producto ->
                println("${index + 1}. '$producto'")
            }
        } else {
            println("❌ PATRÓN FLEXIBLE NO COINCIDE")
        }
    }
}

main()
