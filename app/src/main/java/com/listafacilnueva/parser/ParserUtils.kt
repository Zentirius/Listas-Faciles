package com.listafacilnueva.parser

/**
 * 🛠️ UTILIDADES DEL PARSER
 * 
 * Funciones auxiliares y utilidades para el procesamiento
 * de texto y validación de unidades.
 */
object ParserUtils {

    /**
     * 📏 UNIDADES CONOCIDAS
     */
    val unidades = setOf(
        // peso
        "kg", "kilogramos", "kilogramo", "kilos", "kilo", "kgs",
        "g", "gramos", "gramo", "gr", "grs",
        // volumen
        "l", "litros", "litro", "lt", "lts",
        "ml", "mililitros", "mililitro",
        // longitud
        "m", "metros", "metro", "mtr", "mtrs",
        "cm", "centimetros", "centímetros", "centimetro", "centímetro",
        "mm", "milimetros", "milímetros", "milimetro", "milímetro",
        // envases
        "bolsa", "bolsas", "paquete", "paquetes", "pack", "packs", "paq", "paq.", "pqt",
        // unidades
        "unidad", "unidades", "ud", "uds",
        // otros
        "docena", "docenas", "doc",
        "media", "medio",
        "cuarto", "cuartos",
        "mitad", "mitades",
        "malla", "mallas",
        // otros posibles
        "cc"
    )

    /**
     * 🏷️ MARCAS CONOCIDAS
     */
    val marcasConocidas = setOf(
        "colgate", "sensodyne", "parodontax", "hershey's", "borges", "modena",
        "centrum", "kirkland", "frutos del maipo", "minuto verde", "chef",
        "yemita", "hershey"
    )

    /**
     * 🔢 PALABRAS NUMÉRICAS
     */
    val palabrasNumericas = mapOf(
        "medio" to 0.5, "media" to 0.5,
        "un" to 1.0, "una" to 1.0, "uno" to 1.0,
        "dos" to 2.0, "tres" to 3.0, "cuatro" to 4.0, "cinco" to 5.0,
        "seis" to 6.0, "siete" to 7.0, "ocho" to 8.0, "nueve" to 9.0, "diez" to 10.0,
        "once" to 11.0, "doce" to 12.0, "trece" to 13.0, "catorce" to 14.0, "quince" to 15.0,
        "dieciséis" to 16.0, "diecisiete" to 17.0, "dieciocho" to 18.0, "diecinueve" to 19.0, 
        "veinte" to 20.0, "veintiuno" to 21.0, "veintidós" to 22.0, "veintitrés" to 23.0,
        "media docena" to 6.0, "una docena" to 12.0, "medio kilo" to 0.5, "un cuarto" to 0.25
    )

    /**
     * ✅ VALIDADOR DE UNIDADES CONOCIDAS
     */
    fun esUnidadConocida(unidad: String): Boolean {
        return unidades.contains(unidad.lowercase())
    }

    /**
     * 🔧 NORMALIZADOR DE UNIDADES
     */
    fun normalizarUnidad(unidad: String): String {
        return when (unidad.lowercase()) {
            "kg", "kilogramos", "kilogramo", "kilos", "kilo", "kgs" -> "kg"
            "g", "gramos", "gramo", "gr", "grs" -> "g"
            "l", "litros", "litro", "lt", "lts" -> "l"
            "ml", "mililitros", "mililitro" -> "ml"
            "m", "metros", "metro", "mtr", "mtrs" -> "m"
            "cm", "centimetros", "centímetros", "centimetro", "centímetro" -> "cm"
            "mm", "milimetros", "milímetros", "milimetro", "milímetro" -> "mm"
            "bolsa", "bolsas" -> "bolsa"
            "paquete", "paquetes", "pack", "packs", "paq", "paq.", "pqt" -> "paquete"
            "unidad", "unidades", "ud", "uds" -> "unidad"
            "docena", "docenas", "doc" -> "docena"
            "media", "medio" -> "medio"
            "cuarto", "cuartos" -> "cuarto"
            "mitad", "mitades" -> "mitad"
            "malla", "mallas" -> "malla"
            "cc" -> "cc"
            else -> unidad.lowercase()
        }
    }

    /**
     * 🔢 NORMALIZADOR DE NÚMEROS EN PALABRAS
     */
    fun normalizarNumeros(texto: String): String {
        var resultado = texto
        
        // CORRECCIÓN ESPECÍFICA: "media docena" = 6
        resultado = resultado.replace(Regex("\\bmedia\\s+docena\\b", RegexOption.IGNORE_CASE), "6")
        
        // Aplicar otras normalizaciones
        for ((palabra, numero) in palabrasNumericas) {
            resultado = resultado.replace(Regex("\\b$palabra\\b", RegexOption.IGNORE_CASE), numero.toString())
        }
        
        return resultado
    }
}
