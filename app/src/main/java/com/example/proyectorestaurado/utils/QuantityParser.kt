package com.example.proyectorestaurado.utils

import android.os.Parcelable
import com.example.proyectorestaurado.data.PriceRange
import kotlinx.parcelize.Parcelize
import com.example.proyectorestaurado.utils.ParseMode
import com.example.proyectorestaurado.utils.BrandNoteUtils
import com.example.proyectorestaurado.utils.IrrelevantItemsUtils.irrelevantItems

@Parcelize
data class ParsedItem(
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val brand: String? = null,
    val notes: String? = null,
    val category: String? = null,
    val priceRange: PriceRange? = null
) : Parcelable

object QuantityParser {

    private val wordToNumber = mapOf(
        "un" to 1.0, "uno" to 1.0, "una" to 1.0, "dos" to 2.0, "tres" to 3.0, "cuatro" to 4.0,
        "cinco" to 5.0, "seis" to 6.0, "siete" to 7.0, "ocho" to 8.0, "nueve" to 9.0, "diez" to 10.0,
        "once" to 11.0, "doce" to 12.0, "quince" to 15.0, "veinte" to 20.0, "treinta" to 30.0,
        "media" to 0.5, "docena" to 12.0
    )

    private val unitSynonyms = UnitUtils.unitSynonyms + mapOf(
        "lata" to listOf("lata", "latas"),
        "rollo" to listOf("rollo", "rollos"),
        "bolsa" to listOf("bolsa", "bolsas"),
        "caja" to listOf("caja", "cajas"),
        "tubo" to listOf("tubo", "tubos"),
        "m" to listOf("metro", "metros", "mts", "m"),
        "ml" to listOf("ml", "mililitro", "mililitros", "cc"),
        "docena" to listOf("docena", "docenas"),
        "pulgada" to listOf("pulgada", "pulgadas", "\""),
        "v" to listOf("v", "voltio", "voltios"),
        "malla" to listOf("malla", "mallas"),
        "bandeja" to listOf("bandeja", "bandejas")
    )

    private val brandOrKeywords = setOf("o", "ó", "u", "o el", "o la")
    private val noteKeywords = listOf("barata", "en oferta", "preguntar", "no muy cara", "más barato", "barato", "económico", "o el más barato", "o el económico")

    private val ignoreKeywords = setOf(
        "cuál", "cual", "tipo", "no son cantidades", "orden", "de lista", "/2", "/", "frutos del", "no", "son", "cantidades", "es", "orden", "de", "lista", "m", "y", "car", "es mej", "r",
        "zapatos", "ropa", "pantalón", "pantalones", "camisa", "camisas", "vestido", "vestidos"
    )

    private val singularizeExceptions = mapOf(
        "lentes" to "lentes",
        "francesa" to "francesa",
        "pilas" to "pilas",
        "servilletas" to "servilletas",
        "calcetines" to "calcetines",
        "focos" to "focos",
        // Frutas y verduras plurales y singulares
        "tomates" to "tomates", "tomate" to "tomate",
        "zanahorias" to "zanahorias", "zanahoria" to "zanahoria",
        "papas" to "papas", "papa" to "papa",
        "manzanas" to "manzanas", "manzana" to "manzana",
        "plátanos" to "plátanos", "plátano" to "plátano",
        "sandías" to "sandías", "sandía" to "sandía",
        "lechugas" to "lechugas", "lechuga" to "lechuga",
        "choclos" to "choclos", "choclo" to "choclo",
        // Otros productos típicos
        "cebollas" to "cebollas", "cebolla" to "cebolla",
        "ajos" to "ajos", "ajo" to "ajo",
        "pimentones" to "pimentones", "pimentón" to "pimentón",
        "limones" to "limones", "limón" to "limón",
        "naranjas" to "naranjas", "naranja" to "naranja",
        "uvas" to "uvas", "uva" to "uva",
        "peras" to "peras", "pera" to "pera"
    )

