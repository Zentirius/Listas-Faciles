// EJECUTAR MANUALMENTE - Test rápido de los casos problemáticos

import com.listafacilnueva.parser.QuantityParser

fun main() {
    println("🔧 TEST MANUAL: Casos problemáticos")
    println("="*50)
    
    // Caso 1: 1.1papa mediana2.sandia grande 3.zapato grande
    println("\n🧪 CASO 1: '1.1papa mediana2.sandia grande 3.zapato grande'")
    val caso1 = QuantityParser.parse("1.1papa mediana2.sandia grande 3.zapato grande")
    println("   Productos detectados: ${caso1.size}")
    caso1.forEach { p ->
        println("   - ${p.nombre} (${p.cantidad ?: 1})")
    }
    
    // Verificar productos esperados
    val tienePapa = caso1.any { it.nombre.contains("papa", ignoreCase = true) }
    val tieneSandia = caso1.any { it.nombre.contains("sandia", ignoreCase = true) }
    val tieneZapato = caso1.any { it.nombre.contains("zapato", ignoreCase = true) }
    
    println("   ✓ Papa: $tienePapa")
    println("   ✓ Sandia: $tieneSandia") 
    println("   ✓ Zapato: $tieneZapato")
    
    // Caso 2: 1.2metros cable 2.bombilla grande 3.zapallo chino
    println("\n🧪 CASO 2: '1.2metros cable 2.bombilla grande 3.zapallo chino'")
    val caso2 = QuantityParser.parse("1.2metros cable 2.bombilla grande 3.zapallo chino")
    println("   Productos detectados: ${caso2.size}")
    caso2.forEach { p ->
        println("   - ${p.nombre} (${p.cantidad ?: 1})")
    }
    
    // Verificar productos esperados
    val tieneCable = caso2.any { it.nombre.contains("cable", ignoreCase = true) }
    val tieneBombilla = caso2.any { it.nombre.contains("bombilla", ignoreCase = true) }
    val tieneZapallo = caso2.any { it.nombre.contains("zapallo", ignoreCase = true) }
    
    println("   ✓ Cable: $tieneCable")
    println("   ✓ Bombilla: $tieneBombilla")
    println("   ✓ Zapallo: $tieneZapallo")
    
    println("\n" + "="*50)
    println("📊 RESUMEN:")
    println("Caso 1 detectó: ${caso1.size}/3 productos")
    println("Caso 2 detectó: ${caso2.size}/3 productos")
    
    val totalDetectados = caso1.size + caso2.size
    println("Total detectados: $totalDetectados/6 productos")
    
    if (totalDetectados < 6) {
        println("\n❌ ESTOS SON LOS PRODUCTOS FALTANTES!")
        println("Los productos que faltan para llegar a 70 están en estos casos.")
    }
}

operator fun String.times(n: Int): String = this.repeat(n)
