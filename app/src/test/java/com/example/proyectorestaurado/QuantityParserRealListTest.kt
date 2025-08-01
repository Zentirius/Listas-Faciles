package com.example.proyectorestaurado

import com.example.proyectorestaurado.utils.QuantityParser
import com.example.proyectorestaurado.utils.ParseMode
import org.junit.Assert.assertTrue
import org.junit.Test

class QuantityParserRealListTest {
    // Test principal de lista real de tensión
    @Test
    fun testListaTensionCompleta() {
        val listaTension = """
1. 5 tomates
2. 2 cebollas
3. 1 lechuga
4. 1 pan de molde
5. 12 huevos
6. 1 kg arroz
7. 1 kg azúcar
8. 1 litro leche
9. 1 mantequilla
10. 1 queso
11. 1 paquete fideos
12. 1 café
13. 1 caja té
14. 1 mermelada
15. 1 aceite
16. 1 sal
17. 1 pimienta
""".trimIndent()
        val items = QuantityParser.parseMultipleItems(listaTension, ParseMode.LIBRE)
        println("Productos detectados: ${items.size}")
        items.forEach { println(it) }
        // Esperamos al menos 10 productos válidos (ajustar según lista real)
        assertTrue("Deben detectarse al menos 10 productos", items.size >= 10)
    }
}