    fun parseMultipleItems(originalInput: String, mode: ParseMode = ParseMode.LIBRE): List<ParsedItem> {
        if (originalInput.isBlank()) return emptyList()
        // Reemplazo de palabras-numero ANTES del split
        val textWithNumbers = replaceWrittenNumbers(originalInput)
        val correctedText = applyOcrCorrections(textWithNumbers)

        // Split inteligente: por líneas si hay varias, por separadores si es una sola línea
        val lineItems = correctedText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val initialItems = if (lineItems.size > 3) {
            lineItems
        } else {
            val itemSeparators = Regex("""\n|(?<![0-9])[.,;](?![0-9])|\s-\s|\s+y\s+|\s+na\s+|\s+o\s+|\s+u\s+|(?<=\d)\s+(?=\d)""")
            correctedText.split(itemSeparators).map { it.trim() }.filter { it.isNotBlank() }
        }
        val palabrasNota = listOf("barato", "más barato", "en oferta", "económico", "preguntar", "oferta", "cual", "más", "barata", "barata en bandeja", "económica", "mejor", "ofertas", "precio", "en promoción", "en promo", "en descuento", "en liquidación")
        val parsedItems = mutableListOf<ParsedItem>()
        var ultimoProducto: ParsedItem? = null
        for (item in initialItems) {
            val palabras = item.lowercase().split(" ").map { it.trim() }
            val esNota = palabras.all { TextNormalizationUtils.removeAccents(it) in palabrasNota.map { kw -> TextNormalizationUtils.removeAccents(kw) } } ||
                TextNormalizationUtils.removeAccents(palabras.firstOrNull() ?: "") in palabrasNota.map { kw -> TextNormalizationUtils.removeAccents(kw) }
            if (esNota && ultimoProducto != null) {
                // Agregar como nota al producto anterior
                val notaLimpia = com.example.proyectorestaurado.utils.BrandNoteUtils.cleanNote(item)
                if (notaLimpia.isNotBlank()) {
                    ultimoProducto = ultimoProducto.copy(
                        notes = (ultimoProducto.notes ?: "") + if ((ultimoProducto.notes ?: "").isNotBlank()) ", " else "" + notaLimpia
                    )
                    parsedItems[parsedItems.size - 1] = ultimoProducto
                }
            } else {
                val subItems = splitSubItems(item)
                val nuevos = subItems.mapNotNull { subItem ->
                val palabrasNota = listOf("mas", "más", "barato", "económico", "oferta", "preguntar", "mejor", "en oferta", "más barato", "económica", "no muy cara")
                val subItemLimpio = subItem.trim().lowercase()
                if (palabrasNota.any { TextNormalizationUtils.removeAccents(subItemLimpio) == TextNormalizationUtils.removeAccents(it) }) {
                    // Si es solo una nota, agregar como nota al producto anterior
                    if (ultimoProducto != null) {
                        val notaLimpia = com.example.proyectorestaurado.utils.BrandNoteUtils.cleanNote(subItem)
                        if (notaLimpia.isNotBlank()) {
                            ultimoProducto = ultimoProducto.copy(
                                notes = (ultimoProducto.notes ?: "") + if ((ultimoProducto.notes ?: "").isNotBlank()) ", " else "" + notaLimpia
                            )
                            parsedItems[parsedItems.size - 1] = ultimoProducto
                        }
                    }
                    null // No crear producto
                } else {
                    parseItem(subItem, mode)
                }
            }
                if (nuevos.isNotEmpty()) {
                    parsedItems.addAll(nuevos)
                    ultimoProducto = nuevos.last()
                }
            }
        }
        return groupItems(parsedItems)
    }

