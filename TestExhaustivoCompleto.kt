// TEST EXHAUSTIVO - LISTA COMPLETA DEL USUARIO
// Modelo para test independiente
data class Producto(
    val nombre: String,
    val cantidad: Double? = null,
    val unidad: String? = null,
    val marcas: List<String> = emptyList(),
    val nota: String? = null,
    val original: String? = null,
    val isChecked: Boolean = false
)

// Parser optimizado integrado para test
object TestParser {
    
    private fun aplicarMejorasAlTexto(texto: String): String {
        return texto
            .replace(Regex("(\\d+\\.\\d+)\\.$"), "$1")
            .replace(Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})"), "$1, $2")
            .replace(Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)"), "$1 $2, $3 $4")
            .replace(Regex("(\\d+)([a-zA-Záéíóúüñ]{3,})(\\d+)([a-zA-Záéíóúüñ]{3,})"), "$1 $2, $3 $4")
            .replace(Regex("\\s+"), " ")
            .replace(Regex(",+"), ",")
            .trim()
    }

    fun parse(texto: String): List<Producto> {
        val textoConMejoras = aplicarMejorasAlTexto(texto)
        val textoNormalizado = normalizarNumeros(textoConMejoras)
        val lineas = textoNormalizado.split(Regex("[\n;]+")).filter { it.isNotBlank() }
        
        return lineas.flatMap { linea ->
            if (esLineaBasura(linea)) return@flatMap emptyList()
            
            val lineaLimpia = limpiarNumeracion(linea)
            dividirEnFragmentos(lineaLimpia).mapNotNull { procesarFragmento(it) }
                .filter { esProductoValido(it) }
        }
    }

    private fun limpiarNumeracion(linea: String): String {
        if (!esNumeracionLista(linea)) return linea
        
        return linea
            .replace(Regex("^\\d+\\.\\s*"), "")
            .replace(Regex("\\.$"), "")
            .trim()
    }

    private fun esNumeracionLista(texto: String): Boolean {
        if (Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?|gr|ml|cm|mm)\\b", RegexOption.IGNORE_CASE)
                .containsMatchIn(texto)) return false
        
        return Regex("^\\d+\\.\\s+[a-zA-Z]").containsMatchIn(texto)
    }

    private fun dividirEnFragmentos(linea: String): List<String> {
        return dividirPorSeparadores(linea)
            .flatMap { separarMultiplesProductos(it) }
    }

    private fun dividirPorSeparadores(linea: String): List<String> {
        val separadores = Regex("[,;]|\\sy\\s|\\se\\s", RegexOption.IGNORE_CASE)
        
        return linea.split(separadores)
            .map { it.trim() }
            .filter { it.isNotBlank() && it.length > 1 }
    }

    private fun separarMultiplesProductos(fragmento: String): List<String> {
        val patrones = listOf(
            Regex("(\\d+)([a-zA-Záéíóúüñ]{3,})(\\d+)([a-zA-Záéíóúüñ]{3,})") to
                { m: MatchResult -> listOf("${m.groupValues[1]} ${m.groupValues[2]}", 
                                         "${m.groupValues[3]} ${m.groupValues[4]}") },
            
            Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)") to
                { m: MatchResult -> listOf("${m.groupValues[1]} ${m.groupValues[2].trim()}", 
                                         "${m.groupValues[3]} ${m.groupValues[4].trim()}") }
        )
        
        for ((patron, transformacion) in patrones) {
            val match = patron.find(fragmento)
            if (match != null) {
                return transformacion(match)
            }
        }
        
        return listOf(fragmento)
    }

    private fun procesarFragmento(fragmento: String): Producto? {
        if (esFragmentoBasura(fragmento)) return null
        
        val extraccion = extraerCantidadYUnidad(fragmento)
        val nombreLimpio = extraccion.nombreRestante.trim()
        
        if (nombreLimpio.isEmpty() || nombreLimpio.length < 2) return null
        
        return Producto(
            nombre = nombreLimpio,
            cantidad = extraccion.cantidad,
            unidad = extraccion.unidad,
            original = fragmento
        )
    }

    private data class Extraccion(
        val cantidad: Double?,
        val unidad: String?,
        val nombreRestante: String
    )

    private fun extraerCantidadYUnidad(texto: String): Extraccion {
        val textoNormalizado = texto.trim()
        
        val patrones = listOf(
            Regex("\\((?:x)?(\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ]*?)\\)") to true,
            Regex("^(\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ]*?)\\s+(.+)") to false,
            Regex("\\b(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|media\\s+docena)\\b", RegexOption.IGNORE_CASE) to false
        )
        
        for ((patron, esParentesis) in patrones) {
            val match = patron.find(textoNormalizado)
            if (match != null) {
                return when {
                    esParentesis -> {
                        val cantidad = match.groupValues[1].toDoubleOrNull()
                        val unidad = match.groupValues[2].takeIf { it.isNotBlank() }
                        val nombre = textoNormalizado.replace(match.value, "").trim()
                        Extraccion(cantidad, unidad, nombre)
                    }
                    match.groupValues[1].contains("media docena", ignoreCase = true) -> {
                        val nombre = textoNormalizado.replace(match.value, "6").trim()
                        Extraccion(6.0, null, nombre)
                    }
                    else -> {
                        val cantidad = convertirPalabraANumero(match.groupValues[1])
                        val unidad = match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }
                        val nombre = match.groupValues.getOrNull(3) ?: textoNormalizado.replace(match.value, "").trim()
                        Extraccion(cantidad, unidad, nombre)
                    }
                }
            }
        }
        
        return Extraccion(null, null, textoNormalizado)
    }

    private fun esLineaBasura(linea: String): Boolean = 
        linea.length < 3 || Regex("^[\\d\\s.,;]+$").matches(linea)

    private fun esFragmentoBasura(fragmento: String): Boolean = 
        fragmento.length < 2 || 
        Regex("^[\\d\\s.,;()]+$").matches(fragmento) ||
        fragmento.matches(Regex("^(y|e|con|de|la|el|un|una)$", RegexOption.IGNORE_CASE))

    private fun esProductoValido(producto: Producto): Boolean = 
        producto.nombre.isNotBlank() && 
        producto.nombre.length >= 2 && 
        !producto.nombre.matches(Regex("^[\\d\\s.,;()]+$"))

    private fun normalizarNumeros(texto: String): String {
        val conversiones = mapOf(
            "media docena" to "6"
        )
        
        return conversiones.entries.fold(texto) { acc, (patron, reemplazo) ->
            acc.replace(Regex("\\b$patron\\b", RegexOption.IGNORE_CASE), reemplazo)
        }
    }

    private fun convertirPalabraANumero(palabra: String): Double {
        return when (palabra.lowercase()) {
            "uno", "un", "una" -> 1.0
            "dos" -> 2.0
            "tres" -> 3.0
            "cuatro" -> 4.0
            "cinco" -> 5.0
            "seis" -> 6.0
            "siete" -> 7.0
            "ocho" -> 8.0
            "nueve" -> 9.0
            "diez" -> 10.0
            else -> palabra.toDoubleOrNull() ?: 1.0
        }
    }
}

