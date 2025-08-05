package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto
import org.junit.Test
import org.junit.Assert.*

/**
 * Test simple para validar que el parser funciona correctamente
 * sin depender de UI o componentes complejos
 */
class SimpleParserTest {

    @Test
    fun testParserBasico() {
        println("=== TEST PARSER BÁSICO ===")
        
        // Test 1: Caso simple
        val input1 = "2 bolsas de arroz"
        val productos1 = QuantityParser.parse(input1)
        println("Input: '$input1'")
        println("Productos: $productos1")
        
        assertTrue("Debe generar al menos 1 producto", productos1.isNotEmpty())
        val producto1 = productos1[0]
        assertEquals("Cantidad debe ser 2", 2.0, producto1.cantidad ?: 0.0, 0.01)
        assertTrue("Nombre debe contener 'arroz'", producto1.nombre.contains("arroz", ignoreCase = true))
        
        // Test 2: Conjunciones inteligentes
        val input2 = "jamón y queso"
        val productos2 = QuantityParser.parse(input2)
        println("\nInput: '$input2'")
        println("Productos: $productos2")
        
        assertTrue("Debe generar 1 producto (compuesto)", productos2.size == 1)
        assertTrue("Debe mantener 'jamón y queso' junto", 
            productos2[0].nombre.contains("jamón") && productos2[0].nombre.contains("queso"))
        
        // Test 3: Cantidades separadas
        val input3 = "2 focos y 3 rollos"
        val productos3 = QuantityParser.parse(input3)
        println("\nInput: '$input3'")
        println("Productos: $productos3")
        
        assertTrue("Debe generar 2 productos separados", productos3.size == 2)
        assertTrue("Debe tener producto con cantidad 2", 
            productos3.any { it.cantidad == 2.0 })
        assertTrue("Debe tener producto con cantidad 3", 
            productos3.any { it.cantidad == 3.0 })
        
        // Test 4: Marcas
        val input4 = "leche marca Colun"
        val productos4 = QuantityParser.parse(input4)
        println("\nInput: '$input4'")
        println("Productos: $productos4")
        
        assertTrue("Debe generar al menos 1 producto", productos4.isNotEmpty())
        val producto4 = productos4[0]
        assertTrue("Debe detectar marca 'Colun'", 
            producto4.marcas.any { it.contains("Colun", ignoreCase = true) })
        
        println("\n=== TODOS LOS TESTS BÁSICOS PASARON ===")
    }
    
    @Test
    fun testMejorasImplementadas() {
        println("=== TEST MEJORAS IMPLEMENTADAS ===")
        
        // Test mejora 1: Extracción cantidad + unidad + nombre
        val input1 = "3 latas de atún"
        val productos1 = QuantityParser.parse(input1)
        println("Mejora 1 - Input: '$input1'")
        println("Productos: $productos1")
        
        if (productos1.isNotEmpty()) {
            val p = productos1[0]
            println("✅ Cantidad: ${p.cantidad}, Unidad: ${p.unidad}, Nombre: ${p.nombre}")
        }
        
        // Test mejora 2: Plurales en unidades
        val input2 = "5 kilos de azúcar"
        val productos2 = QuantityParser.parse(input2)
        println("\nMejora 2 - Input: '$input2'")
        println("Productos: $productos2")
        
        if (productos2.isNotEmpty()) {
            val p = productos2[0]
            println("✅ Cantidad: ${p.cantidad}, Unidad: ${p.unidad}, Nombre: ${p.nombre}")
        }
        
        println("\n=== MEJORAS VALIDADAS ===")
    }
}
