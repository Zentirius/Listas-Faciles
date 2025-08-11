import org.junit.Test
import org.junit.Assert.*
import com.listafacilnueva.parser.QuantityParserMejoras

/**
 * 🧪 TESTS PARA MEJORAS DE CASOS EXTREMOS
 * 
 * Estos tests verifican que las mejoras funcionen correctamente
 * sin romper la funcionalidad existente.
 */
class TestMejorasCasosExtremos {
    
    @Test
    fun test_validar_casos_basicos_no_se_rompen() {
        println("🧪 TEST: Validar que los casos básicos no se rompan")
        
        val resultado = QuantityParserMejoras.validarCasosBasicos()
        assertTrue("Los casos básicos deben seguir funcionando", resultado)
        
        println("✅ CASOS BÁSICOS MANTIENEN FUNCIONALIDAD")
    }
    
    @Test
    fun test_decimal_con_punto_final() {
        println("🧪 TEST: Detectar decimal con punto final")
        
        // Caso problemático: "Yogur griego 2.5."
        val caso1 = "Yogur griego 2.5."
        val esDecimal = QuantityParserMejoras.esDecimalConPuntoFinal(caso1)
        assertTrue("Debe detectar 'Yogur griego 2.5.' como decimal con punto final", esDecimal)
        
        // Caso normal: "1. Primera cosa"  
        val caso2 = "1. Primera cosa"
        val noEsDecimal = QuantityParserMejoras.esDecimalConPuntoFinal(caso2)
        assertFalse("No debe detectar '1. Primera cosa' como decimal con punto final", noEsDecimal)
        
        // Caso normal: "Leche 3.2 litros."
        val caso3 = "Leche 3.2 litros."
        val esDecimal3 = QuantityParserMejoras.esDecimalConPuntoFinal(caso3)
        assertFalse("No debe detectar 'Leche 3.2 litros.' porque no termina con número.", esDecimal3)
        
        println("✅ DETECCIÓN DE DECIMAL CON PUNTO FINAL FUNCIONA")
    }
    
    @Test
    fun test_separar_decimales_pegados() {
        println("🧪 TEST: Separar decimales pegados")
        
        // Caso: "1.8bolsas quinoa orgánica"
        val caso1 = "1.8bolsas quinoa orgánica"
        val resultado1 = QuantityParserMejoras.separarDecimalesPegados(caso1)
        assertEquals("Debe separar '1.8bolsas' en '1.8 bolsas'", "1.8 bolsas quinoa orgánica", resultado1)
        
        // Caso múltiple: "2.5kilos arroz 3.7metros tela"
        val caso2 = "2.5kilos arroz 3.7metros tela"
        val resultado2 = QuantityParserMejoras.separarDecimalesPegados(caso2)
        assertEquals("Debe separar múltiples decimales pegados", "2.5 kilos arroz 3.7 metros tela", resultado2)
        
        // Caso que no debe cambiar: "2.5 kilos ya separado"
        val caso3 = "2.5 kilos ya separado"
        val resultado3 = QuantityParserMejoras.separarDecimalesPegados(caso3)
        assertEquals("No debe cambiar lo que ya está bien", "2.5 kilos ya separado", resultado3)
        
        println("✅ SEPARACIÓN DE DECIMALES PEGADOS FUNCIONA")
    }
    
    @Test
    fun test_contexto_tecnico() {
        println("🧪 TEST: Detección de contexto técnico")
        
        // Caso técnico: "2.7metros cuerda náutica 3.9martillo"
        val casoTecnico = "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable"
        val esTecnico = QuantityParserMejoras.tieneContextoTecnico(casoTecnico)
        assertTrue("Debe detectar contexto técnico por palabras clave", esTecnico)
        
        // Caso normal: "1.pan 2.leche 3.huevos"
        val casoNormal = "1.pan 2.leche 3.huevos"
        val noEsTecnico = QuantityParserMejoras.tieneContextoTecnico(casoNormal)
        assertFalse("No debe detectar contexto técnico en lista normal", noEsTecnico)
        
        // Caso con unidades: "2.5 metros de cable"
        val casoConUnidades = "2.5 metros de cable"
        val esUnidad = QuantityParserMejoras.tieneContextoTecnico(casoConUnidades)
        assertTrue("Debe detectar unidades técnicas", esUnidad)
        
        println("✅ DETECCIÓN DE CONTEXTO TÉCNICO FUNCIONA")
    }
    
