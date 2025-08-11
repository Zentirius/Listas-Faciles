import org.junit.Test
import org.junit.Assert.*
import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.parser.QuantityParserMejoras

/**
 * 🚨 TEST DIRECTO CON LA LISTA EXTREMA REAL
 * 
 * Probamos cada caso problemático específico de la lista extrema
 * y vemos exactamente cómo arreglarlos.
 */
class TestListaExtremaReal {
    
    @Test
    fun test_casos_extremos_especificos() {
        println("� PRUEBAS CON CASOS REALES DE LISTA EXTREMA")
        println("=".repeat(80))
        
        // Los casos más problemáticos identificados en la lista real
        val casosExtremos = listOf(
            "Yogur griego 2.5.",
            "Mozzarella 0.8 en descuento.",  
            "Esponjas abrasivas 11.2 en combo.",
            "1.8bolsas quinoa orgánica",
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión",
            "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable",
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso",
            "Crema hidratante 3.4.",
            "3.5bolsas garbanzos premium",
            "fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos"
        )
        
        for ((index, caso) in casosExtremos.withIndex()) {
            probarCasoExtremo(index + 1, caso)
        }
    }
    
    private fun probarCasoExtremo(numero: Int, caso: String) {
        println("\\n🔍 CASO EXTREMO #$numero: '$caso'")
        println("-".repeat(60))
        
        // ANÁLISIS PREVIO
        analizarProblemaEspecifico(caso)
        
        // ANTES: Parser actual sin mejoras
        println("\\n🔴 PARSER ACTUAL (sin mejoras):")
        val resultadoActual = QuantityParser.parse(caso)
        mostrarResultados("ACTUAL", resultadoActual)
        
        // DESPUÉS: Con nuestras mejoras
        println("\\n🔧 APLICANDO MEJORAS:")
        val casoMejorado = QuantityParserMejoras.aplicarMejorasSeguras(caso)
        println("  Input mejorado: '$casoMejorado'")
        
        val resultadoMejorado = QuantityParser.parse(casoMejorado)
        mostrarResultados("MEJORADO", resultadoMejorado)
        
        // EVALUACIÓN
        evaluarMejora(caso, resultadoActual, resultadoMejorado)
        
        println("\\n" + "=".repeat(60))
    }
    
    private fun analizarProblemaEspecifico(caso: String) {
        println("📊 ANÁLISIS DEL PROBLEMA:")
        
        // Detectar decimales
        val decimales = Regex("\\d+\\.\\d+").findAll(caso).map { it.value }.toList()
        println("  Decimales encontrados: $decimales")
        
        // Detectar decimales pegados
        val pegados = Regex("\\d+\\.\\d+[a-zA-Záéíóúüñ]").findAll(caso).map { it.value }.toList()
        println("  Decimales pegados: $pegados")
        
        // Detectar punto final
        val puntoFinal = caso.endsWith(".") && Regex("\\d+\\.\\d+\\.$").find(caso) != null
        println("  ¿Decimal con punto final?: $puntoFinal")
        
        // Detectar comas
        val tieneComas = caso.contains(",")
        val fragmentosComa = if (tieneComas) caso.split(",").size else 0
        println("  ¿Tiene comas?: $tieneComas (fragmentos: $fragmentosComa)")
        
        // Detectar contexto técnico
        val esTecnico = QuantityParserMejoras.tieneContextoTecnico(caso)
        println("  ¿Contexto técnico?: $esTecnico")
    }
    
    private fun mostrarResultados(tipo: String, productos: List<com.listafacilnueva.model.Producto>) {
        println("  Productos $tipo: ${productos.size}")
        if (productos.isEmpty()) {
            println("    ❌ No se generaron productos")
        } else {
            productos.forEachIndexed { i, producto ->
                println("    ${i+1}. '${producto.nombre}' [cantidad: ${producto.cantidad}${producto.unidad}]")
            }
        }
    }
    
    private fun evaluarMejora(
        casoOriginal: String, 
        resultadoActual: List<com.listafacilnueva.model.Producto>, 
        resultadoMejorado: List<com.listafacilnueva.model.Producto>
    ) {
        println("\\n🎯 EVALUACIÓN DE LA MEJORA:")
        
        val cantidadActual = resultadoActual.size
        val cantidadMejorada = resultadoMejorado.size
        
        when {
            cantidadMejorada > cantidadActual -> {
                println("  ✅ MEJORA SIGNIFICATIVA: $cantidadActual → $cantidadMejorada productos (+${cantidadMejorada - cantidadActual})")
                
                // Analizar qué se mejoró específicamente
                analizarTipoMejora(casoOriginal, resultadoActual, resultadoMejorado)
            }
            cantidadMejorada == cantidadActual && cantidadMejorada > 0 -> {
                println("  ✅ MANTUVO CALIDAD: $cantidadActual productos (sin regresión)")
                
                // Verificar si mejoró la calidad de los productos
                val mejoroCalidad = verificarMejoraCalidad(resultadoActual, resultadoMejorado)
                if (mejoroCalidad) {
                    println("    📈 Mejoró la calidad de los productos existentes")
                }
            }
            cantidadMejorada < cantidadActual -> {
                println("  ⚠️  POSIBLE REGRESIÓN: $cantidadActual → $cantidadMejorada productos (-${cantidadActual - cantidadMejorada})")
            }
            cantidadMejorada == 0 -> {
                println("  ❌ NO FUNCIONÓ: No se generaron productos")
                sugerirSolucionEspecifica(casoOriginal)
            }
        }
    }
    
