package com.listafacilnueva.test

import com.listafacilnueva.parser.QuantityParser

class TestRapidoComido {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val texto = "1.2metros cable 2.bombilla grande 3.zapallo chino"
            println("Texto: '$texto'")
            
            val productos = QuantityParser.parse(texto)
            println("Productos encontrados: ${productos.size}")
            
            productos.forEach { producto ->
                println("- ${producto.cantidad} ${producto.nombre}")
            }
            
            if (productos.size == 3) {
                println("✅ ÉXITO: Se detectaron los 3 productos correctamente")
            } else {
                println("❌ ERROR: Se esperaban 3 productos, se encontraron ${productos.size}")
            }
        }
    }
}