    private fun applyOcrCorrections(input: String): String {
    var result = input
    result = replaceWrittenNumbers(result)
    result = normalizeMissingSpaces(result)

    result = result
        .replace(Regex("(^|\\s)\\d+[.)](?=\\s*)"), " ") // Remove list numbering like "1. " or "2)"
        // Normalización robusta de plurales y OCR
        .replace(Regex("tomateses|tomatees|tomate s|tomat(?!e)", RegexOption.IGNORE_CASE), "tomates")
        .replace(Regex("zanahoriases|zanaorias|zanahoria s", RegexOption.IGNORE_CASE), "zanahorias")
        .replace(Regex("limónes|limon s|limone s", RegexOption.IGNORE_CASE), "limones")
        .replace(Regex("papas s|papa s", RegexOption.IGNORE_CASE), "papas")
        .replace(Regex("cebollas s|cebolla s", RegexOption.IGNORE_CASE), "cebollas")
        .replace(Regex("ajos s|ajo s", RegexOption.IGNORE_CASE), "ajos")
        .replace(Regex("pimentones s|pimenton s", RegexOption.IGNORE_CASE), "pimentones")
        .replace(Regex("naranjas s|naranja s", RegexOption.IGNORE_CASE), "naranjas")
        .replace(Regex("uvas s|uva s", RegexOption.IGNORE_CASE), "uvas")
        .replace(Regex("peras s|pera s", RegexOption.IGNORE_CASE), "peras")
        .replace(Regex("sandias s|sandía s|sandías s", RegexOption.IGNORE_CASE), "sandías")
        // Marcas compuestas conocidas
        .replace(Regex("marca fruto del maipo", RegexOption.IGNORE_CASE), "marca fruto del maipo")
        .replace(Regex("marca minuto verde", RegexOption.IGNORE_CASE), "marca minuto verde")
        .replace(Regex("marca tres montes", RegexOption.IGNORE_CASE), "marca tres montes")
        .replace(Regex("marca doña pepa", RegexOption.IGNORE_CASE), "marca doña pepa")
        .replace(Regex("marca la serena", RegexOption.IGNORE_CASE), "marca la serena")
        // Otros reemplazos útiles
        .replace(Regex("""\b(crema dental|pasta dental)\b"""), "pasta de dientes")
        .replace("shampoo", "champú", ignoreCase = true)
        .replace("espaguetis", "tallarines", ignoreCase = true)
        .replace("1malla", "1 malla", ignoreCase = true)
        .replace("6sandias", "6 sandias", ignoreCase = true)
        .replace("8tomates", "8 tomates", ignoreCase = true)
        .replace("6 zanaorias", "6 zanahorias", ignoreCase = true)
        .replace("leche asadas", "leche asada", ignoreCase = true)
            .replace("6 zanaorias", "6 zanahorias", ignoreCase = true)
            .replace("leche asadas", "leche asada", ignoreCase = true)

        // NO filtrar líneas aquí, solo devolver el texto corregido
        return result
    }

    private fun splitSubItems(item: String): List<String> {
        // Split por número seguido de texto, texto seguido de número, y conectores
        val numberThenTextRegex = Regex("(\\d)\\s*([a-zA-Zñáéíóúü(])")
        val textThenNumberRegex = Regex("([a-zA-Zñáéíóúü])\\s*(\\d)")
        var currentItem = item
        currentItem = currentItem.replace(numberThenTextRegex, "$1\n$2")
        currentItem = currentItem.replace(textThenNumberRegex, "$1\n$2")

        // También split por " y ", " na ", " o ", " u " si están entre productos
        val connectorRegex = Regex("(\\s+y\\s+|\\s+na\\s+|\\s+o\\s+|\\s+u\\s+)")
        currentItem = currentItem.replace(connectorRegex, "\n")

        val initialSplit = currentItem.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        if (initialSplit.size > 1) return initialSplit

        // Split por número al inicio de palabra
        val splitByNumber = Regex("(?<=\\d)\\s+(?=[a-zA-Zñáéíóúü])")
        val parts = currentItem.split(splitByNumber).map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size > 1) return parts