    private fun analizarTipoMejora(
        caso: String,
        antes: List<com.listafacilnueva.model.Producto>,
        despues: List<com.listafacilnueva.model.Producto>
    ) {
        println("    📋 TIPO DE MEJORA DETECTADA:")
        
        if (caso.contains(",") && despues.size > antes.size) {
            println("      🔧 Mejoró separación por comas")
        }
        
        if (Regex("\\d+\\.\\d+[a-zA-Z]").containsMatchIn(caso) && despues.size > antes.size) {
            println("      🔧 Mejoró separación de decimales pegados")
        }
        
        if (caso.endsWith(".") && Regex("\\d+\\.\\d+\\.$").containsMatchIn(caso) && despues.size > antes.size) {
            println("      🔧 Mejoró detección de decimal con punto final")
        }
        
        if (QuantityParserMejoras.tieneContextoTecnico(caso) && despues.size > antes.size) {
            println("      🔧 Mejoró manejo de contexto técnico")
        }
    }
    
    private fun verificarMejoraCalidad(
        antes: List<com.listafacilnueva.model.Producto>,
        despues: List<com.listafacilnueva.model.Producto>
    ): Boolean {
        if (antes.isEmpty() || despues.isEmpty()) return false
        
        // Verificar si los nombres son más limpios o las cantidades más precisas
        val nombresMejores = despues.any { producto ->
            producto.nombre.length > 3 && !producto.nombre.contains(Regex("\\d+\\.\\d+[a-zA-Z]"))
        }
        
        val cantidadesMejores = despues.any { producto ->
            producto.cantidad != null && producto.cantidad != 1.0
        }
        
        return nombresMejores || cantidadesMejores
    }
    
    private fun sugerirSolucionEspecifica(caso: String) {
        println("    💡 SUGERENCIAS ESPECÍFICAS:")
        
        when {
            caso.endsWith(".") && Regex("\\d+\\.\\d+\\.$").containsMatchIn(caso) -> {
                println("      - Mejorar detección de decimal con punto final")
            }
            Regex("\\d+\\.\\d+[a-zA-Z]").containsMatchIn(caso) -> {
                println("      - Mejorar separación de decimales pegados")
            }
            caso.contains(",") -> {
                println("      - Mejorar separación por comas con múltiples decimales")
            }
            QuantityParserMejoras.tieneContextoTecnico(caso) -> {
                println("      - Añadir más palabras técnicas al contexto")
            }
            else -> {
                println("      - Necesita análisis más profundo del patrón específico")
            }
        }
    }
    
    @Test
    fun test_proponer_mejoras_adicionales() {
        println("🚀 ANALIZANDO QUÉ MEJORAS ADICIONALES SE NECESITAN")
        println("=".repeat(70))
        
        // Casos que probablemente necesiten mejoras adicionales
        val casosDesafiantes = mapOf(
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso" to 
            "Múltiples productos pegados sin espacios ni comas",
            
            "fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos" to
            "Caso híbrido: comas + decimales pegados + producto final sin coma",
            
            "2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería..." to
            "Texto interrumpido con preguntas"
        )
        
        for ((caso, descripcion) in casosDesafiantes) {
            println("\\n🎯 CASO DESAFIANTE:")
            println("  Descripción: $descripcion")
            println("  Caso: '$caso'")
            
            // Probar con mejoras actuales
            val mejorado = QuantityParserMejoras.aplicarMejorasSeguras(caso)
            val resultado = QuantityParser.parse(mejorado)
            
            println("  Resultado actual: ${resultado.size} productos")
            
            // Proponer mejoras adicionales específicas
            proponerMejorasAdicionales(caso, descripcion, resultado.size)
        }
    }
    
    private fun proponerMejorasAdicionales(caso: String, descripcion: String, productosActuales: Int) {
        println("  💡 MEJORAS ADICIONALES SUGERIDAS:")
        
        when {
            caso.contains("preguntar...") -> {
                println("    - Limpiar texto interrumpido con preguntas antes del parsing")
                println("    - Patrón: texto.replace(Regex(\"preguntar\\\\.\\\\.\\\\..*?\\\\.\\\\.\\\\.\\\\.\"), \"\")")
            }
            
            Regex("\\d+\\.\\d+[a-zA-Z]+\\d+\\.\\d+[a-zA-Z]").containsMatchIn(caso) -> {
                println("    - Mejorar separación de múltiples decimales pegados consecutivos")
                println("    - Usar separación posicional por regex matches")
            }
            
            caso.contains(",") && !caso.endsWith(",") && 
            Regex("\\d+\\.\\d+\\s+[a-zA-Z]+\\s*$").containsMatchIn(caso) -> {
                println("    - Detectar producto final sin coma después de lista con comas")
                println("    - Separar último fragmento si tiene patrón decimal+producto")
            }
            
            else -> {
                println("    - Análisis manual específico requerido")
            }
        }
        
        // Calcular productos esperados vs actuales
        val decimalesEncontrados = Regex("\\d+\\.\\d+").findAll(caso).count()
        val productosEsperados = maxOf(1, decimalesEncontrados)
        
        if (productosActuales < productosEsperados) {
            println("    ⚠️  GAP: Se esperan ~$productosEsperados productos pero solo genera $productosActuales")
        }
    }
}
