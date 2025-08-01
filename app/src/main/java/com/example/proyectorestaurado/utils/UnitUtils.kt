package com.example.proyectorestaurado.utils

object UnitUtils {
    // Aquí van los sinónimos y utilidades de unidades
    val unitSynonyms = mapOf(
        "u" to listOf("u", "un", "uni", "unidad", "unidades", "pz", "pieza", "pzas", "pack", "paquete", "bolsa", "blister", "blíster", "caja", "frasco", "botella", "lata", "sobre", "tableta", "barra", "barras"),
        "kg" to listOf("kg", "kilo", "kilogramo", "kilogramos"),
        "g" to listOf("g", "gr", "gramo", "gramos"),
        "l" to listOf("l", "lt", "litro", "litros"),
        "ml" to listOf("ml", "cc", "mililitro", "mililitros"),
        "m" to listOf("m", "metro", "metros"),
        "cm" to listOf("cm", "centimetro", "centímetros", "centímetro", "centimetros"),
        "docena" to listOf("docena", "docenas"),
        "malla" to listOf("malla", "mallas")
    )
    // Extrae la unidad y el nombre limpio desde un string
    fun findUnitInString(input: String): Pair<String?, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Pair(null, "")
        // Busca la primera coincidencia de unidad conocida
        val trimmedNoAccent = TextNormalizationUtils.removeAccents(trimmed)
        for ((canonical, synonyms) in unitSynonyms) {
            for (syn in synonyms) {
                val synNoAccent = TextNormalizationUtils.removeAccents(syn)
                val regex = Regex("""\b$synNoAccent\b""", RegexOption.IGNORE_CASE)
                val match = regex.find(trimmedNoAccent)
                if (match != null) {
                    val name = trimmed.replace(Regex("""\b$syn\b""", RegexOption.IGNORE_CASE), "").replace("  ", " ").trim()
                    return Pair(canonical, name)
                }
            }
        }
        return Pair(null, trimmed)
    }
}
