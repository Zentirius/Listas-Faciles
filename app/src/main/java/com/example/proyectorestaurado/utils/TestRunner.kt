package com.example.proyectorestaurado.utils

import android.util.Log

object TestRunner {

    private const val TAG = "PARSER_TEST"

    private val testInputs = listOf(
        // Casos simples
        "1. 2 kg de tomates, 1 lechuga y pan (el más barato)",
        "6 huevos, 1 litro de leche no muy cara",
        "media docena de plátanos. 3 manzanas",
        "500gr de carne molida 1 paquete de espaguetis",
        "Zanahorias",
        "1.5 litros de jugo de naranja,,,2 latas de atún.",
        "1.1papa mediana2.sandia grande",
        "1.salmon 1kg",
        "2.zanaorias",
        // Números como palabras
        "cinco tomates",
        "ocho zanahorias",
        "seis tomates ocho papas",
        // Casos con marcas y notas complejas
        "crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)",
        "Shampoo anticaspa 1.",
        "Jabón líquido 2 barato.",
        "3 rollos papel higiénico preguntar...cuál es mejor...",
        "Detergente en polvo más barato en caja",
        "Esponjas 5 en oferta.",
        "1bolsa arroz",
        "1 aceite marca chef o el más económico",
        // Combinados y caóticos
        "champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines",
        "2. 3 kg de azúcar, 1 sal y vinagre (el más barato)",
        "8 pilas, 2 metros de cable no muy caro",
        "media docena de velas. 4 focos",
        "750ml de alcohol 1 paquete de algodón",
        "Papel aluminio",
        "2.5 metros de cinta adhesiva,,,3 tubos de pegamento.",
        "1.2metros cable2.bombilla grande",
        "1.shampoo 500ml",
        "3.desodorantes",
        "siete velas",
        "diez pilas",
        "cinco focos ocho rollos",
        // Casos de palabras pegadas (sugerencia 1)
        "lechepanhuevos",
        "2kgTomates1litroLeche"
    )

    fun runIntegrationTests() {
        Log.i(TAG, "--- INICIANDO PRUEBAS DE INTEGRACIÓN DEL PARSER ---")
        testInputs.forEach { input ->
            try {
                val parsedItems = QuantityParser.parseMultipleItems(input)
                Log.d(TAG, "Input: \"$input\"")
                if (parsedItems.isNotEmpty()) {
                    parsedItems.forEach { item ->
                        Log.d(TAG, "  -> Parsed: $item")
                    }
                } else {
                    Log.w(TAG, "  -> No se pudo parsear o lista vacía.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "  -> ERROR al parsear: ${e.message}", e)
            }
        }
        Log.i(TAG, "--- PRUEBAS DE INTEGRACIÓN DEL PARSER FINALIZADAS ---")
    }
}
