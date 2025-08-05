package com.listafacilnueva.parser

object ParserUtils {
    val palabrasNumericas = mapOf(
        "uno" to 1, "una" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4,
        "cinco" to 5, "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9, "diez" to 10,
        "media docena" to 6, "una docena" to 12, "medio kilo" to 0.5, "un cuarto" to 0.25
    )

    val unidades = setOf(
        "kg", "kilo", "kilos", "g", "gr", "gramo", "gramos", 
        "litro", "litros", "l", "lt", "ml", "mililitro", "mililitros",
        "malla", "mallas", "bolsa", "bolsas", "bola", "bolas",
        "unidad", "unidades", "u", "un", "ud", "cc", 
        "paquete", "paquetes", "docena", "docenas", 
        "lata", "latas", "metro", "metros", "m"
    )

    // Función mejorada para detectar unidades con plurales
    fun esUnidadConocida(unidad: String): Boolean {
        val base = unidad.lowercase().removeSuffix("s")
        val unidadesBase = setOf(
            "kg", "kilo", "gr", "gramo", "litro", "lt", "ml", "mililitro",
            "malla", "bolsa", "bola", "unidad", "un", "ud", "paquete", 
            "docena", "lata", "metro", "m"
        )
        return base in unidadesBase || unidad.lowercase() in unidades
    }

    val palabrasIrrelevantes = setOf(
        "de", "del", "la", "las", "el", "los", "un", "una", "unos", "unas",
        "y", "o", "con", "sin", "para", "por", "a",
        "comprar", "lista", "supermercado", "super", "mandado",
        "favor", "anotar", "apuntar", "recordar",
        "marca", "tipo", "clase", "oferta", "promo",
        "si", "hay", "cuando", "puedas", "despues"
    )

    fun normalizarUnidad(unidad: String): String {
        return when (unidad.lowercase().removeSuffix("s")) {
            "bolsa", "bolsas" -> "bolsa"  
            "bola", "bolas" -> "bolsa"     
            "metros", "metro" -> "m"
            "litros", "litro" -> "lt"
            "kilos", "kilo" -> "kg"
            "gramos", "gramo", "gr" -> "gr"
            "lata", "latas" -> "lata"
            "paquete", "paquetes" -> "paquete"
            "caja", "cajas" -> "caja"
            "malla", "mallas" -> "malla"
            "docena", "docenas" -> "docena"
            "tubo", "tubos" -> "tubo"
            "rollo", "rollos" -> "rollo"
            else -> unidad.lowercase().removeSuffix("s")
        }
    }

    fun esNumero(s: String): Boolean {
        return s.trim().replace(',', '.').toDoubleOrNull() != null
    }

    // Marcas conocidas para nunca crear productos independientes con estos nombres
    val marcasConocidas = setOf(
        "minuto", "minuto verde", "frutos del maipo", "frutos", "maipo", "colun", "soprole", "nestle", "watts", "quillayes", "loncoleche", "parmalat", "regimel", "san jorge", "super pollo", "ariztia", "pf", "costa", "evercrisp", "lays", "marinela", "ideal", "bimbo", "marraqueta", "tía rosa", "maruchan", "maggi", "carozzi", "lucchetti", "trattoria", "luchetti", "carrefour", "jumbo", "lider", "santa isabel", "unimarc", "tottus", "acuenta", "marcas blancas", "marcablanca", "marca blanca"
    )
}