fun main() {
    println("🎯 TEST EXHAUSTIVO - LISTA COMPLETA DEL USUARIO")
    println("Objetivo: Detectar el 100% de todos los productos")
    println("=".repeat(70))
    
    val listaCompleta = """
pastelera con albaca marca, frutos del maipo o minuto verde.(es una bolsa de crema de choclo)
Leche sin lactosa 2.
Mantequilla 1 barata.
2 lechugas francesas preguntar...cual son...
Carne molida más barata en bandeja
Tallarines 4 en oferta.
1malla tomates
1 huevo marca yemita o el más barato

leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos

1. 2 kg de tomates, 1 lechuga y pan (el más barato)
6 huevos, 1 litro de leche no muy cara
media docena de plátanos. 3 manzanas
500gr de carne molida 1 paquete de espaguetis
Zanahorias
1.5 litros de jugo de naranja,,,2 latas de atún.

1.1papa mediana2.sandia grande

1.salmon 1kg
2.zanaorias

cinco tomates
ocho zanahorias

seis tomates ocho papas

crema dental con fluor marca colgate o sensodyne.(es un tubo de pasta dental)
Shampoo anticaspa 1.
Jabón líquido
3 rollos papel higiénico preguntar...cuál es mejor...
Detergente en polvo más barato en caja
Esponjas 5 en oferta.
1bolsa arroz
1 aceite marca chef o el más económico

champú suave ,4cepillos,12servilletas,3 desodorantes 7 calcetines

2. 3 kg de azúcar, 1 sal y vinagre (el más barato)
8 pilas, 2 metros de cable no muy caro
media docena de velas. 4 focos
750ml de alcohol 1 paquete de algodón
Papel aluminio
2.5 metros de cinta adhesiva,,,3 tubos de pegamento.

1.2metros cable 2.bombilla grande 3.zapallo chino

1.2metros de madera 3.8 metros de cable 
2.desodorantes
3.desinfectante

siete velas
diez pilas

cinco focos ocho rollos
    """.trimIndent()
    
    try {
        val productosDetectados = TestParser.parse(listaCompleta)
        
        println("📊 RESULTADOS DE DETECCIÓN:")
        println("   Total productos detectados: ${productosDetectados.size}")
        println("\n📝 LISTA COMPLETA DE PRODUCTOS DETECTADOS:")
        println("=" * 50)
        
        productosDetectados.forEachIndexed { i, p ->
            val cantidad = p.cantidad?.let { 
                if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() 
            } ?: "1"
            val unidad = p.unidad?.let { " $it" } ?: ""
            println("${i+1}. ${p.nombre} (${cantidad}${unidad})")
            if (p.original != null) {
                println("    Original: '${p.original}'")
            }
        }
        
        println("\n" + "=".repeat(70))
        println("🔍 ANÁLISIS DE CASOS CRÍTICOS:")
        
        val casosCriticos = listOf(
            "pastelera con albaca marca" to productosDetectados.any { it.nombre.contains("pastelera", ignoreCase = true) },
            "2 lechugas francesas preguntar" to productosDetectados.any { it.nombre.contains("lechugas", ignoreCase = true) && it.cantidad == 2.0 },
            "6sandias,8tomates,6 zanaorias 5 zapatos" to (productosDetectados.count { it.nombre.contains("sandias", ignoreCase = true) } > 0),
            "media docena de plátanos" to productosDetectados.any { it.nombre.contains("plátanos", ignoreCase = true) && it.cantidad == 6.0 },
            "1.1papa mediana2.sandia grande" to productosDetectados.any { it.nombre.contains("papa", ignoreCase = true) },
            "4cepillos,12servilletas,3 desodorantes 7 calcetines" to (productosDetectados.count { it.nombre.contains("cepillos", ignoreCase = true) } > 0),
            "cinco tomates" to productosDetectados.any { it.nombre.contains("tomates", ignoreCase = true) && it.cantidad == 5.0 },
            "1.2metros cable 2.bombilla grande 3.zapallo chino" to (productosDetectados.count { it.nombre.contains("metros", ignoreCase = true) || it.nombre.contains("cable", ignoreCase = true) } > 0)
        )
        
        var casosExitosos = 0
        casosCriticos.forEach { (caso, exitoso) ->
            val status = if (exitoso) { casosExitosos++; "✅" } else "❌"
            println("$status $caso")
        }
        
        val ratioExito = (casosExitosos * 100 / casosCriticos.size)
        println("\n📈 EVALUACIÓN FINAL:")
        println("   Casos críticos exitosos: $casosExitosos/${casosCriticos.size}")
        println("   Ratio de éxito en casos críticos: $ratioExito%")
        println("   Total productos detectados: ${productosDetectados.size}")
        
        when {
            ratioExito >= 90 && productosDetectados.size >= 50 -> {
                println("🎉 ¡EXCELENTE! El parser maneja muy bien la lista compleja")
            }
            ratioExito >= 70 && productosDetectados.size >= 40 -> {
                println("😊 ¡BIEN! El parser funciona correctamente")
            }
            ratioExito >= 50 && productosDetectados.size >= 30 -> {
                println("😐 REGULAR: El parser necesita mejoras")
            }
            else -> {
                println("😞 PROBLEMAS: El parser necesita revisión")
            }
        }
        
    } catch (e: Exception) {
        println("💥 ERROR CRÍTICO: ${e.message}")
        e.printStackTrace()
    }
}

// Función auxiliar para repetir strings
operator fun String.times(n: Int): String = this.repeat(n)
