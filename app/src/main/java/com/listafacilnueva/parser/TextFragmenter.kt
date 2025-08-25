package com.listafacilnueva.parser

import android.util.Log
import com.listafacilnueva.model.Producto

/**
 * 🔪 FRAGMENTADOR DE TEXTO
 * 
 * Funciones para dividir texto en fragmentos procesables
 * y separar múltiples productos en una sola línea.
 */
object TextFragmenter {

    private var DEBUG_MODE = false
    private const val TAG = "FRAGMENTER"
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            Log.d(TAG, message)
        }
    }

    // Normaliza un fragmento: quita prefijo "de " y espacios repetidos (conserva la puntuación para no romper enumeraciones)
    private fun normalizarFragmento(fragmento: String): String {
        var s = fragmento.trim()
        s = s.replace(Regex("^de\\s+", RegexOption.IGNORE_CASE), "")
        s = s.replace(Regex("\\s+"), " ").trim()
        return s
    }

    // Determina si el fragmento es solo una cantidad opcionalmente seguida de una unidad conocida
    private fun esCantidadPura(fragmento: String): Boolean {
        val m = Regex("^(\\d+(?:\\.\\d+)?)(?:\\s+([\\p{L}]+))?$", RegexOption.IGNORE_CASE).find(fragmento.trim())
        if (m != null) {
            val unidad = m.groupValues.getOrNull(2)?.trim().orEmpty()
            return unidad.isEmpty() || ParserUtils.esUnidadConocida(unidad)
        }
        return false
    }

    // Inserta espacio después de una enumeración pegada al texto o a un dígito seguido de letra.
    // Casos: "1.papa" -> "1. papa"; "1.1papa" -> "1. 1papa". No afecta decimales como "3.8 metros".
    private fun ajustarEnumeracionPegada(s: String): String {
        var t = s
        // Global: dígito después del punto y luego letra (evita decimales porque exige letra tras el dígito)
        t = t.replace(Regex("(\\d+)\\.(\\d)(?=[\\p{L}])"), "$1. $2")
        // Global: letra inmediatamente después del punto
        t = t.replace(Regex("(\\d+)\\.(?=[\\p{L}])"), "$1. ")
        return t
    }

    /**
     * 🔪 DIVIDIR EN FRAGMENTOS - VERSIÓN MEJORADA PARA 71 PRODUCTOS
     */
    fun dividirEnFragmentos(linea: String): List<String> {
        log("Dividiendo en fragmentos: '$linea'")
        val lineaAjustada = ajustarEnumeracionPegada(linea)
        
        // Manejar líneas con "marca" primero
        if (lineaAjustada.contains("marca", ignoreCase = true)) {
            return procesarLineaConMarca(lineaAjustada)
        }
        
        // MEJORA: Segmentación inteligente por conjunciones
        var fragmentos = dividirPorSeparadores(lineaAjustada)
        
        // Separar cantidades pegadas y múltiples productos
        val fragmentosExpandidos = mutableListOf<String>()
        for (frag in fragmentos) {
            val fragAjustado = ajustarEnumeracionPegada(frag)
            val separados = separarMultiplesProductos(fragAjustado)
            fragmentosExpandidos.addAll(separados)
        }
        
        // Devolver tal cual (solo trim/filtrado), para no perder señales que otros módulos usan
        val finales = fragmentosExpandidos.map { it.trim() }.filter { it.isNotBlank() }
        log("Fragmentos finales: ${finales.size}")
        return finales
    }
    
    /**
     * 🎯 DIVISIÓN INTELIGENTE POR SEPARADORES - VERSIÓN MEJORADA PARA 71 PRODUCTOS
     */
    private fun dividirPorSeparadores(linea: String): List<String> {
        // MEJORA CRÍTICA: Manejar casos como "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"
        
        // Paso 1: Separar por comas primero
        var fragmentos = linea.split(Regex("[,;]\\s*")).map { it.trim() }.filter { it.isNotBlank() }

        // Paso 1b: Expandir por enumeraciones internas "N." pegadas dentro del mismo fragmento
        val tmp = mutableListOf<String>()
        for (f in fragmentos) {
            val exp = expandirPorEnumeracionesPegadas(f)
            if (exp.isEmpty()) tmp.add(f) else tmp.addAll(exp)
        }
        fragmentos = tmp
        
        // Paso 2: Para cada fragmento, verificar si tiene múltiples productos pegados
        val fragmentosExpandidos = mutableListOf<String>()
        for (fragmento in fragmentos) {
            // CRÍTICO: Detectar múltiples cantidades pegadas como "6sandias8tomates" o "6 zanaorias 5 zapatos"
            val patronMultipleCantidades = Regex("(\\d+)\\s*([a-zA-Záéíóúüñ]+)\\s+(\\d+)\\s*([a-zA-Záéíóúüñ]+)", RegexOption.IGNORE_CASE)
            val matchMultiple = patronMultipleCantidades.findAll(fragmento).toList()
            
            if (matchMultiple.isNotEmpty() && matchMultiple.size == 1) {
                // Caso: "6 zanaorias 5 zapatos" -> separar en dos productos
                val match = matchMultiple[0]
                val cantidad1 = match.groupValues[1]
                val producto1 = match.groupValues[2]
                val cantidad2 = match.groupValues[3] 
                val producto2 = match.groupValues[4]
                
                fragmentosExpandidos.add("$cantidad1 $producto1")
                fragmentosExpandidos.add("$cantidad2 $producto2")
                log("Separados múltiples cantidades: '$cantidad1 $producto1' y '$cantidad2 $producto2'")
                continue
            }
            
            // CASO CRÍTICO ADICIONAL: "6sandias8tomates" (sin espacios entre números)
            val patronPegadoSinEspacios = Regex("(\\d+)([a-zA-Záéíóúüñ]+)(\\d+)([a-zA-Záéíóúüñ]+)", RegexOption.IGNORE_CASE)
            val matchPegado = patronPegadoSinEspacios.find(fragmento)
            
            if (matchPegado != null && !fragmento.contains('.')) { // No es numeración de lista
                val cantidad1 = matchPegado.groupValues[1]
                val producto1 = matchPegado.groupValues[2]
                val cantidad2 = matchPegado.groupValues[3] 
                val producto2 = matchPegado.groupValues[4]
                
                // Solo separar si ambos productos tienen al menos 3 caracteres
                if (producto1.length >= 3 && producto2.length >= 3) {
                    fragmentosExpandidos.add("$cantidad1 $producto1")
                    fragmentosExpandidos.add("$cantidad2 $producto2")
                    log("Separados pegados sin espacios: '$cantidad1 $producto1' y '$cantidad2 $producto2'")
                    continue
                }
            }
            
            fragmentosExpandidos.add(fragmento)
        }
        
        fragmentos = fragmentosExpandidos

        // Paso 3: (Deshabilitado por defecto) Fusión de continuaciones que empiezan con "de "
        // Mantener desactivado para no afectar enumeraciones; si se necesita, reactivar con condiciones más estrictas.
        // fragmentos = fusionarContinuacionesSiAplica(fragmentos)
        
        // Para cada fragmento, verificar si debe dividirse por "y" o contiene múltiples productos
        val fragmentosFinales = mutableListOf<String>()
        for (fragmento in fragmentos) {
            // MEJORA CRÍTICA: Manejar casos como "2 kg de tomates, 1 lechuga y pan (el más barato)"
            if (fragmento.contains(" y ", ignoreCase = true) && fragmento.contains(",")) {
                // Primero separar por comas, luego manejar "y"
                val partesPorComa = fragmento.split(",").map { it.trim() }
                for (parte in partesPorComa) {
                    if (parte.contains(" y ", ignoreCase = true)) {
                        val partesY = parte.split(Regex("\\s+y\\s+", RegexOption.IGNORE_CASE))
                        if (partesY.size == 2 && tieneCantidadesSeparadas(partesY[0], partesY[1])) {
                            fragmentosFinales.addAll(partesY.map { it.trim() })
                            log("Separadas partes con 'y': ${partesY.joinToString(" | ")}")
                        } else {
                            // Solo separar si son productos claramente diferentes
                            val tieneNumero1 = Regex("\\d+").containsMatchIn(partesY[0])
                            val tieneNumero2 = Regex("\\d+").containsMatchIn(partesY[1]) || 
                                             partesY[1].contains(Regex("\\b(el|la|los|las)\\s+(más|menos)\\b", RegexOption.IGNORE_CASE))
                            
                            if (tieneNumero1 || partesY.size > 2 || partesY[1].split(" ").size <= 4) {
                                fragmentosFinales.addAll(partesY.map { it.trim() })
                                log("Separadas partes diferentes: ${partesY.joinToString(" | ")}")
                            } else {
                                fragmentosFinales.add(parte)
                            }
                        }
                    } else {
                        fragmentosFinales.add(parte)
                    }
                }
            } else if (fragmento.contains(" y ", ignoreCase = true)) {
                // MEJORA CRÍTICA: División más inteligente por "y"
                val partesY = fragmento.split(Regex("\\s+y\\s+", RegexOption.IGNORE_CASE))
                if (partesY.size == 2) {
                    val parte1 = partesY[0].trim()
                    val parte2 = partesY[1].trim()
                    
                    // Casos donde SÍ dividir:
                    // 1. Ambas partes tienen cantidades separadas
                    val tieneNumero1 = Regex("\\d+").containsMatchIn(parte1)
                    val tieneNumero2 = Regex("\\d+").containsMatchIn(parte2)
                    
                    // 2. Una parte tiene cantidad explícita y la otra es un producto simple
                    val esProductoSimple1 = parte1.split(" ").size <= 3 && !tieneNumero1
                    val esProductoSimple2 = parte2.split(" ").size <= 3 && !tieneNumero2
                    
                    // 3. Ambas son productos simples y diferentes
                    val sonProductosSimples = esProductoSimple1 && esProductoSimple2
                    
                    // 4. Una parte tiene indicadores de preferencia como "el más barato"
                    val tienePreferencia1 = parte1.contains(Regex("\\b(el|la|los|las)\\s+(más|menos)\\b", RegexOption.IGNORE_CASE))
                    val tienePreferencia2 = parte2.contains(Regex("\\b(el|la|los|las)\\s+(más|menos)\\b", RegexOption.IGNORE_CASE))
                    
                    if ((tieneNumero1 && tieneNumero2) || 
                        (tieneNumero1 && esProductoSimple2) ||
                        (esProductoSimple1 && tieneNumero2) ||
                        (sonProductosSimples && parte1 != parte2) ||
                        tienePreferencia1 || tienePreferencia2) {
                        
                        fragmentosFinales.addAll(partesY.map { it.trim() })
                        log("Separadas por 'y': '$parte1' y '$parte2'")
                    } else {
                        // No dividir, es un producto compuesto como "jamón y queso"
                        fragmentosFinales.add(fragmento)
                    }
                } else {
                    // Múltiples productos unidos por "y"
                    if (partesY.size > 2 || tieneCantidadesSeparadas(partesY[0], partesY.getOrNull(1) ?: "")) {
                        fragmentosFinales.addAll(partesY.map { it.trim() })
                        log("Separadas múltiples 'y': ${partesY.joinToString(" | ")}")
                    } else {
                        fragmentosFinales.add(fragmento)
                    }
                }
            } else {
                fragmentosFinales.add(fragmento)
            }
        }
        
        return fragmentosFinales.map { it.trim() }.filter { it.isNotBlank() }
    }
    
    /**
     * 🆕 NUEVA FUNCIÓN: Verificar si hay cantidades separadas
     */
    private fun tieneCantidadesSeparadas(parte1: String, parte2: String): Boolean {
        val tieneNumero1 = Regex("\\d+").containsMatchIn(parte1)
        val tieneNumero2 = Regex("\\d+").containsMatchIn(parte2)
        return tieneNumero1 && tieneNumero2
    }

    // Divide un fragmento cuando hay enumeraciones internas como "...2.sandia...".
    // No divide en decimales (\d+\.\d+), solo cuando tras el punto NO hay dígito.
    private fun expandirPorEnumeracionesPegadas(fragmento: String): List<String> {
        val s = fragmento.trim()
        if (s.isEmpty()) return listOf()
        val partes = mutableListOf<String>()
        var i = 0
        val n = s.length
        while (i < n) {
            // Buscar próxima ocurrencia de \d+.
            val m = Regex("(\\d+)\\.").find(s, i)
            if (m == null) {
                partes.add(s.substring(i).trim())
                break
            }
            val start = m.range.first
            val end = m.range.last + 1 // posición después del punto
            if (start == i) {
                // Enumeración al inicio del segmento: continuar avanzando sin cortar
                i = end
                continue
            }
            // Verificar si es un decimal: caracter anterior es dígito y el siguiente es dígito
            val prevIsDigit = if (start - 1 >= 0) s[start - 1].isDigit() else false
            val nextIsDigit = if (end < n) s[end].isDigit() else false
            if (prevIsDigit && nextIsDigit) {
                // Parece decimal como 3.8 -> no cortar aquí, avanzar después del punto
                i = end
                continue
            }
            // Si no es decimal, cortar antes de la enumeración encontrada
            val chunk = s.substring(i, start).trim()
            if (chunk.isNotEmpty()) partes.add(chunk)
            i = start // el resto comenzará desde la enumeración
        }
        return partes.ifEmpty { listOf(s) }
    }
    
    /**
     * 🆕 NUEVA FUNCIÓN: Determinar si debe separar por punto
     */
    private fun debeSeprararPorPunto(fragmento: String): Boolean {
        // NO separar si contiene puntos suspensivos (...)
        if (fragmento.contains("...")) return false
        
        // NO separar si es una frase larga (más de 8 palabras sin números al inicio)
        val palabras = Regex("\\s+").split(fragmento).filter { it.isNotBlank() }
        if (palabras.size > 8) {
            val primeraPalabra = palabras.firstOrNull() ?: ""
            if (!Regex("\\d+").containsMatchIn(primeraPalabra)) {
                return false // Es una frase larga sin número al inicio
            }
        }
        
        // NO separar si contiene palabras que indican continuidad
        val palabrasContinuidad = setOf("preguntar", "cual", "cuál", "donde", "dónde", "como", "cómo", "que", "qué")
        if (palabrasContinuidad.any { fragmento.contains(it, ignoreCase = true) }) {
            return false
        }
        
        // SÍ separar si hay patrón claro de productos separados por punto
        // Ejemplo: "12 de plátanos. 3 manzanas"
        val patronProductosSeparados = Regex("\\d+[^.]*\\.[^.]*\\d+", RegexOption.IGNORE_CASE)
        if (patronProductosSeparados.containsMatchIn(fragmento)) {
            // Verificar que no sea decimal
            val patronDecimal = Regex("\\d+\\.\\d+", RegexOption.IGNORE_CASE)
            if (!patronDecimal.containsMatchIn(fragmento)) {
                return true // Es separación de productos, no decimal
            }
        }
        
        return false // Por defecto, no separar
    }

    /**
     * 🏷️ PROCESADOR DE LÍNEAS CON MARCA
     */
    private fun procesarLineaConMarca(linea: String): List<String> {
        val partes = linea.split(Regex("marca", RegexOption.IGNORE_CASE), 2)
        if (partes.size == 2) {
            val primeraParte = partes[0].trim().removeSuffix(",").trim()
            val marcasTexto = partes[1].trim().removePrefix(",").trim()
            
            // Extraer nota de marcas si existe
            val notaMatch = Regex("\\(([^)]+)\\)").find(marcasTexto)
            val marcasLimpias = if (notaMatch != null) {
                marcasTexto.replace(notaMatch.value, "").trim()
            } else {
                marcasTexto
            }
            
            val nota = notaMatch?.groupValues?.get(1)?.trim()
            val marcas = marcasLimpias.split(Regex("\\s+o\\s+|\\s*,\\s*"))
                .map { it.trim().removeSuffix(".") }
                .filter { it.isNotBlank() }
            
            // Crear producto con marcas
            val productoConMarcas = if (nota != null) {
                "$primeraParte (marcas: ${marcas.joinToString(", ")}) ($nota)"
            } else {
                "$primeraParte (marcas: ${marcas.joinToString(", ")})"
            }
            
            return listOf(productoConMarcas)
        }
        return listOf(linea)
    }

    /**
     * 🔪 SEPARAR MÚLTIPLES PRODUCTOS - VERSIÓN MEJORADA
     */
    private fun separarMultiplesProductos(fragmento: String): List<String> {
        log("separarMultiplesProductos evaluando: '$fragmento'")
        
        // CASO CRÍTICO 1: Numeración de lista detectada
        if (EnumerationAnalyzer.tieneSecuenciaEnumeracion(fragmento)) {
            log("✅ Es numeración de lista, procediendo a separar")
            
            // ENFOQUE GENERAL: Dividir por los números de lista detectados
            val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE).findAll(fragmento).toList()
            if (numerosConPunto.size >= 2) {
                val productos = mutableListOf<String>()
                for (i in numerosConPunto.indices) {
                    val inicioActual = numerosConPunto[i].range.first
                    val finActual = if (i < numerosConPunto.size - 1) {
                        numerosConPunto[i + 1].range.first
                    } else {
                        fragmento.length
                    }
                    
                    // Extraer el contenido del producto actual
                    val contenidoProducto = fragmento.substring(inicioActual, finActual).trim()
                    // Limpiar el número de lista del inicio
                    val productoLimpio = contenidoProducto.replace(Regex("^\\d+\\.\\s*"), "").trim()
                    
                    if (productoLimpio.isNotEmpty()) {
                        productos.add(productoLimpio)
                        log("📋 Producto ${i + 1}: '$productoLimpio'")
                    }
                }
                if (productos.size >= 2) {
                    log("📋 Separación exitosa: ${productos.size} productos")
                    return productos
                }
            }
            
            // Patrón para casos como "1.1papa mediana2.sandia grande"
            val patronDecimalPegado = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s*([a-zA-Záéíóúüñ\\s]*?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
            val matchDecimalPegado = patronDecimalPegado.find(fragmento)
            if (matchDecimalPegado != null) {
                val cantidadDecimal = matchDecimalPegado.groupValues[2]
                val nombreProducto1 = matchDecimalPegado.groupValues[3]
                val descripcion1 = matchDecimalPegado.groupValues[4].trim()
                val producto2 = matchDecimalPegado.groupValues[6].trim()
                
                val producto1Completo = if (descripcion1.isNotEmpty()) {
                    "${cantidadDecimal}.${nombreProducto1} $descripcion1"
                } else {
                    "${cantidadDecimal}.${nombreProducto1}"
                }
                
                log("📋 Separación decimal pegada: '$producto1Completo' y '$producto2'")
                return listOf(producto1Completo, producto2)
            }
            
            // Patrón general para otros casos de numeración
            val patronListaNumerada = Regex("(\\d+)\\.(.*?)(\\d+)\\.(.*)", RegexOption.IGNORE_CASE)
            val matchLista = patronListaNumerada.find(fragmento)
            if (matchLista != null) {
                val contenido1 = matchLista.groupValues[2].trim()
                val contenido2 = matchLista.groupValues[4].trim()
                if (contenido1.length >= 2 && contenido2.length >= 2) {
                    log("📋 Separación general: '$contenido1' y '$contenido2'")
                    return listOf(contenido1, contenido2)
                }
            }
        } else {
            log("❌ NO es numeración de lista, verificando cantidades decimales")
        }
        
        // CASO CRÍTICO 2: "1.2metros de madera 3.8 metros de cable" (NO separar - son cantidades)
        if (fragmento.contains(Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?)"))) {
            log("🚫 Contiene cantidades decimales reales - NO separar")
            return listOf(fragmento)
        }
        
        // CASO 1: Detectar cantidades pegadas como "6sandias8tomates" (SIN numeración de lista)
        // Solo aplicar si NO hay patrones de numeración de lista
        if (!fragmento.contains(Regex("\\d+\\.[a-zA-Z]"))) {
            val patronCantidadPegada = Regex("(\\d+(?:\\.\\d+)?)([a-zA-Záéíóúüñ\\s]+?)(\\d+(?:\\.\\d+)?)([a-zA-Záéíóúüñ\\s]+)", RegexOption.IGNORE_CASE)
            val matchPegado = patronCantidadPegada.find(fragmento)
            if (matchPegado != null) {
                val cantidad1 = matchPegado.groupValues[1]
                val producto1 = matchPegado.groupValues[2].trim()
                val cantidad2 = matchPegado.groupValues[3]
                val producto2 = matchPegado.groupValues[4].trim()
                
                // Solo separar si ambos productos tienen contenido sustancial
                if (producto1.length >= 3 && producto2.length >= 3) {
                    log("📋 Separación cantidades pegadas: '$cantidad1 $producto1' y '$cantidad2 $producto2'")
                    return listOf("$cantidad1 $producto1", "$cantidad2 $producto2")
                }
            }
        }
        
        // CASO 2: Detectar productos separados por espacio con cantidades claras
        // "500gr de carne molida 1 paquete de espaguetis"
        val patronDosCantidades = Regex("(\\d+\\w*\\s+[^\\d]+?)\\s+(\\d+\\w*\\s+.+)", RegexOption.IGNORE_CASE)
        val matchDos = patronDosCantidades.find(fragmento)
        if (matchDos != null) {
            val producto1 = matchDos.groupValues[1].trim()
            val producto2 = matchDos.groupValues[2].trim()
            
            // Solo separar si son productos diferentes y sustanciales
            // NO separar si contiene palabras de continuidad
            if (producto1.length > 8 && producto2.length > 8 && 
                !producto1.contains("preguntar", ignoreCase = true) && 
                !producto2.contains("preguntar", ignoreCase = true) && 
                !fragmento.contains("...")) {
                log("📋 Separación dos cantidades: '$producto1' y '$producto2'")
                return listOf(producto1, producto2)
            }
        }
        
        // CASO 3: Detectar múltiples productos como "cinco focos ocho rollos" o "seis tomates ocho papas"
        val patronMultiple = Regex("(\\d+|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce)\\s+([a-zA-Záéíóúüñ]+)\\s+(\\d+|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce)\\s+([a-zA-Záéíóúüñ]+)", RegexOption.IGNORE_CASE)
        val matchMultiple = patronMultiple.find(fragmento)
        if (matchMultiple != null) {
            val cantidad1Texto = matchMultiple.groupValues[1]
            val nombre1 = matchMultiple.groupValues[2]
            val cantidad2Texto = matchMultiple.groupValues[3]
            val nombre2 = matchMultiple.groupValues[4]
            
            // Convertir cantidades si están en palabras
            val cantidad1 = cantidad1Texto.toDoubleOrNull() ?: TextPreprocessor.convertirPalabraANumero(cantidad1Texto)
            val cantidad2 = cantidad2Texto.toDoubleOrNull() ?: TextPreprocessor.convertirPalabraANumero(cantidad2Texto)
            
            // Verificar que son productos válidos y diferentes
            if (nombre1.length >= 3 && nombre2.length >= 3 && nombre1 != nombre2) {
                log("🔄 Separación múltiple palabras/números: '${cantidad1.toInt()} $nombre1' y '${cantidad2.toInt()} $nombre2'")
                return listOf("${cantidad1.toInt()} $nombre1", "${cantidad2.toInt()} $nombre2")
            }
        }
        
        // CASO 4: Múltiples productos pegados como "4cepillos,12servilletas,3 desodorantes 7 calcetines"
        // Primero verificar si ya fue separado por comas en dividirPorSeparadores
        if (!fragmento.contains(",")) {
            // Detectar múltiples cantidades en una sola línea como "3 desodorantes 7 calcetines"
            val patronMultipleEnLinea = Regex("(\\d+)\\s*([a-zA-Záéíóúüñ]+)\\s+(\\d+)\\s*([a-zA-Záéíóúüñ]+)(?:\\s+(\\d+)\\s*([a-zA-Záéíóúüñ]+))?", RegexOption.IGNORE_CASE)
            val matchMultipleLinea = patronMultipleEnLinea.find(fragmento)
            if (matchMultipleLinea != null) {
                val cantidad1 = matchMultipleLinea.groupValues[1]
                val producto1 = matchMultipleLinea.groupValues[2]
                val cantidad2 = matchMultipleLinea.groupValues[3]
                val producto2 = matchMultipleLinea.groupValues[4]
                
                val productos = mutableListOf<String>()
                productos.add("$cantidad1 $producto1")
                productos.add("$cantidad2 $producto2")
                
                // Verificar si hay un tercer producto
                if (matchMultipleLinea.groupValues[5].isNotEmpty() && matchMultipleLinea.groupValues[6].isNotEmpty()) {
                    val cantidad3 = matchMultipleLinea.groupValues[5]
                    val producto3 = matchMultipleLinea.groupValues[6]
                    productos.add("$cantidad3 $producto3")
                }
                
                log("🔄 Separación múltiple en línea: ${productos.joinToString(" | ")}")
                return productos
            }
        }
        
        // CASO 5: Cantidades pegadas múltiples como "4cepillos12servilletas" o "4 cepillos 12 servilletas"
        val patron = Regex("(\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ\\s]+?)(?=\\d|$)", RegexOption.IGNORE_CASE)
        val matches = patron.findAll(fragmento).toList()
        if (matches.size > 1) {
            // Múltiples patrones, separarlos
            val resultado = mutableListOf<String>()
            for (match in matches) {
                val cantidad = match.groupValues[1]
                val producto = match.groupValues[2].trim()
                // Solo agregar si el producto tiene contenido sustancial
                if (producto.length >= 3) {
                    resultado.add("$cantidad $producto")
                }
            }
            // Solo retornar la separación si encontramos múltiples productos válidos
            if (resultado.size > 1) {
                log("🔄 Separación múltiples cantidades: ${resultado.joinToString(" | ")}")
                return resultado
            }
        }
        
        // Por defecto, no separar
        log("🔄 Sin separación, retornando fragmento original: '$fragmento'")
        return listOf(fragmento)
    }
}
