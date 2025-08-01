package com.example.proyectorestaurado.utils

object BrandNoteUtils {
    val notePhrases = listOf(
        "o el más barato", "o el económico", "el más barato", "el económico",
        "o el más económico", "o el más barato posible"
    )
    val noteKeywords = listOf(
        "barato", "económico", "oferta", "preguntar", "mejor", "más barato",
        "económica", "económico", "no muy cara"
    )

    @JvmStatic
    fun cleanNote(note: String): String {
        // Elimina puntos suspensivos, repeticiones y relleno, deja solo palabras útiles
        val cleaned = note
            .replace(Regex("""[.]{2,}"""), " ") // reemplaza varios puntos por espacio
            .replace(Regex("""[^\wáéíóúñ\s,]+"""), " ") // elimina símbolos raros
            .replace(Regex("""\s+"""), " ") // colapsa espacios
            .trim()
        // Solo deja palabras clave útiles
        val palabrasClave = listOf("preguntar", "cual", "en oferta", "barato", "más barato", "oferta", "económico")
        return cleaned.split(" ").filter { palabra ->
        palabrasClave.any { kw -> TextNormalizationUtils.removeAccents(palabra) == TextNormalizationUtils.removeAccents(kw) } || palabra.length > 3
    }.joinToString(" ").trim()
    }

    fun separateBrandsAndNotes(brandText: String): Pair<List<String>, List<String>> {
        val noteCandidates = mutableListOf<String>()
        var cleanedBrandText = brandText

        // Extrae frases completas a notes
        notePhrases.forEach { phrase ->
            val idx = cleanedBrandText.indexOf(phrase, ignoreCase = true)
            if (idx != -1) {
                val cleaned = cleanNote(phrase)
                if (cleaned.isNotBlank()) noteCandidates.add(cleaned)
                cleanedBrandText = cleanedBrandText.replace(phrase, "", ignoreCase = true)
            }
        }
        // Caso especial: marcas compuestas conocidas
        val marcasCompuestas = listOf("fruto del maipo", "minuto verde", "fruto del maipo o minuto verde")
        val compuesto = marcasCompuestas.find { cleanedBrandText.lowercase().contains(it) }
        val brandCandidates = if (compuesto != null) {
            listOf(compuesto)
        } else {
            cleanedBrandText.split(Regex("\\s+/(\\s+|$)|\\s+o\\s+|\\s+u\\s+")).map { it.trim() }
                .filter { it.isNotEmpty() && it.isNotBlank() && !notePhrases.any { phrase -> it.equals(phrase, ignoreCase = true) } }
        }
        val (brands, notesExtra) = brandCandidates.partition { part ->
            val lower = part.lowercase()
            !noteKeywords.any { lower.contains(it) }
        }
        return Pair(brands, noteCandidates + notesExtra.filter { it.isNotBlank() })
    }
}
