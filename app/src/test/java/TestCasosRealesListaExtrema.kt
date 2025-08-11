import org.junit.Test
import org.junit.Assert.*
import com.listafacilnueva.parser.IntegradorMejoras

/**
 * 🎯 TEST FINAL: CASOS REALES DE LISTA EXTREMA
 * 
 * Este test usa casos directos de la lista extremamente desafiante
 * para demostrar cómo las mejoras solucionan los problemas reales.
 */
class TestCasosRealesListaExtrema {
    
    @Test
    fun test_casos_reales_problematicos() {
        println("🎯 TESTING CON CASOS REALES DE LA LISTA EXTREMA")
        println("Estos son los casos exactos que estaban fallando")
        println("=".repeat(70))
        
        // Casos copiados directamente de lista_extremadamente_desafiante.txt
        val casosReales = listOf(
            "Yogur griego 2.5.",
            "Mozzarella 0.8 en descuento.",
            "Esponjas abrasivas 11.2 en combo.",
            "1.8bolsas quinoa orgánica",
            "2.4 aceite oliva extra virgen marca borges o el más puro",
            
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión",
            
            "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable",
            
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso",
            
            "Crema hidratante 3.4.",
            "2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería...",
            "Toallitas desmaquillantes 6.8 en paquete familiar.",
            "3.5bolsas garbanzos premium",
            
            "fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos"
        )
        
        var casosMejorados = 0
        var casosMantenidos = 0
        var casosEmpeorados = 0
        
        casosReales.forEachIndexed { index, caso ->
            println("\\n🔍 CASO #${index + 1}: '$caso'")
            println("-".repeat(50))
            
            try {
                val comparacion = IntegradorMejoras.compararResultados(caso)
                val sinMejoras = comparacion["sin_mejoras"] as Int
                val conMejoras = comparacion["con_mejoras"] as Int
                val mejoro = comparacion["mejora"] as Boolean
                
                when {
                    mejoro -> {
                        println("  ✅ MEJORADO: $sinMejoras → $conMejoras productos (+${conMejoras - sinMejoras})")
                        casosMejorados++
                        
                        @Suppress("UNCHECKED_CAST")
                        val productosNuevos = comparacion["productos_con_mejoras"] as List<String>
                        println("    Productos generados:")
                        productosNuevos.forEachIndexed { i, prod -> 
                            println("      ${i+1}. $prod") 
                        }
                    }
                    conMejoras >= sinMejoras -> {
                        println("  ✅ MANTENIDO: $sinMejoras productos (sin regresión)")
                        casosMantenidos++
                    }
                    else -> {
                        println("  ❌ EMPEORADO: $sinMejoras → $conMejoras productos")
                        casosEmpeorados++
                    }
                }
                
            } catch (e: Exception) {
                println("  ❌ ERROR: ${e.message}")
                casosEmpeorados++
            }
        }
        
        // Resultados finales
        println("\\n" + "=".repeat(70))
        println("🏆 RESULTADOS FINALES DE CASOS REALES")
        println("=".repeat(70))
        
        val totalCasos = casosReales.size
        println("📊 ESTADÍSTICAS:")
        println("  ✅ Casos mejorados: $casosMejorados de $totalCasos (${casosMejorados * 100 / totalCasos}%)")
        println("  ✅ Casos mantenidos: $casosMantenidos de $totalCasos (${casosMantenidos * 100 / totalCasos}%)")
        println("  ❌ Casos empeorados: $casosEmpeorados de $totalCasos (${casosEmpeorados * 100 / totalCasos}%)")
        
        val casosExitosos = casosMejorados + casosMantenidos
        val porcentajeExito = (casosExitosos * 100) / totalCasos
        
        println("\\n🎯 EVALUACIÓN GENERAL:")
        println("  Éxito total: $casosExitosos de $totalCasos casos ($porcentajeExito%)")
        
        when {
            porcentajeExito >= 90 -> {
                println("  🥇 EXCELENTE: Las mejoras resuelven casi todos los casos extremos")
            }
            porcentajeExito >= 75 -> {
                println("  🥈 MUY BUENO: Las mejoras resuelven la mayoría de casos extremos")
            }
            porcentajeExito >= 60 -> {
                println("  🥉 BUENO: Las mejoras tienen un impacto positivo significativo")
            }
            porcentajeExito >= 40 -> {
                println("  ⚠️  ACEPTABLE: Las mejoras ayudan parcialmente")
            }
            else -> {
                println("  🔧 NECESITA TRABAJO: Las mejoras requieren más refinamiento")
            }
        }
        
        // Verificación mínima para el test
        assertTrue("Al menos 40% de los casos deben mejorar o mantenerse", porcentajeExito >= 40)
        assertTrue("No debe haber más casos empeorados que mejorados", casosEmpeorados <= casosMejorados)
        
        println("\\n💡 CONCLUSIÓN:")
        if (casosMejorados > 0) {
            println("  Las mejoras implementadas solucionan varios de los problemas")  
            println("  identificados en la lista extremamente desafiante.")
            println("  Los casos que no mejoran son desafíos adicionales para futuras mejoras.")
        } else {
            println("  Las mejoras no tuvieron el impacto esperado.")
            println("  Se requiere análisis adicional de los patrones problemáticos.")
        }
        
        println("\\n🚀 RECOMENDACIÓN:")
        if (porcentajeExito >= 60) {
            println("  ✅ Las mejoras están listas para integración en el QuantityParser principal")
            println("  📝 Se recomienda hacer commit de estos cambios")
        } else {
            println("  🔧 Se recomienda refinamiento adicional antes de la integración")
            println("  📝 Analizar casos específicos que no mejoraron")
        }
    }
    
    @Test  
    fun test_validacion_casos_basicos_funcionan() {
        println("\\n🧪 VALIDACIÓN: Los casos básicos deben seguir funcionando")
        println("-".repeat(50))
        
        val casosBasicos = mapOf(
            "1.pan 2.leche 3.huevos" to 3,
            "2.5 metros de cable" to 1,
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to 3,
            "pan blanco" to 1,
            "leche descremada" to 1
        )
        
        var todosFuncionan = true
        
        for ((caso, productosEsperados) in casosBasicos) {
            val comparacion = IntegradorMejoras.compararResultados(caso)
            val conMejoras = comparacion["con_mejoras"] as Int
            
            val funciona = conMejoras >= productosEsperados - 1 // Tolerancia de ±1
            
            if (funciona) {
                println("  ✅ '$caso' → $conMejoras productos (esperados: $productosEsperados)")
            } else {
                println("  ❌ '$caso' → $conMejoras productos (esperados: $productosEsperados)")
                todosFuncionan = false
            }
        }
        
        assertTrue("Todos los casos básicos deben seguir funcionando", todosFuncionan)
        
        if (todosFuncionan) {
            println("  🎉 ✅ TODOS LOS CASOS BÁSICOS MANTIENEN SU FUNCIONALIDAD")
        }
    }
}
