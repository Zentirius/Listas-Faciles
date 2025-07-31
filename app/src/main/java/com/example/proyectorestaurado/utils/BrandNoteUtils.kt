package com.example.proyectorestaurado.utils

object BrandNoteUtils {
    private val notePhrases = listOf(
        "o el más barato", "o el económico", "el más barato", "el económico",
        "o el más económico", "o el más barato posible"
    )
    private val noteKeywords = listOf(
        "barato", "económico", "oferta", "preguntar", "mejor", "más barato",
        "económica", "económico", "no muy cara"
    )

    fun separateBrandsAndNotes(brandText: String): Pair<List<String>, List<String>> {
        val noteCandidates = mutableListOf<String>()
        var cleanedBrandText = brandText
        // Extrae frases completas a notes
        notePhrases.forEach { phrase ->
            val idx = cleanedBrandText.indexOf(phrase, ignoreCase = true)
            if (idx != -1) {
                noteCandidates.add(phrase.trim())
                cleanedBrandText = cleanedBrandText.replace(phrase, "", ignoreCase = true)
            }
        }
        // Split solo por /, o, u si hay espacios alrededor
        val brandCandidates = cleanedBrandText.split(Regex("\\s+/(\\s+|$)|\\s+o\\s+|\\s+u\\s+")).map { it.trim() }
            .filter { it.isNotEmpty() && it.isNotBlank() && !notePhrases.any { phrase -> it.equals(phrase, ignoreCase = true) } }
        val (brands, notesExtra) = brandCandidates.partition { part ->
            val lower = part.lowercase()
            !noteKeywords.any { lower.contains(it) }
        }
        return Pair(brands, noteCandidates + notesExtra.filter { it.isNotBlank() })
    }
}
