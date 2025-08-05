package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto
import org.junit.Test
import org.junit.Assert.*

/**
 * Test específico para validar la mejora sugerida por el amigo:
 * Manejo de casos como "1.2metros cable2.bombilla grande"
 */
class MejoraAmigoTest {

    @Test
    fun testCasoComplejo_1punto2metrosCable2bombillaGrande() {
        println("=== TEST MEJORA DEL AMIGO ===")
        
        // Caso específico mencionado por el amigo
        val input = "1.2metros cable2.bombilla grande"
        println("Input: '$input'")
        
        val productos = QuantityParser.parse(input)
        println("Productos generados: ${productos.size}")
        
        for ((i, producto) in productos.withIndex()) {
            println("Producto ${i+1}:")
            println("  Nombre: '${producto.nombre}'")
            println("  Cantidad: ${producto.cantidad}")
            println("  Unidad: '${producto.unidad}'")
            println("  Original: '${producto.original}'")
        }
        
        // Validaciones según la explicación del amigo
        assertTrue("Debe generar exactamente 2 productos", productos.size == 2)
        
        // Producto 1: "2 metros cable"
        val producto1 = productos.find { it.nombre.contains("cable", ignoreCase = true) }
        assertNotNull("Debe existir producto con 'cable'", producto1)
        producto1?.let {
            assertEquals("Cantidad debe ser 2.0", 2.0, it.cantidad ?: 0.0, 0.01)
            assertTrue("Unidad debe ser metros o m", 
                it.unidad?.contains("metro", ignoreCase = true) == true || it.unidad == "m")
        }
        
        // Producto 2: "bombilla grande"
        val producto2 = productos.find { it.nombre.contains("bombilla", ignoreCase = true) }
        assertNotNull("Debe existir producto con 'bombilla'", producto2)
        producto2?.let {
            assertTrue("Nombre debe contener 'grande'", 
                it.nombre.contains("grande", ignoreCase = true))
        }
        
        println("✅ Test de mejora del amigo PASÓ correctamente")
    }
    
    @Test
    fun testVariacionesDelCasoComplejo() {
        println("\n=== TEST VARIACIONES DEL CASO COMPLEJO ===")
        
        val testCases = listOf(
            "1.3kg azucar2.aceite",
            "1.5litros leche2.pan integral", 
            "1.2metros cable2.bombilla grande",
            "1.4kilos arroz2.frijoles negros"
        )
        
        for (testCase in testCases) {
            println("\nTesting: '$testCase'")
            val productos = QuantityParser.parse(testCase)
            println("Productos: ${productos.size}")
            
            // Debe generar 2 productos para cada caso
            assertTrue("'$testCase' debe generar 2 productos", productos.size == 2)
            
            productos.forEachIndexed { i, producto ->
                println("  ${i+1}. ${producto.nombre} (${producto.cantidad} ${producto.unidad})")
            }
        }
        
        println("✅ Todas las variaciones funcionan correctamente")
    }
    
    @Test
    fun testTrazabilidadConOriginal() {
        println("\n=== TEST TRAZABILIDAD CON .original ===")
        
        val input = "1.2metros cable2.bombilla grande"
        val productos = QuantityParser.parse(input)
        
        println("Input original: '$input'")
        for (producto in productos) {
            println("Producto: '${producto.nombre}' -> Original: '${producto.original}'")
            
            // Validar que .original conserva trazabilidad
            assertNotNull("Campo .original no debe ser null", producto.original)
            assertTrue("Campo .original no debe estar vacío", producto.original?.isNotBlank() == true)
        }
        
        println("✅ Trazabilidad con .original funciona correctamente")
    }
}
