import org.junit.Test
import com.listafacilnueva.parser.QuantityParser

class TestValidacionRapida {
    
    @Test
    fun testCasosRapidos() {
        println("🚀 VALIDACIÓN RÁPIDA DE CORRECCIONES")
        
        val casos = mapOf(
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
            "2.5 metros de cinta adhesiva,,,3 tubos de pegamento" to 2,
            "6sandias,8tomates" to 2,
            "cinco focos ocho rollos" to 2
        )
        
        var casosExitosos = 0
        
        casos.forEach { (entrada, esperado) ->
            println("\n📝 TEST: '$entrada'")
            val productos = QuantityParser.parse(entrada)
            val obtenido = productos.size
            
            if (obtenido == esperado) {
                println("✅ ÉXITO: $obtenido productos (esperado: $esperado)")
                casosExitosos++
            } else {
                println("❌ FALLA: $obtenido productos (esperado: $esperado)")
                productos.forEachIndexed { i, p -> 
                    println("   ${i+1}. ${p.nombre} [${p.cantidad}${p.unidad ?: ""}]")
                }
            }
        }
        
        println("\n🎯 RESUMEN: $casosExitosos/${casos.size} casos exitosos")
        
        if (casosExitosos == casos.size) {
            println("🎉 ¡TODAS LAS CORRECCIONES FUNCIONAN CORRECTAMENTE!")
        } else {
            println("❌ Aún hay casos que requieren atención")
        }
    }
}
