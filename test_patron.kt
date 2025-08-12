fun testPatron() {
    val linea = "1.2metros cable 2.bombilla grande 3.zapallo chino"
    val patron3Productos = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
    val match = patron3Productos.find(linea)
    
    println("Texto: '$linea'")
    println("¿Coincide patrón? ${match != null}")
    
    if (match != null) {
        println("Grupos:")
        match.groupValues.forEachIndexed { index, value ->
            println("  $index: '$value'")
        }
    } else {
        println("No coincide. Analizando por partes:")
        println("Parte 1 (1.2metros cable): ${Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)").find(linea)}")
        println("Parte 2 (2.bombilla grande): ${Regex("(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)").findAll(linea).toList()}")
        println("Parte 3 (3.zapallo chino): ${Regex("(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$").find(linea)}")
    }
}

testPatron()
