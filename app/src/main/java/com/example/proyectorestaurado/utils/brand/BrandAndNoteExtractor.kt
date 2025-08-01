package com.example.proyectorestaurado.utils.brand

import com.example.proyectorestaurado.utils.TextNormalizationUtils
import com.example.proyectorestaurado.utils.BrandNoteUtils

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
            val lowerNoAccent = TextNormalizationUtils.removeAccents(part.lowercase())
            !BrandNoteUtils.noteKeywords.any { kw -> lowerNoAccent.contains(TextNormalizationUtils.removeAccents(kw)) }
        }
        return Pair(brands, noteCandidates + notesExtra.filter { it.isNotBlank() })
    }
}