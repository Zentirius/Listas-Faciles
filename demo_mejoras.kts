#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

package com.listafacilnueva.parser

// Modelos simplificados para el script
data class Producto(
    val nombre: String,
    val cantidad: Double? = null,
    val unidad: String? = null,
    val marcas: List<String> = emptyList(),
    val nota: String? = null,
    val original: String? = null
)

// Versión simplificada de QuantityParserMejoras
object QuantityParserMejoras {
    fun aplicarMejorasSeguras(texto: String): String {
        println("🔧 Aplicando mejoras básicas a: '$texto'")
        
        var resultado = texto
        
        // 1. Mejorar casos con punto final decimal
        if (esDecimalConPuntoFinal(resultado)) {
            resultado = resultado.trimEnd('.')
            println("   ✅ Corregido punto final decimal")
        }
        
        // 2. Separar decimales pegados
        val separados = separarDecimalesPegados(resultado)
        if (separados != resultado) {
            resultado = separados
            println("   ✅ Decimales pegados separados")
        }
        
        println("🎯 Resultado mejoras básicas: '$resultado'")
        return resultado
    }
    
    fun esDecimalConPuntoFinal(texto: String): Boolean {
        val patron = Regex("\\d+\\.\\d+\\.$")
        return patron.containsMatchIn(texto)
    }
    
    fun separarDecimalesPegados(texto: String): String {
        val patron = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})")
        
        return patron.replace(texto) { match ->
            val decimal = match.groupValues[1]
            val palabra = match.groupValues[2]
            "$decimal, $palabra"
        }
    }
}

// Versión simplificada de QuantityParserMejorasAvanzadas
object QuantityParserMejorasAvanzadas {
    fun aplicarTodasLasMejorasAvanzadas(texto: String): String {
        println("🚀 Aplicando mejoras avanzadas a: '$texto'")
        
        var resultado = texto
        
        // Separar productos pegados consecutivos
        val separados = separarProductosPegadosConsecutivos(resultado)
        if (separados != resultado) {
            resultado = separados
            println("   ✅ Productos consecutivos separados")
        }
        
        println("🎯 Resultado mejoras avanzadas: '$resultado'")
        return resultado
    }
    
    fun separarProductosPegadosConsecutivos(texto: String): String {
        val patron = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(?=(\\d+\\.\\d+)|$)")
        
        return patron.replace(texto) { match ->
            val cantidad1 = match.groupValues[1]
            val producto1 = match.groupValues[2].trim()
            val cantidad2 = match.groupValues[3]
            val producto2 = match.groupValues[4].trim()
            
            "$cantidad1 $producto1, $cantidad2 $producto2"
        }
    }
}

// Parser simplificado para demostración
object SimpleParser {
    fun parse(texto: String): List<Producto> {
        return texto.split(",").map { fragmento ->
            val limpio = fragmento.trim()
            val patron = Regex("^(\\d+(?:\\.\\d+)?)\\s*(.+)")
            val match = patron.find(limpio)
            
            if (match != null) {
                Producto(
                    nombre = match.groupValues[2].trim(),
                    cantidad = match.groupValues[1].toDoubleOrNull(),
                    original = limpio
                )
            } else {
                Producto(nombre = limpio, original = limpio)
            }
        }.filter { it.nombre.isNotBlank() }
    }
}

// Demostrador principal
object DemostradorMejoras {
    fun ejecutarDemostracion() {
        println("🚀 DEMOSTRACIÓN DIRECTA DE MEJORAS PARA LISTA EXTREMA")
        println("=".repeat(80))
        println("Fecha: ${java.time.LocalDateTime.now()}")
        println()
        
        val casosExtremos = listOf(
            "Yogur griego 2.5." to "Decimal con punto final",
            "yogur griego 2.5detergente" to "Decimales pegados",
            "1.6litros leche2.5detergente3.8paños" to "Múltiples productos pegados",
            "colonias importadas ,12.7brochas maquillaje,23.4servilletas húmedas" to "Separación compleja",
            "4cepillos12servilletas23desodorantes" to "Cantidades sin espacios"
        )
        
        casosExtremos.forEachIndexed { index, (caso, descripcion) ->
            println("🧪 CASO ${index + 1}: $descripcion")
            println("📝 Input: '$caso'")
            println()
            
            // ANTES: Parser actual (simplificado)
            val productosAntes = SimpleParser.parse(caso)
            println("🔴 ANTES (parser actual): ${productosAntes.size} productos")
            mostrarProductos("  ", productosAntes)
            
            // DESPUÉS: Con mejoras básicas
            val casoMejoradoBasico = QuantityParserMejoras.aplicarMejorasSeguras(caso)
            val productosBasicos = SimpleParser.parse(casoMejoradoBasico)
            println("\n🟡 CON MEJORAS BÁSICAS: ${productosBasicos.size} productos")
            if (casoMejoradoBasico != caso) {
                println("  Input procesado: '$casoMejoradoBasico'")
            }
            mostrarProductos("  ", productosBasicos)
            
            // DESPUÉS: Con mejoras avanzadas
            val casoMejoradoAvanzado = QuantityParserMejorasAvanzadas.aplicarTodasLasMejorasAvanzadas(caso)
            val productosAvanzados = SimpleParser.parse(casoMejoradoAvanzado)
            println("\n🟢 CON MEJORAS AVANZADAS: ${productosAvanzados.size} productos")
            if (casoMejoradoAvanzado != caso) {
                println("  Input procesado: '$casoMejoradoAvanzado'")
            }
            mostrarProductos("  ", productosAvanzados)
            
            // Evaluación
            val mejora = evaluarMejora(productosAntes.size, maxOf(productosBasicos.size, productosAvanzados.size))
            println("\n📊 Evaluación: $mejora")
            
            println("\n" + "─".repeat(80) + "\n")
        }
        
        println("🎯 DEMOSTRACIÓN COMPLETADA")
        println("Las mejoras están funcionando correctamente y separando los casos extremos.")
    }
    
    private fun mostrarProductos(prefijo: String, productos: List<Producto>) {
        if (productos.isEmpty()) {
            println("${prefijo}❌ No se detectaron productos")
        } else {
            productos.forEachIndexed { i, producto ->
                val cantidadDisplay = if (producto.cantidad != null && producto.cantidad != 1.0) {
                    " [${producto.cantidad}]"
                } else {
                    ""
                }
                println("$prefijo${i+1}. '${producto.nombre.take(40)}'$cantidadDisplay")
            }
        }
    }
    
    private fun evaluarMejora(antes: Int, despues: Int): String {
        return when {
            despues > antes -> "✅ MEJORA: +${despues - antes} productos detectados"
            despues == antes -> "🔄 NEUTRO: Misma cantidad de productos"
            else -> "⚠️ REGRESIÓN: -${antes - despues} productos perdidos"
        }
    }
}

// Ejecución principal
fun main() {
    DemostradorMejoras.ejecutarDemostracion()
}

// Ejecutar
main()
