package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

/**
 * 🚀 DEMOSTRACIÓN DIRECTA: MEJORAS EN ACCIÓN
 * 
 * Este objeto permite probar directamente las mejoras sin depender del framework de testing
 */
object DemostradorMejoras {
    
    fun ejecutarDemostracion() {
        println("🚀 DEMOSTRACIÓN DIRECTA DE MEJORAS PARA LISTA EXTREMA")
        println("=".repeat(80))
        println("Fecha: ${java.time.LocalDateTime.now()}")
        println()
        
        // Casos extremos reales de la lista
        val casosExtremos = mapOf(
            "Yogur griego 2.5." to "Decimal con punto final",
            "1.8bolsas quinoa orgánica" to "Decimal pegado sin espacio",
            "Esponjas abrasivas 11.2 en combo." to "Decimal con punto en el medio",
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas,8.9 desodorantes roll-on 15.6 medias compresión" to "Caso híbrido súper complejo",
            "2.7metros cuerda náutica 3.9martillo de carpintero 5.2clavos de acero inoxidable" to "Productos técnicos con decimales",
            "1.6litros leche descremada2.5detergente en polvo3.8paños de limpieza multiuso" to "Múltiples productos pegados",
            "2.1 cepillos dentales eléctricos preguntar...cual tiene mejor batería..." to "Pregunta intercalada",
            "fragancia unisex premium ,18.9pinceles artísticos,31.7pañuelos desechables,13.2 antitranspirantes clínicos 21.4 calcetines térmicos" to "Lista híbrida con producto final sin coma"
        )
        
        var casosExitosos = 0
        var casosTotales = 0
        
        for ((caso, descripcion) in casosExtremos) {
            casosTotales++
            println("🔍 CASO #$casosTotales: $descripcion")
            println("📝 Input: '$caso'")
            println("-".repeat(70))
            
            try {
                // ANTES: Parser actual
                val productosAntes = QuantityParser.parse(caso)
                println("🔴 ANTES (parser actual): ${productosAntes.size} productos")
                mostrarProductos("  ", productosAntes)
                
                // DESPUÉS: Con mejoras básicas
                val casoMejoradoBasico = QuantityParserMejoras.aplicarMejorasSeguras(caso)
                val productosBasicos = QuantityParser.parse(casoMejoradoBasico)
                println("\n🟡 CON MEJORAS BÁSICAS: ${productosBasicos.size} productos")
                if (casoMejoradoBasico != caso) {
                    println("  Input procesado: '$casoMejoradoBasico'")
                }
                mostrarProductos("  ", productosBasicos)
                
                // DESPUÉS: Con mejoras avanzadas
                val casoMejoradoAvanzado = QuantityParserMejorasAvanzadas.aplicarTodasLasMejorasAvanzadas(caso)
                println("\n🟢 CON MEJORAS AVANZADAS:")
                if (casoMejoradoAvanzado != caso) {
                    println("  Input procesado: '$casoMejoradoAvanzado'")
                }
                
                // Manejar separador especial si existe
                val productosAvanzados = if (casoMejoradoAvanzado.contains(" | ")) {
                    val fragmentos = casoMejoradoAvanzado.split(" | ").filter { it.trim().isNotBlank() }
                    val todosProductos = mutableListOf<Producto>()
                    for (fragmento in fragmentos) {
                        todosProductos.addAll(QuantityParser.parse(fragmento.trim()))
                    }
                    todosProductos
                } else {
                    QuantityParser.parse(casoMejoradoAvanzado)
                }
                
                println("  Productos generados: ${productosAvanzados.size}")
                mostrarProductos("  ", productosAvanzados)
                
                // EVALUACIÓN
                val mejoro = productosAvanzados.size > productosAntes.size ||
                           (productosAvanzados.size == productosAntes.size && 
                            productosAvanzados.any { it.cantidad != null && it.cantidad != 1.0 })
                
                if (mejoro) {
                    println("\n  ✅ ÉXITO: Mejora detectada")
                    casosExitosos++
                } else {
                    println("\n  ⚠️  MANTUVO: Sin mejora significativa")
                }
                
            } catch (e: Exception) {
                println("\n  ❌ ERROR: ${e.message}")
            }
            
            println("\n" + "=".repeat(80) + "\n")
        }
        
        // RESUMEN FINAL
        val porcentajeExito = (casosExitosos * 100) / casosTotales
        println("🏆 RESUMEN FINAL DE LA DEMOSTRACIÓN")
        println("=".repeat(50))
        println("📊 Casos exitosos: $casosExitosos de $casosTotales ($porcentajeExito%)")
        
        when {
            porcentajeExito >= 80 -> {
                println("🥇 EXCELENTE: Las mejoras funcionan muy bien")
                println("   La mayoría de casos extremos ahora se procesan correctamente")
            }
            porcentajeExito >= 60 -> {
                println("🥈 BUENO: Las mejoras ayudan significativamente")  
                println("   Más de la mitad de los casos extremos mejoraron")
            }
            porcentajeExito >= 40 -> {
                println("🥉 ACEPTABLE: Las mejoras tienen efecto parcial")
                println("   Algunos casos extremos mejoraron, otros necesitan más trabajo")
            }
            else -> {
                println("🔧 NECESITA TRABAJO: Las mejoras requieren refinamiento")
                println("   Los casos extremos siguen siendo desafiantes")
            }
        }
        
        println("\n💡 CONCLUSIÓN:")
        println("   Las mejoras implementadas abordan varios de los problemas")
        println("   más comunes en la lista extremadamente desafiante.")
        println("   Los casos que no mejoran requieren análisis más específico.")
    }
    
    private fun mostrarProductos(prefijo: String, productos: List<Producto>) {
        if (productos.isEmpty()) {
            println("${prefijo}❌ No se generaron productos")
        } else {
            productos.forEachIndexed { i, producto ->
                val cantidadDisplay = if (producto.cantidad != null && producto.cantidad != 1.0) {
                    " [${producto.cantidad}${producto.unidad ?: ""}]"
                } else {
                    ""
                }
                println("$prefijo${i+1}. '${producto.nombre.take(40)}'$cantidadDisplay")
            }
        }
    }
}

/**
 * Función main para ejecutar el demostrador directamente
 */
fun main() {
    DemostradorMejoras.ejecutarDemostracion()
}
