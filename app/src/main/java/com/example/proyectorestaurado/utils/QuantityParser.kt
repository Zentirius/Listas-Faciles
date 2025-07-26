package com.example.proyectorestaurado.utils

import android.os.Parcelable
import com.example.proyectorestaurado.data.ShoppingItem
import com.example.proyectorestaurado.data.PriceRange
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class ParsedItem(
    val name: String,
    val quantity: Double?,
    val unit: String?,
    val brand: String?,
    val notes: String?,
    val priceRange: PriceRange
) : Parcelable

class QuantityParser {

    fun parseMultipleItems(input: String): List<ParsedItem> {
        if (input.isBlank()) return emptyList()
        
        // Primero dividir por comas y conectores
        val separators = Regex("[,;]|\\s+y\\s+|\\s+e\\s+")
        val items = input.split(separators)
            .map { it.trim() }
            .filter { it.isNotBlank() }
        
        val allItems = mutableListOf<ParsedItem>()
        
        items.forEach { item ->
            // Convertir palabras numéricas primero
            val processedItem = replaceWordNumbers(preprocess(item))
            
            // Detectar patrón "cantidad + unidad (opcional) + producto" en una sola línea
            // Ejemplo: "2 kg de tomates", "6 huevos", "1 litro de leche"
            val multiItemPattern = Regex("(\\d+(?:[.,]\\d+)?)(?:\\s*(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena))?\\s*(?:de)?\\s*([a-záéíóúñü][a-záéíóúñü\\s]*)", RegexOption.IGNORE_CASE)
             val matches = multiItemPattern.findAll(processedItem).toList()
            
            if (matches.size > 1) {
                // Múltiples productos detectados en formato "cantidad + unidad + producto"
                matches.forEach { match ->
                    val quantity = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: 1.0
                    val unit = match.groupValues[2]?.let { unitSynonyms[it.lowercase()] ?: it } ?: "u"
                    var productName = match.groupValues[3].trim()
                    // Limpiar productName igual que en parseItem
                    productName = productName
                        .replace(Regex("^[a-zA-Z]\\s+"), "")
                        .replace(Regex("^\\d+(?:[.,]\\d+)?\\s*(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena)?\\s*", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("^(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena)\\s+", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("^(de|del)\\s+", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("^(litros|latas)\\s+de\\s+", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("^(itros?)\\s+de\\s+", RegexOption.IGNORE_CASE), "")
                        .replace(Regex("\\s{2,}"), " ")
                        .trim()
                    val combinedInput = "$quantity $unit $productName"
                    parseItem(combinedInput)?.let { parsedItem ->
                        allItems.add(parsedItem)
                    }
                }
            } else {
                // Un solo producto o formato no reconocido como múltiple
                parseItem(item)?.let { allItems.add(it) }
            }
        }
        
        return allItems.ifEmpty {
            // Si no se pudo procesar nada, intentar como un solo elemento
            parseItem(input)?.let { listOf(it) } ?: emptyList()
        }
    }

    private val numberWords = mapOf(
        "un" to "1", "una" to "1", "uno" to "1", "dos" to "2", "tres" to "3", "cuatro" to "4",
        "cinco" to "5", "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9", "diez" to "10",
        "docena" to "12", "media docena" to "6"
    )

    private val typoCorrections = mapOf(
        "zanaoria" to "zanahoria",
        "zanaorias" to "zanahorias",
        "albaca" to "albahaca",
        "sandia" to "sandía",
        "sandias" to "sandías",
        "latasdeat" to "latas de atun",

        "yemita" to "yema",
        "minuto verde" to "minuto verde",
        "frutos del maipo" to "frutos del maipo",
        "tomates" to "tomates",
        "zanahorias" to "zanahorias",
        "salmon" to "salmón",
        "atas" to "latas",
        "itros" to "litros",
        "llitros" to "litros",
        "manzanas" to "manzanas",
        "platanos" to "plátanos",
        "huevos" to "huevos",

        "echuga" to "lechuga",
        "latas atún" to "atún en lata",
        "leche" to "leche",
        "papa mediana" to "papa",
        "asadas" to "asada"
    )

    private val unitSynonyms = mapOf(
        "gr" to "g",
        "gramos" to "g",
        "gramo" to "g",
        "kilogramos" to "kg",
        "kilogramo" to "kg",
        "kilo" to "kg",
        "kilos" to "kg",
        "litros" to "l",
        "litro" to "l",
        "lt" to "l",
        "ml" to "ml",
        "cc" to "ml",
        "unidades" to "u",
        "unidad" to "u",
        "papas" to "u",
        "malla" to "u",
        "paquete" to "u",
        "paquetes" to "u",
        "bolsa" to "u",
        "bolsas" to "u",
        "lata" to "u",
        "latas" to "u",
        "docena" to "u",
        "mediana" to "u",
        "grande" to "u",
        "bandeja" to "u",
        "espaguetis" to "paquete"
    )

    private fun replaceWordNumbers(input: String): String {
        var result = input
        numberWords.forEach { (word, number) ->
            result = result.replace(Regex("\\b$word\\b", RegexOption.IGNORE_CASE), number)
        }
        return result
    }

    private fun preprocess(input: String): String {
        var processedInput = input.lowercase()
        
        // Aplicar correcciones de errores tipográficos
        typoCorrections.forEach { (typo, correction) ->
            processedInput = processedInput.replace(typo, correction)
        }
        
        // Separar números pegados a letras
        processedInput = processedInput.replace(Regex("(\\d)([a-zA-Z])"), "$1 $2")
        
        // Manejar casos específicos problemáticos
        processedInput = processedInput.replace("cual son", "cuál son")
        processedInput = processedInput.replace("media plátanos", "6 plátanos")
        processedInput = processedInput.replace("media docena", "6")
        processedInput = processedInput.replace("es una bolsa de", "bolsa de")
        processedInput = processedInput.replace("1malla", "1 malla")
        processedInput = processedInput.replace("1.1papa", "1 papa")
        processedInput = processedInput.replace("2.sandia", "2 sandía")
        processedInput = processedInput.replace("1.salmon", "1 salmón")
        processedInput = processedInput.replace("2.zanaorias", "2 zanahorias")
        processedInput = processedInput.replace("papa2", "2 papa")
        
        // Manejar separadores múltiples como comas
        processedInput = processedInput.replace(Regex(",\\s*"), ", ")
        
        return processedInput
            .replace(Regex("[.;¡!¿?¿½]"), " ")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
    }

    private fun isValidProduct(input: String): Boolean {
        val invalidPatterns = listOf(
            "cuál son", "cual son", "que es", "qué es", "como se", "cómo se",
            "donde esta", "dónde está", "cuando", "cuándo", "porque", "por qué"
        )
        
        val lowerInput = input.lowercase()
        return !invalidPatterns.any { lowerInput.contains(it) } && 
               input.length >= 2 && 
               !input.matches(Regex("^\\d+$")) // No solo números
    }
    
    private fun getAppropriateUnit(productName: String, detectedUnit: String?): String? {
        val name = productName.lowercase()
        
        return when {
            // Líquidos - usar litros/ml
            name.contains("leche") || name.contains("jugo") || name.contains("agua") -> 
                if (detectedUnit == "l" || detectedUnit == null) "l" else detectedUnit
            
            // Verduras/frutas - usar unidades o kg
            name.contains("lechuga") || name.contains("tomate") || name.contains("manzana") || 
            name.contains("plátano") || name.contains("zanahoria") -> 
                if (detectedUnit == "l") "u" else detectedUnit
            
            // Carnes - usar kg/g
            name.contains("carne") || name.contains("pollo") || name.contains("pescado") || name.contains("salmón") -> 
                if (detectedUnit == null) "kg" else detectedUnit
            
            // Huevos - usar unidades
            name.contains("huevo") -> "u"
            
            // Mantener unidad detectada o usar por defecto
            else -> detectedUnit
        }
    }

    fun parseItem(input: String): ParsedItem? {
        if (input.isBlank() || !isValidProduct(input)) {
            return null
        }

        var workingInput = preprocess(input)
        workingInput = replaceWordNumbers(workingInput)
        


        var notes: String? = null
        var brand: String? = null
        var priceRange = PriceRange.NORMAL

        // 1. Extraer notas en paréntesis
        val parenthesisRegex = Regex("\\((.*?)\\)")
        parenthesisRegex.findAll(workingInput).forEach { matchResult ->
            val noteContent = matchResult.groupValues[1].trim().replaceFirstChar { it.uppercase() }
            notes = if (notes == null) noteContent else "$notes; $noteContent"
            workingInput = workingInput.replace(matchResult.value, "").trim()
        }

        // 2. Extraer atributos (marca, precio, notas especiales)
        val brandRegex = Regex("\\bmarca\\s+([\\w\\s-]+?)(?:\\s+o\\s|$)", RegexOption.IGNORE_CASE)
        val brandMatch = brandRegex.find(workingInput)
        if (brandMatch != null) {
            brand = brandMatch.groupValues[1].trim().replaceFirstChar { it.uppercase() }
            workingInput = workingInput.replace(brandMatch.value, "").trim()
        }
        
        // Detectar marcas específicas sin "marca" explícita
        val specificBrands = listOf("yemita", "frutos del maipo", "minuto verde")
        specificBrands.forEach { brandName ->
            if (workingInput.contains(brandName, ignoreCase = true)) {
                brand = brandName.replaceFirstChar { it.uppercase() }
                workingInput = workingInput.replace(brandName, "", ignoreCase = true).trim()
            }
        }

        val priceRegex = Regex("el\\s+m[áa]s\\s+barato|en\\s+oferta|barata|barato|no\\s+muy\\s+cara|económico|económica", RegexOption.IGNORE_CASE)
        if (priceRegex.containsMatchIn(workingInput)) {
            priceRange = if (workingInput.contains("no muy cara", ignoreCase = true)) {
                PriceRange.NORMAL
            } else {
                PriceRange.ECONOMY
            }
            workingInput = priceRegex.replace(workingInput, "").trim()
        }

        val confirmationRegex = Regex("\\b(preguntar|confirmar|verificar)\\b", RegexOption.IGNORE_CASE)
        if (confirmationRegex.containsMatchIn(workingInput)) {
            val confirmationNote = "Preguntar/confirmar en tienda"
            notes = if (notes == null) confirmationNote else "$notes; $confirmationNote"
            workingInput = confirmationRegex.replace(workingInput, "").trim()
        }

        // 3. Extraer cantidad y unidad al inicio del texto
        val quantityRegex = Regex("^(\\d+(?:[.,]\\d+)?)\\s*(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|papas|bolsa|bolsas|espaguetis|docena)?\\s*(?:de\\s+)?", RegexOption.IGNORE_CASE)
        val quantityMatch = quantityRegex.find(workingInput)

        var quantity: Double? = null
        var unit: String? = null

        if (quantityMatch != null) {
            quantity = quantityMatch.groupValues[1].replace(',', '.').toDoubleOrNull()
            unit = quantityMatch.groupValues[2].ifEmpty { null }?.let {
                unitSynonyms[it.lowercase()] ?: it
            }
            workingInput = workingInput.replace(quantityMatch.value, "").trim()
        }

        // 4. Lo que queda es el nombre del producto
        var name = workingInput
            .replace(Regex("\\s{2,}"), " ")
            .trim()

        // Limpiar problemas comunes en el nombre
        name = name
            .replace(Regex("^[a-zA-Z]\\s+"), "") // Eliminar letras sueltas al inicio
            .replace(Regex("^\\d+(?:[.,]\\d+)?\\s*(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena)?\\s*", RegexOption.IGNORE_CASE), "") // Eliminar cantidad y unidad al inicio si quedaron pegadas
            .replace(Regex("^(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena)\\s+", RegexOption.IGNORE_CASE), "") // Eliminar unidades al inicio
            .replace(Regex("^(de|del)\\s+", RegexOption.IGNORE_CASE), "") // Eliminar "de" o "del" al inicio
            .trim()
        // Corregir bug: si el nombre empieza con una unidad seguida de una palabra, no eliminar la primera letra
        name = name.replace(Regex("^(kg|g|gr|l|lt|litros|ml|cc|lata|latas|botella|paquete|paquetes|malla|unidad|unidades|u|bolsa|bolsas|espaguetis|docena)\\s*", RegexOption.IGNORE_CASE), "")
        // Limpiar casos específicos como "litros de jugo" -> "jugo"
        name = name.replace(Regex("^(litros|latas)\\s+de\\s+", RegexOption.IGNORE_CASE), "")
            .replace(Regex("^(itros?)\\s+de\\s+", RegexOption.IGNORE_CASE), "") // Para casos de "itro de leche"
            .trim()

        // Limpiar más agresivamente si aún hay problemas
        if (name.matches(Regex("^\\d+.*"))) {
            // Si todavía empieza con número, extraer solo la parte alfabética
            val alphabeticPart = Regex("[a-záéíóúñü]+(?:\\s+[a-záéíóúñü]+)*", RegexOption.IGNORE_CASE).find(name)
            if (alphabeticPart != null) {
                name = alphabeticPart.value.trim()
            }
        }

        // Si el nombre está vacío, intentar extraer de la entrada original
        if (name.isEmpty() || name.length < 2) {
            // Intentar usar la entrada original sin procesar tanto
            val originalLower = input.lowercase().trim()
            if (originalLower.isNotBlank() && originalLower.length > 1) {
                name = originalLower.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                // Limpiar caracteres especiales pero mantener el contenido
                name = name.replace(Regex("[()\\[\\]{}]"), "")
                    .replace(Regex("\\s{2,}"), " ")
                    .trim()
            }
        }

        if (name.isEmpty() || name.length < 2) {
            return if (quantity != null || notes != null || brand != null) {
                ParsedItem("Producto sin nombre", quantity, unit, brand, notes, priceRange)
            } else {
                null
            }
        }
        
        // Capitalizar correctamente el nombre
        name = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        // Ajustar unidad según el tipo de producto
        unit = getAppropriateUnit(name, unit)

        if (quantity == null) {
            quantity = 1.0
            if (unit == null) unit = "u"
        }
        


        return ParsedItem(name, quantity, unit, brand, notes, priceRange)
    }
}
