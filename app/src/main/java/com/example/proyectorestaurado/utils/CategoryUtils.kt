package com.example.proyectorestaurado.utils

object CategoryUtils {
    val categoryKeywords: Map<String, List<String>> = mapOf(
        "Frutas y Verduras" to listOf("tomate", "zanahoria", "papa", "sandía", "manzana", "plátano", "lechuga", "francesa", "albahaca", "choclo", "salmon"),
        "Carnes" to listOf("carne molida", "pollo", "bistec"),
        "Lácteos y Huevos" to listOf("leche", "queso", "yogur", "huevos", "mantequilla", "yemita"),
        "Panadería y Dulces" to listOf("pan", "galletas", "pastel", "leche asada"),
        "Despensa" to listOf("arroz", "fideos", "aceite", "azúcar", "sal", "tallarines", "atún", "jugo de naranja", "jugo naranja"),
        "Bebidas" to listOf("gaseosa", "jugo", "agua"),
        "Cuidado Personal" to listOf("champú", "jabón", "pasta de dientes", "cepillos", "desodorante", "calcetines", "focos", "pilas"),
        "Limpieza" to listOf("detergente", "cloro", "lavalozas", "papel higiénico", "esponjas", "servilletas")
    )
}
