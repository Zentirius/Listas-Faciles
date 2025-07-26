package com.example.proyectorestaurado

import com.example.proyectorestaurado.utils.QuantityParser
import org.junit.Test
import org.junit.Assert.*

class ComplexParsingTest {
    
    private val quantityParser = QuantityParser()
    
    @Test
    fun testSeisTomates() {
        println("=== Test: seis tomates ocho papas ===")
        val input = "seis tomates ocho papas"
        val result = quantityParser.parseMultipleItems(input)
        
        println("Input: '$input'")
        println("Results: ${result.size} items")
        result.forEachIndexed { index, item ->
            println("Item ${index + 1}: '${item.name}' (${item.quantity} ${item.unit ?: "u"})")
        }
        
        assertEquals("Should have 2 items", 2, result.size)
        assertEquals("First item should be Tomates", "Tomates", result[0].name)
        assertEquals("Second item should be Papas", "Papas", result[1].name)
    }
    
    @Test
    fun testComplexInput() {
        println("\n=== Test: Complex Input ===")
        val input = """1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
Zanahorias
1.5 litros de jugo de naranja,,,2 latas de atún."""
        
        println("Input:")
        println(input)
        println("\n--- Processing ---")
        
        // Simular exactamente lo que hace MainActivity
        val lines = input.split(Regex("[\n]+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        println("Lines after split: ${lines.size}")
        lines.forEachIndexed { index, line ->
            println("Line ${index + 1}: '$line'")
        }
        
        val allItems = mutableListOf<String>()
        
        lines.forEach { line ->
            println("\nProcessing line: '$line'")
            val parsedItems = quantityParser.parseMultipleItems(line)
            println("parseMultipleItems returned ${parsedItems.size} products")
            
            parsedItems.forEachIndexed { index, parsed ->
                println("  Product ${index + 1}: '${parsed.name}' (${parsed.quantity} ${parsed.unit ?: "u"})")
                allItems.add("${parsed.name} (${parsed.quantity} ${parsed.unit ?: "u"})")
            }
        }
        
        println("\n=== Final Results ===")
        println("Total items: ${allItems.size}")
        allItems.forEachIndexed { index, item ->
            println("${index + 1}. $item")
        }
        
        // Verificar que se detectaron múltiples productos
        assertTrue("Should have more than 5 items", allItems.size > 5)
    }
    
    @Test
    fun testIndividualLines() {
        println("\n=== Test: Individual Lines ===")
        
        val testCases = listOf(
            "2 kg de tomates, 1 lechuga y pan (el más barato)",
            "6 huevos, 1 litro de leche no muy cara",
            "media docena de plátanos. 3 manzanas",
            "500gr de carne molida 1 paquete de espaguetis",
            "1.5 litros de jugo de naranja,,,2 latas de atún"
        )
        
        testCases.forEach { testCase ->
            println("\nTesting: '$testCase'")
            val result = quantityParser.parseMultipleItems(testCase)
            println("Results: ${result.size} items")
            result.forEachIndexed { index, item ->
                println("  ${index + 1}. ${item.name} (${item.quantity} ${item.unit ?: "u"})")
            }
        }
    }
}