        // Validación extra: evitar productos inválidos solo si el nombre completo es irrelevante o muy corto
        val cleaned = item.trim().lowercase()
        if (cleaned.length < 2 || irrelevantItems.any { cleaned == it || cleaned.startsWith(it) }) {
            return emptyList()
        }
        return listOf(item)
    }

    private fun groupItems(items: List<ParsedItem>): List<ParsedItem> {
        return items.groupBy { Triple(it.name.lowercase(), it.unit, it.brand) }.map { (_, group) ->
            if (group.any { it.notes != null }) {
                group
            } else {
                val first = group.first()
                val totalQuantity = group.sumOf { it.quantity ?: 1.0 }
                listOf(first.copy(quantity = if (totalQuantity > 0) totalQuantity else null))
            }
        }.flatten()
    }

    fun parseItem(originalInput: String, mode: ParseMode = ParseMode.LIBRE): ParsedItem? {
        if (originalInput.isBlank() || originalInput.length < 2) return null

        var input = originalInput.lowercase()
        var (notes, remainingAfterNotes) = extractNotes(input)
        input = remainingAfterNotes

        val (brand, remainingAfterBrand, brandNotes) = extractBrand(input)
        input = remainingAfterBrand

        if (brandNotes != null) {
            notes = listOfNotNull(notes, brandNotes)
                .flatMap { it.split(",") }
                .map { it.trim() }
                .distinct()
                .joinToString(", ").ifEmpty { null }
        }

        val (quantity, unit, remainingAfterQuant) = extractQuantityAndUnit(input)
        input = remainingAfterQuant

        val finalProductName = cleanProductName(input)

        val finalProductNameNoAccents = TextNormalizationUtils.removeAccents(finalProductName.lowercase())
        val ignoreKeywordsNoAccents = ignoreKeywords.map { TextNormalizationUtils.removeAccents(it) }
        if (finalProductName.isBlank() || finalProductName.length < 2 || unitSynonyms.values.flatten().contains(finalProductName.lowercase()) || ignoreKeywordsNoAccents.any { finalProductNameNoAccents.contains(it) }) {
            return null
        }

        val category = if (mode == ParseMode.SUPERMERCADO) {
            inferCategory(finalProductName, brand, notes)
        } else null

        val priceRange = inferPriceRange(notes)

        return ParsedItem(
            name = finalProductName.replaceFirstChar { it.uppercase() },
            quantity = quantity,
            unit = unit,
            brand = brand,
            notes = notes,
            category = category,
            priceRange = priceRange
        )
    }

    private fun replaceWrittenNumbers(input: String): String {
        var result = input
        wordToNumber.forEach { (word, num) ->
            result = result.replace(Regex("\\b$word\\b", RegexOption.IGNORE_CASE), num.toString())
        }
        return result
    }

    private fun normalizeMissingSpaces(input: String): String {
        return input
            .replace(Regex("(?<=\\d)(?=(kg|gr|ml|lt|mts|m|cc|lts|pq|u))", RegexOption.IGNORE_CASE), " ") // 1kg -> 1 kg
            .replace(Regex("(?<=\\d)(?=[a-zA-Zñ])"), " ") // 1kg -> 1 kg
            .replace(Regex("(?<=[a-zA-Zñ])(?=\\d)"), " ") // papa1 -> papa 1
            .replace(Regex("([a-zA-Zñáéíóúü])([0-9])"), "$1 $2") // texto seguido de número
            .replace(Regex("(\\d)([a-zA-Zñáéíóúü])"), "$1 $2") // número seguido de texto
    }

    private fun extractNotes(input: String): Pair<String?, String> {
        var remainingInput = input
        val foundNotes = mutableListOf<String>()

        // Notas entre paréntesis
        val noteInParenthesesRegex = Regex("""\(([^)]+)\)""")
        remainingInput = noteInParenthesesRegex.replace(remainingInput) { match ->
            foundNotes.add(match.groupValues[1].trim())
            ""
        }

        // Nueva lógica avanzada: buscar palabra clave de nota y tomar todo hasta la próxima coma, punto y coma, salto de línea o final
        val noteKeywordsPattern = noteKeywords.joinToString("|", prefix = "(?:", postfix = ")")
        val advancedNoteRegex = Regex("""$noteKeywordsPattern[^,;\.\n]*""", RegexOption.IGNORE_CASE)
        advancedNoteRegex.findAll(remainingInput).forEach { match ->
            foundNotes.add(match.value.trim())
            remainingInput = remainingInput.replace(match.value, "")
        }

        val notes = foundNotes.joinToString(", ").ifEmpty { null }
        return Pair(notes, remainingInput.trim())
    }

    private fun extractBrand(input: String): Triple<String?, String, String?> {
        var remainingInput = input
        var extractedNotes: String? = null

        // Nueva lógica avanzada: buscar 'marca' y tomar todo hasta la próxima coma, punto y coma, salto de línea o final
        val brandAdvancedRegex = Regex("""\bmarca(?:s)?[, ]*([^,;\.\n]*)_""", RegexOption.IGNORE_CASE)
        brandAdvancedRegex.find(remainingInput)?.let {
            val brandText = it.groupValues[1].trim()
            val (brands, notes) = BrandNoteUtils.separateBrandsAndNotes(brandText)
            // Eliminar el bloque completo de marca de la cadena
            remainingInput = Regex(
                """\bmarca(?:s)?[, ]*${Regex.escape(brandText)}""",
                RegexOption.IGNORE_CASE
            ).replaceFirst(remainingInput, "").trim()
            extractedNotes = notes.joinToString(", ").ifEmpty { null }
            return Triple(brands.joinToString(" / "), remainingInput.trim(), extractedNotes)
        }

        // Caso 2: "producto marca X" o "producto barato marca Y"
        val words = remainingInput.split(" ").toMutableList()
        val orIndex = words.indexOfFirst { it in brandOrKeywords }
        if (orIndex > 0 && orIndex < words.size - 1) {
            val potentialBrand = words.subList(orIndex - 1, words.size).joinToString(" ")
            val (brands, notes) = BrandNoteUtils.separateBrandsAndNotes(potentialBrand)
            val name = words.subList(0, orIndex - 1).joinToString(" ")
            extractedNotes = notes.joinToString(", ").ifEmpty { null }
            return Triple(brands.joinToString(" / "), name.trim(), extractedNotes)
        }

        // Caso 3: "producto [nota] [marca]" o "producto [marca] [nota]"
        val noteAndBrandWords = words.filter { word ->
            noteKeywords.any { it.equals(word, ignoreCase = true) } ||
                    !unitSynonyms.values.flatten().contains(word) // Evitar confundir unidades con marcas
        }

        if (noteAndBrandWords.size > 1) {
            // Suponemos que la última palabra que no es una nota es la marca
            val lastWord = words.last()
            if (!noteKeywords.any { it.equals(lastWord, ignoreCase = true)}) {
                val (brands, notes) = BrandNoteUtils.separateBrandsAndNotes(words.takeLast(2).joinToString(" "))
                if (brands.isNotEmpty()) {
                    val name = words.dropLast(2).joinToString(" ")
                    extractedNotes = notes.joinToString(", ").ifEmpty { null }
                    return Triple(brands.joinToString(" / "), name, extractedNotes)
                }
            }
        }

        return Triple(null, remainingInput.trim(), null)
    }

    private fun extractQuantityAndUnit(input: String): Triple<Double?, String?, String> {
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
            val unit = unitSynonyms.entries.find { (_, synonyms) -> unitStr in synonyms }?.key ?: unitStr
            val name = remainingInput.replace(it.value, "").trim()
            return Triple(quantity, unit, name)
        }

        // Número separado de unidad (ej: 2 kg)
        val quantityWithUnitRegex = Regex("""(\d+(?:[.,]\d+)?)\s*(${unitSynonyms.values.flatten().joinToString("|")})\b""", RegexOption.IGNORE_CASE)
        quantityWithUnitRegex.find(remainingInput)?.let {
            val quantityStr = it.groupValues[1].replace(',', '.')
            val quantity = quantityStr.toDoubleOrNull()
            val unitStr = it.groupValues[2].lowercase()
            val unit = unitSynonyms.entries.find { (_, synonyms) -> unitStr in synonyms }?.key
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

    private fun singularize(word: String): String {
        singularizeExceptions[word.lowercase()]?.let {
            return it
        }
        return when {
            word.endsWith("es", ignoreCase = true) &&
                !word.endsWith("ses", ignoreCase = true) &&
                !word.endsWith("ces", ignoreCase = true) -> word.dropLast(2)
            word.endsWith("s", ignoreCase = true) &&
                word.length > 3 &&
                !singularizeExceptions.containsKey(word.lowercase()) &&
                !word.endsWith("es", ignoreCase = true) -> word.dropLast(1)
            else -> word
        }
    }

    private fun cleanProductName(name: String): String {
        val irrelevantWords = listOf("de", "el", "la", "los", "las", "un", "una", "unos", "unas", "con", "en", "a", "para", "talla", "sin", "con fluor")
        var cleanedName = name.trim()
        irrelevantWords.forEach { cleanedName = cleanedName.replace(Regex("""\b$it\b""", RegexOption.IGNORE_CASE), "") }
        cleanedName = cleanedName.replace(Regex("""[^a-zA-Z0-9\sñáéíóúü/\.-]"""), " ").replace(Regex("""\s{2,}"""), " ").trim()
        return singularize(cleanedName)
         return singularize(cleanedName)
        }

        private fun inferPriceRange(notes: String?): PriceRange? {
            if (notes == null) return null
            val economyKeywords = listOf("barato", "económico", "oferta", "barata")
            return if (economyKeywords.any { kw ->
                TextNormalizationUtils.removeAccents(notes).contains(TextNormalizationUtils.removeAccents(kw), ignoreCase = true)
            }) {
                PriceRange.ECONOMY
            } else {
                null
            }
        }

        private fun inferCategory(productName: String, brand: String?, notes: String?): String? {
            val textToSearch = listOfNotNull(productName, brand, notes).joinToString(" ").lowercase()
            for ((category, keywords) in categoryKeywords) {
                if (keywords.any { keyword ->
                    TextNormalizationUtils.removeAccents(textToSearch).contains(TextNormalizationUtils.removeAccents(keyword), ignoreCase = true)
                }) {
                    return category
                }
            }
            return null
        }

        private val categoryKeywords = mapOf(
            "Frutas y Verduras" to listOf("tomate", "zanahoria", "papa", "sandía", "manzana", "plátano", "lechuga", "francesa", "albahaca", "choclo", "salmon"),
            "Carnes" to listOf("carne molida", "pollo", "bistec"),
            "Lácteos y Huevos" to listOf("leche", "queso", "yogur", "huevos", "mantequilla", "yemita"),
            "Panadería y Dulces" to listOf("pan", "galletas", "pastel", "leche asada"),
            "Despensa" to listOf("arroz", "fideos", "aceite", "azúcar", "sal", "tallarines", "atún", "jugo de naranja", "jugo naranja"),
            "Bebidas" to listOf("gaseosa", "jugo", "agua"),
            "Cuidado Personal" to listOf("champú", "jabón", "pasta de dientes", "cepillos", "desodorante", "calcetines", "focos", "pilas"),
            "Limpieza" to listOf("detergente", "cloro", "lavalozas", "papel higiénico", "esponjas", "servilletas")
        )

    
        // Ahora importado desde UnitUtils:
        // val result = UnitUtils.UnitUtils.findUnitInString(input)
    
    }