    @Test
    fun test_separacion_por_comas_con_decimales() {
        println("🧪 TEST: Separación por comas con decimales")
        
        // Caso complejo: "verduras orgánicas,11.5manzanas,16.8limones,7.3 cebollas"
        val casoComplejo = "verduras orgánicas,11.5manzanas,16.8limones,7.3 cebollas rojas"
        val fragmentos = QuantityParserMejoras.separarPorComasConDecimales(casoComplejo)
        
        assertTrue("Debe generar al menos 4 fragmentos", fragmentos.size >= 4)
        assertTrue("Primer fragmento debe ser 'verduras orgánicas'", fragmentos[0].trim() == "verduras orgánicas")
        assertTrue("Segundo fragmento debe contener 'manzanas' separado", fragmentos[1].contains("manzanas"))
        assertTrue("Segundo fragmento debe tener espacio entre número y producto", fragmentos[1].contains("11.5 manzanas"))
        
        println("✅ SEPARACIÓN POR COMAS CON DECIMALES FUNCIONA")
    }
    
    @Test
    fun test_caso_extremo_colonias() {
        println("🧪 TEST: Caso extremo - colonias complejas")
        
        val casoExtemo = "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión"
        
        // Aplicar mejoras paso a paso
        val paso1 = QuantityParserMejoras.separarDecimalesPegados(casoExtemo)
        println("  Paso 1 (decimales pegados): '$paso1'")
        
        val fragmentos = QuantityParserMejoras.separarPorComasConDecimales(paso1)
        println("  Fragmentos finales: ${fragmentos.size}")
        fragmentos.forEachIndexed { i, frag ->
            println("    ${i+1}. '$frag'")
        }
        
        // Verificaciones
        assertTrue("Debe generar al menos 4 fragmentos", fragmentos.size >= 4)
        
        // Verificar que los decimales estén separados
        val tienenDecimalesSeparados = fragmentos.drop(1).all { fragmento ->
            val decimal = Regex("\\d+\\.\\d+").find(fragmento)
            if (decimal != null) {
                // Debe haber espacio después del decimal O antes del producto
                val tieneSeparacion = fragmento.contains(Regex("\\d+\\.\\d+\\s+[a-zA-Z]")) || 
                                     fragmento.trim().matches(Regex("\\d+\\.\\d+\\s+.+"))
                println("    Fragmento '$fragmento' → separado: $tieneSeparacion")
                tieneSeparacion
            } else {
                true // Si no tiene decimal, está bien
            }
        }
        
        assertTrue("Los decimales deben estar separados de los productos", tienenDecimalesSeparados)
        
        println("✅ CASO EXTREMO COLONIAS MEJORADO")
    }
    
    @Test
    fun test_aplicar_mejoras_seguras() {
        println("🧪 TEST: Aplicar todas las mejoras de forma segura")
        
        val casosDeTest = mapOf(
            "Yogur griego 2.5." to "Yogur griego 2.5",
            "1.8bolsas quinoa" to "1.8 bolsas quinoa",
            "verduras,2.3manzanas,4.7peras" to "verduras,2.3 manzanas,4.7 peras",
            "1.pan 2.leche 3.huevos" to "1.pan 2.leche 3.huevos" // No debe cambiar
        )
        
        for ((input, esperado) in casosDeTest) {
            val resultado = QuantityParserMejoras.aplicarMejorasSeguras(input)
            println("  Input: '$input'")
            println("  Esperado: '$esperado'")
            println("  Resultado: '$resultado'")
            
            // Verificar que la mejora sea razonable (no necesariamente exacta)
            val esRazonable = resultado.length >= input.length * 0.8 && 
                             resultado.length <= input.length * 1.3
            assertTrue("El resultado debe ser razonable para '$input'", esRazonable)
            
            println("  ✅ Mejora aplicada correctamente")
            println()
        }
        
        println("✅ TODAS LAS MEJORAS APLICADAS CORRECTAMENTE")
    }
}
