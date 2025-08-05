package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests para validar las mejoras sugeridas por el amigo del usuario:
 * 1. Extracción de cantidad + unidad + nombre separados ("2 bolsas de arroz")
 * 2. Conjunciones inteligentes ("jamón y queso" vs "2 focos y 3 rollos")
 * 3. Detección mejorada de marcas ("marca Coca-Cola")
 * 4. Plurales en unidades ("bolsas", "litros", "metros")
 */
class MejorasParserTest {

    private fun printAndAssert(input: String, expectedSize: Int, assertions: (List<Producto>) -> Unit) {
        println("--- INICIO TEST MEJORAS ---")
        println("Input: \"$input\"")
        val productos = QuantityParser.parse(input)
        println("Productos Parseados (${productos.size}):")
        productos.forEach { println("  - $it") }
        println("--- FIN TEST MEJORAS ---")
        assertEquals("El número de productos no coincide.", expectedSize, productos.size)
        assertions(productos)
    }

    @Test
    fun `test mejora 1 - extraccion cantidad unidad nombre separados`() {
        val input = "2 bolsas de arroz\n3 latas de atún\n1 metro de cable"
        printAndAssert(input, 3) { productos ->
            // "2 bolsas de arroz" → cantidad=2, unidad=bolsa, nombre=arroz
            assertTrue("Debe extraer 'bolsas' como unidad", 
                productos.any { it.cantidad == 2.0 && it.unidad == "bolsa" && it.nombre.contains("arroz") })
            
            // "3 latas de atún" → cantidad=3, unidad=lata, nombre=atún
            assertTrue("Debe extraer 'latas' como unidad", 
                productos.any { it.cantidad == 3.0 && it.unidad == "lata" && it.nombre.contains("atún") })
            
            // "1 metro de cable" → cantidad=1, unidad=m, nombre=cable
            assertTrue("Debe extraer 'metro' como unidad", 
                productos.any { it.cantidad == 1.0 && it.unidad == "m" && it.nombre.contains("cable") })
        }
    }

    @Test
    fun `test mejora 2 - conjunciones inteligentes`() {
        val input = "jamón y queso\n2 focos y 3 rollos\npan y mantequilla"
        printAndAssert(input, 4) { productos ->
            // "jamón y queso" → 1 producto compuesto (no dividir)
            assertTrue("'jamón y queso' debe ser 1 producto", 
                productos.any { it.nombre.contains("jamón y queso") || it.nombre.contains("jamón") && it.nombre.contains("queso") })
            
            // "2 focos y 3 rollos" → 2 productos separados (dividir por cantidades)
            assertTrue("Debe separar '2 focos'", 
                productos.any { it.cantidad == 2.0 && it.nombre.contains("focos") })
            assertTrue("Debe separar '3 rollos'", 
                productos.any { it.cantidad == 3.0 && it.nombre.contains("rollos") })
            
            // "pan y mantequilla" → 1 producto compuesto (no dividir)
            assertTrue("'pan y mantequilla' debe ser 1 producto", 
                productos.any { it.nombre.contains("pan y mantequilla") || it.nombre.contains("pan") && it.nombre.contains("mantequilla") })
        }
    }

    @Test
    fun `test mejora 3 - deteccion mejorada de marcas`() {
        val input = "marca Coca-Cola 2 latas de refresco\nleche marca Colun\n1 shampoo marca Head & Shoulders"
        printAndAssert(input, 3) { productos ->
            // "marca Coca-Cola 2 latas de refresco" → marca=Coca-Cola
            assertTrue("Debe detectar marca 'Coca-Cola'", 
                productos.any { it.marcas.contains("Coca-Cola") && it.nombre.contains("refresco") })
            
            // "leche marca Colun" → marca=Colun
            assertTrue("Debe detectar marca 'Colun'", 
                productos.any { it.marcas.contains("Colun") && it.nombre.contains("leche") })
            
            // "1 shampoo marca Head & Shoulders" → marca=Head & Shoulders
            assertTrue("Debe detectar marca 'Head & Shoulders'", 
                productos.any { it.marcas.contains("Head & Shoulders") && it.nombre.contains("shampoo") })
        }
    }

    @Test
    fun `test mejora 4 - plurales en unidades`() {
        val input = "5 kilos de azúcar\n2 litros de leche\n3 metros de cinta\n4 bolsas de pan"
        printAndAssert(input, 4) { productos ->
            // "5 kilos de azúcar" → unidad=kg
            assertTrue("'kilos' debe normalizarse a 'kg'", 
                productos.any { it.cantidad == 5.0 && it.unidad == "kg" && it.nombre.contains("azúcar") })
            
            // "2 litros de leche" → unidad=lt
            assertTrue("'litros' debe normalizarse a 'lt'", 
                productos.any { it.cantidad == 2.0 && it.unidad == "lt" && it.nombre.contains("leche") })
            
            // "3 metros de cinta" → unidad=m
            assertTrue("'metros' debe normalizarse a 'm'", 
                productos.any { it.cantidad == 3.0 && it.unidad == "m" && it.nombre.contains("cinta") })
            
            // "4 bolsas de pan" → unidad=bolsa
            assertTrue("'bolsas' debe normalizarse a 'bolsa'", 
                productos.any { it.cantidad == 4.0 && it.unidad == "bolsa" && it.nombre.contains("pan") })
        }
    }

    @Test
    fun `test casos complejos combinados`() {
        val input = "2 bolsas de arroz marca Gallo\njamón y queso para sandwich\n3 latas de atún y 1 botella de aceite"
        printAndAssert(input, 4) { productos ->
            // "2 bolsas de arroz marca Gallo" → cantidad=2, unidad=bolsa, nombre=arroz, marca=Gallo
            assertTrue("Caso complejo 1", 
                productos.any { it.cantidad == 2.0 && it.unidad == "bolsa" && it.nombre.contains("arroz") && it.marcas.contains("Gallo") })
            
            // "jamón y queso para sandwich" → 1 producto compuesto
            assertTrue("Caso complejo 2", 
                productos.any { it.nombre.contains("jamón") && it.nombre.contains("queso") })
            
            // "3 latas de atún y 1 botella de aceite" → 2 productos separados
            assertTrue("Caso complejo 3a", 
                productos.any { it.cantidad == 3.0 && it.unidad == "lata" && it.nombre.contains("atún") })
            assertTrue("Caso complejo 3b", 
                productos.any { it.cantidad == 1.0 && it.nombre.contains("aceite") })
        }
    }
}
