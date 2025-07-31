package com.example.proyectorestaurado.utils.brand

object BrandAndNoteExtractor {
    fun separateBrandsAndNotes(brandText: String): Pair<List<String>, List<String>> {
        val notePhrases = listOf("o el más barato", "o el económico", "el más barato", "el económico", "o el más económico", "o el más barato posible")
        val noteCandidates = mutableListOf<String>()
        var cleanedBrandText = brandText
        notePhrases.forEach { phrase ->
            val idx = cleanedBrandText.indexOf(phrase, ignoreCase = true)
            if (idx != -1) {
                noteCandidates.add(phrase.trim())
                cleanedBrandText = cleanedBrandText.replace(phrase, "", ignoreCase = true)
            }
        }
        val brandCandidates = cleanedBrandText.split(Regex("\\s+/\\s+|\\s+o\\s+|\\s+u\\s+")).map { it.trim() }.filter { it.isNotEmpty() }
        val (brands, notesExtra) = brandCandidates.partition { part ->
            val lower = part.lowercase()
            !listOf("barato", "económico", "oferta", "preguntar", "mejor", "más barato", "económica", "económico", "no muy cara").any { lower.contains(it) }
        }
        return Pair(brands, noteCandidates + notesExtra.filter { it.isNotBlank() })
    }
}