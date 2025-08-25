package com.listafacilnueva

import com.listafacilnueva.parser.QuantityParser

/**
 * 🧪 TEST DE LISTA REAL DE 86 PRODUCTOS - VERIFICACIÓN COMPLETA
 * 
 * Este test utiliza una lista real proporcionada por el usuario con 86 productos
 * que incluye casos muy complejos y desafiantes para el parser.
 * 
 * Casos incluidos:
 * - Productos pegados sin espacios
 * - Decimales complejos (2.5, 3.7, 750.5ml)
 * - Múltiples productos en una línea
 * - Productos con marcas específicas
 * - Unidades variadas (kg, ml, metros, etc.)
 * - Números escritos en palabras (catorce, dieciocho, etc.)
 * - Productos con descripciones largas
 * - Mezcla de formatos en una misma línea
 */
fun main() {
    println("🧪 TEST DE LISTA REAL DE 86 PRODUCTOS - VERIFICACIÓN COMPLETA")
    println("=".repeat(70))
    println("🎯 OBJETIVO: Verificar detección completa de lista compleja real")
    println("📋 PRODUCTOS ESPERADOS: 86")
    println("=".repeat(70))
    
    // 🚀 HABILITAR DEBUG para ver procesamiento detallado
    QuantityParser.setDebugMode(true)
    
    // 📝 LISTA REAL DE 86 PRODUCTOS (texto original del usuario)
    val listaCompleta = """
medicina para presión marca, losartán o genérico barato.(son tabletas de 50mg)
Yogur griego 2.5.
Mozzarella 0.8 en descuento.
3.7 kilos papas preguntar...cuales no tienen ojos...
Pollo entero más barato por kilo en refrigerador
Macarrones 8.2 en oferta especial.
1.5malla duraznos pequeños
2.3 chocolates marca hershey's o el más cremoso

verduras orgánicas ,11.5manzanas,16.8limones,7.3 cebollas rojas 12.1 peras maduras

1. 4.7 kg de azúcar refinada, 1.2 sal marina y pimienta negra (la más picante)
15.6 pilas AAA, 2.8 metros de cable coaxial no muy rígido
media docena bombillas led. 8.3 interruptores dobles
750.5ml de antiséptico 1.4 paquetes de mascarillas quirúrgicas
Papel bond A4
2.9 metros de tubería PVC,,,7.1 codos de 90 grados.

1.6litros leche descremada2.5detergente en polvo4.8paños de limpieza multiuso

1.4papa criolla mediana2.7apio fresco orgánico3.1zanahoria baby

1.salmón rosado 3.2kg extra fresco
2.5rábanos rojos picantes

catorce velas aromáticas eucalipto
dieciocho pilas recargables tipo C

cinco focos led 60w trece metros cable calibre 12

pasta dental blanqueadora marca sensodyne o parodontax.(tubo de 120ml)
Champú reparador 1.7.
Desinfectante multiusos
9.3 rollos papel higiénico doble hoja preguntar...cual es más suave...
Limpiador desengrasante más potente en spray
Esponjas abrasivas 11.2 en combo.
1.8bolsas quinoa orgánica
2.4 aceite oliva extra virgen marca borges o el más puro

colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión

4. 3.8 kg de avena integral, 1.9 miel natural y canela molida (la más aromática)
19.2 tornillos autorroscantes, 4.5 metros de cinta americana resistente al agua
media docena de candados de seguridad. 9.7 llaves maestras
1.2litros de alcohol etílico 96° 2.3 paquetes de gasas estériles
Cinta adhesiva transparente
5.1 metros de cable HDMI 4K,,,8.4 adaptadores USB-C multipuerto.

2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable

2.8metros de lona impermeable 6.1 metros de hilo encerado resistente
4.6tijeras profesionales de costura
7.3pegamento epóxico bicomponente

diecisiete velas votivas aromáticas
veintitrés pilas de litio larga duración

ocho focos halógenos 500w catorce metros tubo galvanizado

suplemento vitamínico marca centrum o kirkland.(frasco de 365 tabletas)
Crema hidratante 3.4.
Jabón neutro pH 5.5
2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería...
Enjuague bucal antibacterial más efectivo en botella grande
Toallitas desmaquillantes 6.8 en paquete familiar.
3.5bolsas garbanzos premium
4.7 vinagre balsámico añejado marca modena o el más concentrado

fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos
""".trimIndent()
    
    println("🔍 PROCESANDO LISTA COMPLETA DE 86 PRODUCTOS")
    println("=".repeat(70))
    
    try {
        val resultados = QuantityParser.parse(listaCompleta)
        
        println("\n" + "=".repeat(70))
        println("📊 RESULTADOS DEL ANÁLISIS")
        println("=".repeat(70))
        
        val productosDetectados = resultados.size
        val porcentajeDeteccion = (productosDetectados.toDouble() / 86) * 100
        
        println("📈 ESTADÍSTICAS GENERALES:")
        println("   • Productos esperados: 86")
        println("   • Productos detectados: $productosDetectados")
        println("   • Porcentaje de detección: ${String.format("%.1f", porcentajeDeteccion)}%")
        
        // 📋 DETALLES DE PRODUCTOS DETECTADOS
        println("\n🎯 PRODUCTOS DETECTADOS:")
        println("-".repeat(70))
        
        for ((index, producto) in resultados.withIndex()) {
            val cantidadStr = producto.cantidad?.let { "cantidad: $it" } ?: "sin cantidad"
            val unidadStr = producto.unidad?.let { " (${producto.unidad})" } ?: ""
            val marcasStr = if (producto.marcas.isNotEmpty()) " [marcas: ${producto.marcas.joinToString(", ")}]" else ""
            
            println("${String.format("%3d", index + 1)}. '${producto.nombre}' ($cantidadStr$unidadStr)$marcasStr")
        }
        
        // 🏆 EVALUACIÓN DE RENDIMIENTO
        println("\n" + "=".repeat(70))
        println("🏆 EVALUACIÓN DE RENDIMIENTO")
        println("=".repeat(70))
        
        when {
            porcentajeDeteccion >= 95 -> {
                println("🥇 ¡EXCELENTE! (${String.format("%.1f", porcentajeDeteccion)}%)")
                println("   ✅ Detección casi perfecta de lista compleja")
                println("   🚀 Parser funciona a nivel profesional")
            }
            porcentajeDeteccion >= 90 -> {
                println("🥈 ¡MUY BUENO! (${String.format("%.1f", porcentajeDeteccion)}%)")
                println("   ✅ Excelente detección de mayoría de casos")
                println("   🔧 Pequeños ajustes podrían mejorar aún más")
            }
            porcentajeDeteccion >= 80 -> {
                println("🥉 ¡BUENO! (${String.format("%.1f", porcentajeDeteccion)}%)")
                println("   ✅ Buena detección general")
                println("   🔧 Oportunidades de mejora en casos específicos")
            }
            porcentajeDeteccion >= 70 -> {
                println("⚠️ ACEPTABLE (${String.format("%.1f", porcentajeDeteccion)}%)")
                println("   🔧 Necesita optimización para casos complejos")
                println("   📈 Revisar patrones no detectados")
            }
            else -> {
                println("❌ NECESITA MEJORAS (${String.format("%.1f", porcentajeDeteccion)}%)")
                println("   🔧 Revisar implementación completa")
                println("   📈 Analizar casos problemáticos")
            }
        }
        
        // 🔍 ANÁLISIS DETALLADO POR CATEGORÍAS
        println("\n📊 ANÁLISIS POR CATEGORÍAS:")
        
        val conCantidad = resultados.count { it.cantidad != null }
        val sinCantidad = resultados.count { it.cantidad == null }
        val conUnidad = resultados.count { it.unidad != null }
        val conMarca = resultados.count { it.marcas.isNotEmpty() }
        
        println("   • Con cantidad explícita: $conCantidad")
        println("   • Sin cantidad (implícita): $sinCantidad")
        println("   • Con unidad detectada: $conUnidad")
        println("   • Con marca detectada: $conMarca")
        
        // 🎯 CASOS ESPECÍFICOS A VERIFICAR
        println("\n🔍 VERIFICACIÓN DE CASOS CRÍTICOS:")
        
        val casosEspeciales = listOf(
            "números pegados" to resultados.any { it.nombre.contains("sandias") || it.nombre.contains("tomates") },
            "decimales complejos" to resultados.any { (it.cantidad ?: 0.0) > 10 && (it.cantidad ?: 0.0) < 100 },
            "productos con marca" to (conMarca > 0),
            "múltiples en línea" to resultados.any { it.nombre.length > 20 },
            "unidades específicas" to (conUnidad > 0)
        )
        
        for ((caso, detectado) in casosEspeciales) {
            val status = if (detectado) "✅" else "❌"
            println("   $status $caso: ${if (detectado) "DETECTADO" else "NO DETECTADO"}")
        }
        
    } catch (e: Exception) {
        println("💥 ERROR DURANTE EL PROCESAMIENTO: ${e.message}")
        e.printStackTrace()
    }
    
    println("\n🎉 TEST DE 86 PRODUCTOS COMPLETADO")
    println("=".repeat(70))
}

