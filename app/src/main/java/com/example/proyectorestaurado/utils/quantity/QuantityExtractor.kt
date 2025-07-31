package com.example.proyectorestaurado.utils.quantity

import com.example.proyectorestaurado.utils.UnitUtils

object QuantityExtractor {
    fun extractQuantityAndUnit(input: String): Triple<Double?, String?, String> {
        var remainingInput = input.lowercase()

        // Casos especiales: docena y media docena
        if (remainingInput.contains("media docena")) {
            val name = remainingInput.replace("media docena", "").trim()
            return Triple(6.0, "u", name)
        }
        if (remainingInput.contains("docena")) {
            val name = remainingInput.replace("docena", "").trim()
            return Triple(12.0, "u", name)
        }

        // Cantidad y unidad pegadas (ej: 750ml, 1malla, 12servilletas)
        val quantityUnitTogetherRegex = Regex("""(\d+(?:[.,]\d+)?)([a-zA-Zñáéíóúü]+)""")
        quantityUnitTogetherRegex.find(remainingInput)?.let {
            val quantityStr = it.groupValues[1].replace(',', '.')
            val quantity = quantityStr.toDoubleOrNull()
            val unitStr = it.groupValues[2].lowercase()
            val unit = UnitUtils.unitSynonyms.entries.find { (_, synonyms) -> unitStr in synonyms }?.key ?: unitStr
            val name = remainingInput.replace(it.value, "").trim()
            return Triple(quantity, unit, name)
        }

        // Número separado de unidad (ej: 2 kg)
        val quantityWithUnitRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(${UnitUtils.unitSynonyms.values.flatten().joinToString("|")})\b""", RegexOption.IGNORE_CASE)
        quantityWithUnitRegex.find(remainingInput)?.let {
            val quantityStr = it.groupValues[1].replace(',', '.')
            val quantity = quantityStr.toDoubleOrNull()
            val unitStr = it.groupValues[2].lowercase()
            val unit = UnitUtils.unitSynonyms.entries.find { (_, synonyms) -> unitStr in synonyms }?.key
            val name = remainingInput.replace(it.value, "").trim()
            return Triple(quantity, unit, name)
        }

        // Cantidad al final (ej: 'jabón líquido 2')
        val quantityAtEndRegex = Regex("""(.+?)\s+(\d+(?:[.,]\d+)?)$""", RegexOption.IGNORE_CASE)
        quantityAtEndRegex.find(remainingInput)?.let {
            val name = it.groupValues[1].trim()
            val quantityStr = it.groupValues[2].replace(',', '.')
            val quantity = quantityStr.toDoubleOrNull()
            val (unit, finalName) = UnitUtils.findUnitInString(name)
            return Triple(quantity, unit, finalName)
        }

        // Primer número que aparezca
        val quantityRegex = Regex("""\d+[.,]?\d*""")
        quantityRegex.find(remainingInput)?.let {
            val quantityStr = it.groupValues[0].replace(',', '.')
            val quantity = quantityStr.toDoubleOrNull()
            val tempInput = remainingInput.replaceFirst(quantityStr, "").trim()
            val (unit, name) = UnitUtils.findUnitInString(tempInput)
            return Triple(quantity, unit, name)
        }

        return Triple(null, null, remainingInput)
    }
}