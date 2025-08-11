package com.listafacilnueva.parser

import com.listafacilnueva.model.Producto

object QuantityParser {

    // FUNCIÓN DE MEJORAS INTEGRADA - Aplica las mejoras más efectivas de la demostración
    private fun aplicarMejorasAlTexto(texto: String): String {
        var resultado = texto
        
        // MEJORA 1: Corregir decimales con punto final
        val patronDecimalConPuntoFinal = Regex("(\\d+\\.\\d+)\\.$")
        if (resultado.contains(patronDecimalConPuntoFinal)) {
            resultado = resultado.replace(patronDecimalConPuntoFinal, "$1")
        }
        
        // MEJORA 2: Separar decimales pegados
        val patronDecimalesPegados = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ]{3,})")
        if (resultado.contains(patronDecimalesPegados)) {
            resultado = resultado.replace(patronDecimalesPegados, "$1, $2")
        }
        
        // MEJORA 3: Separar productos consecutivos con decimales
        val patronProductosConsecutivos = Regex("(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+\\.\\d+)([a-zA-Záéíóúüñ\\s]+)")
        if (resultado.contains(patronProductosConsecutivos)) {
            resultado = resultado.replace(patronProductosConsecutivos, "$1 $2, $3 $4")
        }
        
        // MEJORA 4: Separar cantidades pegadas sin decimales
        val patronCantidadesPegadas = Regex("(\\d+)([a-zA-Záéíóúüñ]{3,})(\\d+)([a-zA-Záéíóúüñ]{3,})")
        if (resultado.contains(patronCantidadesPegadas)) {
            resultado = resultado.replace(patronCantidadesPegadas, "$1 $2, $3 $4")
        }
        
        // MEJORA 5: Limpiar espacios múltiples y comas repetidas
        resultado = resultado.replace(Regex("\\s+"), " ")
        resultado = resultado.replace(Regex(",+"), ",")
        resultado = resultado.trim()
        
        return resultado
    }

    fun parse(texto: String): List<Producto> {
        val textoConMejoras = aplicarMejorasAlTexto(texto)
        val productos = mutableListOf<Producto>()
        val textoNormalizado = normalizarNumeros(textoConMejoras)
        val lineas = textoNormalizado.split(Regex("[\n;]+")).filter { it.isNotBlank() }
        
        for ((indice, linea) in lineas.withIndex()) {
            if (esLineaBasura(linea)) continue
            
            val lineaConCantidadImplicita = extraerCantidadImplicita(linea, indice, lineas)
            val productosDecimales = analizarCantidadesDecimales(lineaConCantidadImplicita)
            if (productosDecimales.isNotEmpty()) {
                productos.addAll(productosDecimales)
                continue
            }
            
            val lineaLimpia = limpiarNumeracionLista(limpiarNumeracionCompuesta(lineaConCantidadImplicita))
            val fragmentos = dividirEnFragmentos(lineaLimpia)
            
            for (fragmento in fragmentos) {
                val producto = procesarFragmento(fragmento)
                if (producto != null && esProductoValido(producto)) {
                    productos.add(producto)
                }
            }
        }
        
        return productos
    }

    // NUEVA FUNCIÓN: Extraer cantidad implícita del contexto
    private fun extraerCantidadImplicita(linea: String, indice: Int, todasLineas: List<String>): String {
        // Caso: "2 lechugas francesas preguntar...cual son..."
        // La cantidad puede estar al inicio de la línea pero perderse en el procesamiento
        
        // Verificar si la línea original tiene cantidad al inicio
        val patronCantidadInicio = Regex("^(\\d+)\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchInicio = patronCantidadInicio.find(linea)
        
        if (matchInicio != null) {
            val cantidad = matchInicio.groupValues[1]
            val resto = matchInicio.groupValues[2]
            
            // Si el resto contiene indicadores de continuación como "preguntar", mantener la cantidad
            if (resto.contains("preguntar", ignoreCase = true) || resto.contains("...", ignoreCase = true)) {
                // Limpiar la parte problemática pero mantener la estructura principal
                val restoLimpio = resto
                    .replace(Regex("preguntar[^.]*\\.\\.\\..*"), "")
                    .trim()
                
                if (restoLimpio.isNotEmpty()) {
                    return "$cantidad $restoLimpio"
                }
            }
        }
        
        return linea
    }

    // VERSIÓN 100% OPTIMIZADA: Detecta numeración de lista vs cantidades decimales
    private fun tieneSecuenciaEnumeracion(texto: String): Boolean {
        println("🔍 ANALIZANDO: '$texto'")
        
        // VERIFICACIÓN PRIMERA: Detectar secuencias de numeración válidas ANTES de rechazar por decimales
        // CASO CRÍTICO: "1.2metros cable 2.bombilla grande 3.zapallo chino" (DEBE ser numeración)
        
        // Enfoque simplificado: buscar todos los números seguidos de punto
        val numerosConPunto = Regex("\\b(\\d+)\\.", RegexOption.IGNORE_CASE).findAll(texto).map { it.groupValues[1].toInt() }.toList()
        
        if (numerosConPunto.size >= 2) {
            // Verificar si es una secuencia consecutiva
            var esSecuenciaConsecutiva = true
            for (i in 1 until numerosConPunto.size) {
                if (numerosConPunto[i] != numerosConPunto[i-1] + 1) {
                    esSecuenciaConsecutiva = false
                    break
                }
            }
            
            if (esSecuenciaConsecutiva && numerosConPunto[0] in 1..10) { // Rango razonable para numeración de lista
                println("🔍 NUMERACIÓN DE LISTA DETECTADA: ${numerosConPunto.size} productos (${numerosConPunto.joinToString(", ")})")
                return true
            }
        }
        
        // VERIFICACIÓN CRÍTICA SEGUNDA: Detectar cantidades decimales reales (solo si NO es numeración)
        // Casos como "1.2metros de madera 3.8 metros de cable" NO son numeración
        val tieneDecimalesConEspacios = Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?|gr|ml|cm|mm)\\b", RegexOption.IGNORE_CASE)
        if (tieneDecimalesConEspacios.containsMatchIn(texto)) {
            println("🔍 CANTIDADES DECIMALES REALES DETECTADAS - NO es numeración")
            return false
        }
        
        // VERIFICACIÓN ADICIONAL: Si contiene múltiples patrones decimal+unidad, es cantidad real
        val patronDecimalUnidad = Regex("\\d+\\.\\d+\\s*[a-zA-Záéíóúüñ]+", RegexOption.IGNORE_CASE)
        val matchesDecimalUnidad = patronDecimalUnidad.findAll(texto).toList()
        if (matchesDecimalUnidad.size >= 2) {
            println("🔍 MÚLTIPLES CANTIDADES DECIMALES - NO es numeración")
            return false
        }
        
        // CASO CRÍTICO 1: "1.2metros cable2.bombilla grande" (DEBE ser numeración)
        // Detectar patrón específico: número.cantidad+texto+número.texto
        val patronEnumeracionCritica = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchCritica = patronEnumeracionCritica.find(texto.trim())
        
        if (matchCritica != null) {
            val numeroLista1 = matchCritica.groupValues[1].toIntOrNull()
            val cantidad = matchCritica.groupValues[2]
            val unidad = matchCritica.groupValues[3]
            val producto1Resto = matchCritica.groupValues[4].trim()
            val numeroLista2 = matchCritica.groupValues[5].toIntOrNull()
            val producto2 = matchCritica.groupValues[6].trim()
            
            // CRÍTICO: NO tratar como numeración si la "unidad" es una unidad de medida real
            val unidadesReales = setOf("metros", "metro", "kg", "litros", "litro", "gramos", "gr", "ml", "cm", "mm")
            if (unidadesReales.contains(unidad.lowercase())) {
                println("🔍 UNIDAD REAL DETECTADA ($unidad) - NO es numeración")
                return false
            }
            
            // Verificar secuencia de lista válida (1->2, 2->3, etc.)
            if (numeroLista1 != null && numeroLista2 != null && 
                numeroLista2 == numeroLista1 + 1 &&
                producto1Resto.isNotEmpty() && producto2.isNotEmpty()) {
                println("🔍 CASO CRÍTICO DETECTADO: Lista $numeroLista1.$cantidad$unidad $producto1Resto + $numeroLista2.$producto2")
                return true
            }
        }
        
        // CASO CRÍTICO NUEVO: "1.1papa mediana2.sandia grande" (numeración con cantidad decimal pegada)
        val patronDecimalPegado = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s*([a-zA-Záéíóúüñ\\s]*?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchDecimalPegado = patronDecimalPegado.find(texto.trim())
        
        if (matchDecimalPegado != null) {
            val numeroLista1 = matchDecimalPegado.groupValues[1].toIntOrNull()
            val cantidadDecimal = matchDecimalPegado.groupValues[2]
            val nombreProducto1 = matchDecimalPegado.groupValues[3]
            val descripcion1 = matchDecimalPegado.groupValues[4].trim()
            val numeroLista2 = matchDecimalPegado.groupValues[5].toIntOrNull()
            val producto2 = matchDecimalPegado.groupValues[6].trim()
            
            // Verificar secuencia de lista válida
            if (numeroLista1 != null && numeroLista2 != null && 
                numeroLista2 == numeroLista1 + 1 &&
                nombreProducto1.isNotEmpty() && producto2.isNotEmpty()) {
                println("🔍 NUMERACIÓN DECIMAL PEGADA: $numeroLista1.$cantidadDecimal$nombreProducto1 $descripcion1 + $numeroLista2.$producto2")
                return true
            }
        }
        
        // CASO CRÍTICO 2: "1.2metros de madera 3.8 metros de cable" (NO debe ser numeración)
        // Detectar cantidades decimales válidas
        val tieneDecimalesReales = texto.contains(Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?)"))
        if (tieneDecimalesReales) {
            println("🔍 CANTIDADES DECIMALES DETECTADAS - NO es numeración")
            return false
        }
        
        // CASO 3: Numeración pegada estándar "1.producto2.producto"
        val patronNumeracionPegada = Regex("^(\\d+)\\.(\\w[a-zA-Záéíóúüñ\\s]*?)(\\d+)\\.(\\w[a-zA-Záéíóúüñ\\s]*)$", RegexOption.IGNORE_CASE)
        val matchPegada = patronNumeracionPegada.find(texto.trim())
        
        if (matchPegada != null) {
            val numero1 = matchPegada.groupValues[1].toIntOrNull()
            val contenido1 = matchPegada.groupValues[2].trim()
            val numero2 = matchPegada.groupValues[3].toIntOrNull()
            val contenido2 = matchPegada.groupValues[4].trim()
            
            // Verificar secuencia de numeración válida y contenido sustancial
            if (numero1 != null && numero2 != null &&
                numero2 == numero1 + 1 &&  // Secuencia consecutiva
                contenido1.length >= 2 && contenido2.length >= 2 &&
                !contenido1.contains(Regex("\\d+\\.\\d+")) &&  // Sin decimales en contenido
                !contenido2.contains(Regex("\\d+\\.\\d+"))) {
                
                println("🔍 NUMERACIÓN ESTÁNDAR: $numero1.$contenido1 + $numero2.$contenido2")
                return true
            }
        }
        
        // CASO 4: Múltiples numeraciones separadas
        val patronSeparado = Regex("\\d+\\.[a-zA-Záéíóúüñ]", RegexOption.IGNORE_CASE)
        val matches = patronSeparado.findAll(texto).toList()
        
        if (matches.size >= 2) {
            // Verificar que NO contiene unidades decimales
            if (!texto.contains(Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?)"))) {
                println("🔍 NUMERACIÓN MÚLTIPLE: ${matches.size} elementos")
                return true
            }
        }
        
        println("🔍 NO ES NUMERACIÓN: '$texto'")
        return false
    }
    
    // VERSIÓN 100% OPTIMIZADA: Limpieza inteligente de numeración compuesta
    private fun limpiarNumeracionCompuesta(linea: String): String {
        // MEJORA CRÍTICA: Solo aplicar si detectamos secuencia válida
        if (!tieneSecuenciaEnumeracion(linea)) {
            println("🔄 NO es secuencia válida, sin modificación: '$linea'")
            return linea // No aplicar transformación, tratar como cantidad normal
        }
        
        // CASO CRÍTICO ESPECÍFICO: "1.2metros cable 2.bombilla grande 3.zapallo chino"
        // Patrón para 3 productos con cantidad en el primero
        val patron3Productos = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)\\s+(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val match3Prod = patron3Productos.find(linea)
        
        if (match3Prod != null) {
            val cantidad1 = match3Prod.groupValues[2]
            val unidad1 = match3Prod.groupValues[3]
            val nombre1 = match3Prod.groupValues[4].trim()
            val nombre2 = match3Prod.groupValues[6].trim()
            val nombre3 = match3Prod.groupValues[8].trim()
            
            val resultado = "$cantidad1$unidad1 $nombre1, $nombre2, $nombre3"
            println("🧠 Transformación 3 productos: '$resultado'")
            return resultado
        }
        
        // CASO CRÍTICO: "1.2.5kg arroz2.1.8litros agua3.bombillas led"
        // Patrón para numeración con cantidades decimales complejas
        val patronDecimalComplejo = Regex("^(\\d+)\\.(\\d+\\.\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)(\\d+)\\.(\\d+\\.\\d+)([a-zA-Záéíóúüñ]+)\\s+([a-zA-Záéíóúüñ\\s]+?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchComplejo = patronDecimalComplejo.find(linea)
        
        if (matchComplejo != null) {
            val cantidad1 = matchComplejo.groupValues[2]  // "2.5"
            val unidad1 = matchComplejo.groupValues[3]     // "kg"
            val nombre1 = matchComplejo.groupValues[4].trim()  // "arroz"
            val cantidad2 = matchComplejo.groupValues[6]  // "1.8"
            val unidad2 = matchComplejo.groupValues[7]     // "litros"
            val nombre2 = matchComplejo.groupValues[8].trim()  // "agua"
            val nombre3 = matchComplejo.groupValues[10].trim() // "bombillas led"
            
            val resultado = "$cantidad1$unidad1 $nombre1, $cantidad2$unidad2 $nombre2, $nombre3"
            println("🧠 Transformación compleja: '$resultado'")
            return resultado
        }
        
        // CASO CRÍTICO NUEVO: "1.1papa mediana2.sandia grande" (cantidad decimal pegada)
        val patronDecimalPegado = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s*([a-zA-Záéíóúüñ\\s]*?)(\\d+)\\.([a-zA-Záéíóúüñ\\s]+)$", RegexOption.IGNORE_CASE)
        val matchDecimalPegado = patronDecimalPegado.find(linea)
        
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
            
            val resultado = "$producto1Completo, $producto2"
            println("🧠 Transformación decimal pegada: '$resultado'")
            return resultado
        }
        
        // IMPORTANTE: Si es un caso simple pegado, dejarlo para separarMultiplesProductos
        val patronSimplePegado = Regex("^\\d+\\.\\w[^\\d]*\\d+\\.\\w", RegexOption.IGNORE_CASE)
        if (patronSimplePegado.containsMatchIn(linea)) {
            println("🔄 DELEGANDO a separarMultiplesProductos: '$linea'")
            return linea // Dejar que separarMultiplesProductos lo maneje
        }
        // Patrón 1: 1.cantidad+unidad+producto+número.producto
        val patron1 = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ]+)\\s+([^\\d]+?)(\\d+)\\.(.+)$", RegexOption.IGNORE_CASE)
        val match1 = patron1.find(linea)
        
        if (match1 != null) {
            val cantidad1 = match1.groupValues[2]
            val unidad1 = match1.groupValues[3]
            val nombre1 = match1.groupValues[4].trim()
            val nombre2 = match1.groupValues[6].trim()
            return "$cantidad1 $unidad1 $nombre1, $nombre2"
        }
        
        // Patrón 2: 1.cantidad+producto+número.producto
        val patron2 = Regex("^(\\d+)\\.(\\d+)([a-zA-Záéíóúüñ\\s]+?)(\\d+)\\.(.+)$", RegexOption.IGNORE_CASE)
        val match2 = patron2.find(linea)
        
        if (match2 != null) {
            val cantidad1 = match2.groupValues[2]
            val nombre1 = match2.groupValues[3].trim()
            val nombre2 = match2.groupValues[5].trim()
            return "$cantidad1 $nombre1, $nombre2"
        }
        
        // Patrón 3: 1.producto+cantidad+unidad+número.producto
        val patron3 = Regex("^(\\d+)\\.([a-zA-Záéíóúüñ\\s]+?)(\\d+)([a-zA-Záéíóúüñ]*)(\\d+)\\.(.+)$", RegexOption.IGNORE_CASE)
        val match3 = patron3.find(linea)
        
        if (match3 != null) {
            val nombre1 = match3.groupValues[2].trim()
            val cantidad1 = match3.groupValues[3]
            val unidad1 = match3.groupValues[4]
            val nombre2 = match3.groupValues[6].trim()
            
            val producto1 = if (unidad1.isNotBlank()) {
                "$nombre1 $cantidad1$unidad1"
            } else {
                "$cantidad1 $nombre1"
            }.trim()
            
            return "$producto1, $nombre2"
        }
        
        // Patrón 4: Múltiples enumeraciones simples pegadas (mejorado)
        // Maneja casos como "1.salmon 1kg2.zanaorias" Y "1.2metros cable2.bombilla grande"
        val patron4 = Regex("^(\\d+)\\.([^\\d]*\\d*[^\\d]*?)(\\d+)\\.(.+)$", RegexOption.IGNORE_CASE)
        val match4 = patron4.find(linea)
        
        if (match4 != null) {
            val producto1 = match4.groupValues[2].trim() // "salmon 1kg" o "2metros cable"
            val producto2 = match4.groupValues[4].trim() // "zanaorias" o "bombilla grande"
            
            // Verificar que ambos productos tienen contenido válido
            if (producto1.length >= 2 && producto2.length >= 2) {
                return "$producto1, $producto2"
            }
        }
        
        return linea
    }
    
    private fun limpiarNumeracionLista(linea: String): String {
        // CORRECCIÓN CRÍTICA: NO eliminar decimales reales como "2.5 metros"
        // Solo eliminar numeración de lista como "1. producto", "2. producto"
        
        // Verificar si es un decimal real (número.decimal seguido de unidad o espacio+texto)
        val esDecimalReal = Regex("^\\d+\\.\\d+(\\s|[a-zA-Záéíóúüñ])", RegexOption.IGNORE_CASE).find(linea) != null
        if (esDecimalReal) {
            println("🔒 Preservando cantidad decimal: '$linea'")
            return linea.replace(Regex("\\.$"), "").trim() // Solo quitar punto final
        }
        
        // Limpiar numeración simple como "1.", "2." (solo si NO es decimal)
        return linea.replace(Regex("^\\d+\\.\\s*"), "")
                   .replace(Regex("\\.$"), "")
                   .trim()
    }

    private fun dividirEnFragmentos(linea: String): List<String> {
        // Manejar líneas con "marca" primero
        if (linea.contains("marca", ignoreCase = true)) {
            return procesarLineaConMarca(linea)
        }
        
        // MEJORA: Segmentación inteligente por conjunciones
        var fragmentos = dividirPorSeparadores(linea)
        
        // Separar cantidades pegadas y múltiples productos
        val fragmentosExpandidos = mutableListOf<String>()
        for (frag in fragmentos) {
            val separados = separarMultiplesProductos(frag)
            fragmentosExpandidos.addAll(separados)
        }
        
        return fragmentosExpandidos
    }
    
    // NUEVA FUNCIÓN: División inteligente por separadores - VERSIÓN MEJORADA PARA 69 PRODUCTOS
    private fun dividirPorSeparadores(linea: String): List<String> {
        // MEJORA CRÍTICA: Manejar casos como "leche asadas ,6sandias,8tomates,6 zanaorias 5 zapatos"
        
        // Paso 1: Separar por comas primero
        var fragmentos = linea.split(Regex("[,;]\\s*")).map { it.trim() }.filter { it.isNotBlank() }
        
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
                    continue
                }
            }
            
        // CASO CRÍTICO 3: Detectar múltiples productos con cantidades al final del fragmento
        // "3 desodorantes 7 calcetines" donde hay más de 2 números
        val patronMultipleComplejo = Regex("(.+?)(\\d+)\\s*([a-zA-Záéíóúüñ]+)\\s+(\\d+)\\s*([a-zA-Záéíóúüñ]+)$", RegexOption.IGNORE_CASE)
        val matchComplejo = patronMultipleComplejo.find(fragmento)
        
        if (matchComplejo != null) {
            val inicio = matchComplejo.groupValues[1].trim()
            val cantidad1 = matchComplejo.groupValues[2]
            val producto1 = matchComplejo.groupValues[3]
            val cantidad2 = matchComplejo.groupValues[4]
            val producto2 = matchComplejo.groupValues[5]
            
            // Si hay contenido al inicio, incluirlo como producto separado
            if (inicio.isNotEmpty() && inicio.length >= 3) {
                fragmentosExpandidos.add(inicio)
            }
            fragmentosExpandidos.add("$cantidad1 $producto1")
            fragmentosExpandidos.add("$cantidad2 $producto2")
            continue
        }
        
        // MEJORA CRÍTICA: Detectar múltiples productos con formato (x#)
        val patronMultipleX = Regex("(.+?\\(x\\d+\\))(?:\\s*,\\s*|$)", RegexOption.IGNORE_CASE)
        val matchesX = patronMultipleX.findAll(fragmento).toList()
        
        if (matchesX.size > 1) {
            // Separar productos con formato (x#)
            for (match in matchesX) {
                val producto = match.groupValues[1].trim()
                if (producto.isNotEmpty()) {
                    fragmentosExpandidos.add(producto)
                }
            }
        } else {
            // Solo dividir por punto si hay patrón claro: "número texto. número texto"
            val patronProductosSeparados = Regex("(\\d+[^.]*?)\\s*\\.\\s*(\\d+[^.]*)", RegexOption.IGNORE_CASE)
            val matchProductos = patronProductosSeparados.find(fragmento)
            
            if (matchProductos != null && !fragmento.contains("...") && !fragmento.contains("preguntar", ignoreCase = true)) {
                val producto1 = matchProductos.groupValues[1].trim()
                val producto2 = matchProductos.groupValues[2].trim()
                if (producto1.length > 3 && producto2.length > 3) {
                    fragmentosExpandidos.addAll(listOf(producto1, producto2))
                    continue
                }
            }
            
            fragmentosExpandidos.add(fragmento)
        }
        }
        
        fragmentos = fragmentosExpandidos
        
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
                        } else {
                            // Solo separar si son productos claramente diferentes
                            val tieneNumero1 = Regex("\\d+").containsMatchIn(partesY[0])
                            val tieneNumero2 = Regex("\\d+").containsMatchIn(partesY[1]) || 
                                             partesY[1].contains(Regex("\\b(el|la|los|las)\\s+(más|menos)\\b", RegexOption.IGNORE_CASE))
                            
                            if (tieneNumero1 || partesY.size > 2 || partesY[1].split(" ").size <= 4) {
                                fragmentosFinales.addAll(partesY.map { it.trim() })
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
                    } else {
                        // No dividir, es un producto compuesto como "jamón y queso"
                        fragmentosFinales.add(fragmento)
                    }
                } else {
                    // Múltiples productos unidos por "y"
                    if (partesY.size > 2 || tieneCantidadesSeparadas(partesY[0], partesY.getOrNull(1) ?: "")) {
                        fragmentosFinales.addAll(partesY.map { it.trim() })
                    } else {
                        fragmentosFinales.add(fragmento)
                    }
                }
            } else {
                fragmentosFinales.add(fragmento)
            }
        }
        
        return fragmentosFinales.filter { it.isNotBlank() }
    }
    
    // NUEVA FUNCIÓN: Verificar si hay cantidades separadas
    private fun tieneCantidadesSeparadas(parte1: String, parte2: String): Boolean {
        val tieneNumero1 = Regex("\\d+").containsMatchIn(parte1)
        val tieneNumero2 = Regex("\\d+").containsMatchIn(parte2)
        return tieneNumero1 && tieneNumero2
    }
    
    // NUEVA FUNCIÓN: Determinar si debe separar por punto
    private fun debeSeprararPorPunto(fragmento: String): Boolean {
        // NO separar si contiene puntos suspensivos (...)
        if (fragmento.contains("...")) return false
        
        // NO separar si es una frase larga (más de 8 palabras sin números al inicio)
        val palabras = fragmento.split("\\s+").filter { it.isNotBlank() }
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

    private fun separarMultiplesProductos(fragmento: String): List<String> {
        println("🔍 separarMultiplesProductos evaluando: '$fragmento'")
        
        // CASO CRÍTICO 1: Numeración de lista detectada
        if (tieneSecuenciaEnumeracion(fragmento)) {
            println("✅ Es numeración de lista, procediendo a separar")
            
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
                        println("📋 Producto ${i + 1}: '$productoLimpio'")
                    }
                }
                
                if (productos.size >= 2) {
                    println("📋 Separación exitosa: ${productos.size} productos")
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
                
                println("📋 Separación decimal pegada: '$producto1Completo' y '$producto2'")
                return listOf(producto1Completo, producto2)
            }
            
            // Patrón general para otros casos de numeración
            val patronListaNumerada = Regex("(\\d+)\\.(.*?)(\\d+)\\.(.*)", RegexOption.IGNORE_CASE)
            val matchLista = patronListaNumerada.find(fragmento)
            
            if (matchLista != null) {
                val contenido1 = matchLista.groupValues[2].trim()
                val contenido2 = matchLista.groupValues[4].trim()
                
                if (contenido1.length >= 2 && contenido2.length >= 2) {
                    println("📋 Separación general: '$contenido1' y '$contenido2'")
                    return listOf(contenido1, contenido2)
                }
            }
        } else {
            println("❌ NO es numeración de lista, verificando cantidades decimales")
        }
        
        // CASO CRÍTICO 2: "1.2metros de madera 3.8 metros de cable" (NO separar - son cantidades)
        if (fragmento.contains(Regex("\\d+\\.\\d+\\s+(metros?|kg|litros?|gramos?)"))) {
            println("🚫 Contiene cantidades decimales reales - NO separar")
            return listOf(fragmento)
        }
        
        // CASO PRIORITARIO: Detectar numeración de lista pegada como "1.2metros cable2.bombilla grande"
        // Solo aplicar si hemos detectado que es una secuencia de numeración válida
        if (tieneSecuenciaEnumeracion(fragmento)) {
            println("✅ Es numeración de lista, procediendo a separar")
            // Sugerencia implementada: Regex más flexible para capturar cualquier contenido
            val patronListaNumerada = Regex("(\\d+)\\.(.*?)(\\d+)\\.(.*)", RegexOption.IGNORE_CASE)
            val matchLista = patronListaNumerada.find(fragmento)
            
            if (matchLista != null) {
                val numero1 = matchLista.groupValues[1]
                val contenido1 = matchLista.groupValues[2].trim()
                val numero2 = matchLista.groupValues[3]
                val contenido2 = matchLista.groupValues[4].trim()
                
                // Verificar que ambos contenidos son sustanciales
                if (contenido1.length >= 2 && contenido2.length >= 2) {
                    println("📋 Separando numeración: '$contenido1' y '$contenido2'")
                    return listOf(contenido1, contenido2)
                }
            }
        } else {
            println("❌ NO es numeración de lista, verificando otros casos")
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
            val cantidad1 = cantidad1Texto.toDoubleOrNull() ?: convertirPalabraANumero(cantidad1Texto)
            val cantidad2 = cantidad2Texto.toDoubleOrNull() ?: convertirPalabraANumero(cantidad2Texto)
            
            // Verificar que son productos válidos y diferentes
            if (nombre1.length >= 3 && nombre2.length >= 3 && nombre1 != nombre2) {
                println("🔄 Separación múltiple palabras/números: '${cantidad1.toInt()} $nombre1' y '${cantidad2.toInt()} $nombre2'")
                return listOf("${cantidad1.toInt()} $nombre1", "${cantidad2.toInt()} $nombre2")
            }
        }
        
        // CASO CRÍTICO 4: Múltiples productos pegados como "4cepillos,12servilletas,3 desodorantes 7 calcetines"
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
                
                println("🔄 Separación múltiple en línea: ${productos.joinToString(" | ")}")
                return productos
            }
        }
        
        // CASO 4: Cantidades pegadas múltiples como "4cepillos12servilletas" o "4 cepillos 12 servilletas"
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
                return resultado
            }
        }
        
        // Por defecto, no separar
        println("🔄 Sin separación, retornando fragmento original: '$fragmento'")
        return listOf(fragmento)
    }

    // FUNCIÓN 100% NUEVA: Análisis inteligente de cantidades decimales
    private fun analizarCantidadesDecimales(texto: String): List<Producto> {
        println("🔢 Analizando cantidades decimales en: '$texto'")
        
        // CASO CRÍTICO: "1.2metros de madera 3.8 metros de cable" 
        // Patrón mejorado para detectar múltiples cantidades decimales con unidades
        val patronDecimalesConUnidades = Regex("(\\d+\\.\\d+)\\s*(metros?|kg|gramos?|g|litros?|l|ml|cm|mm|bolsas?|paquetes?)\\s+(?:de\\s+)?([a-zA-Záéíóúüñ\\s]+?)(?=\\s+\\d+\\.\\d+|,|$)", RegexOption.IGNORE_CASE)
        val matchesConUnidades = patronDecimalesConUnidades.findAll(texto).toList()
        
        if (matchesConUnidades.size >= 2) {
            val productos = mutableListOf<Producto>()
            println("🎯 Detectadas ${matchesConUnidades.size} cantidades decimales con unidades")
            
            for ((index, match) in matchesConUnidades.withIndex()) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].lowercase()
                val nombre = match.groupValues[3].trim()
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = when(unidad) {
                            "kg", "kilogramos" -> "kg"
                            "g", "gramos", "gr" -> "g"
                            "l", "litros" -> "l"
                            "m", "metros" -> "m"
                            "ml", "mililitros" -> "ml"
                            "cm", "centímetros" -> "cm"
                            else -> unidad
                        }
                    )
                    productos.add(producto)
                    println("✅ Producto decimal ${index + 1}: ${producto.nombre} - ${producto.cantidad}${producto.unidad}")
                }
            }
            
            if (productos.size >= 2) {
                return productos
            }
        }
        
        // CASO ADICIONAL: Detectar cantidades decimales separadas por comas con múltiples puntos como separadores
        // "1.5 litros de jugo de naranja,,,2 latas de atún."
        val patronComaSeparador = Regex("(\\d+\\.\\d+)\\s*(\\w*)\\s+(?:de\\s+)?([^,;]+?)(?:[,;]{1,3}|$)", RegexOption.IGNORE_CASE)
        val matchesComa = patronComaSeparador.findAll(texto).toList()
        
        if (matchesComa.size >= 2) {
            val productos = mutableListOf<Producto>()
            println("🎯 Detectadas ${matchesComa.size} cantidades decimales separadas por comas")
            
            for ((index, match) in matchesComa.withIndex()) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].takeIf { it.isNotBlank() }
                val nombre = match.groupValues[3].trim().removeSuffix(".")
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = unidad?.let { ParserUtils.normalizarUnidad(it) }
                    )
                    productos.add(producto)
                    println("✅ Producto coma-separado ${index + 1}: ${producto.nombre} - ${producto.cantidad}${producto.unidad ?: ""}")
                }
            }
            
            if (productos.size >= 2) {
                return productos
            }
        }
        
        // Patrón original para compatibilidad
        val patronDecimales = Regex("(\\d+\\.\\d+)\\s*(kg|gramos?|g|litros?|l|metros?|m)\\s+([a-zA-Záéíóúüñ\\s]+?)(?=\\s*\\d+\\.\\d+|$)", RegexOption.IGNORE_CASE)
        val matches = patronDecimales.findAll(texto).toList()
        
        if (matches.size >= 2) {
            val productos = mutableListOf<Producto>()
            
            for (match in matches) {
                val cantidad = match.groupValues[1].toDoubleOrNull()
                val unidad = match.groupValues[2].lowercase()
                val nombre = match.groupValues[3].trim()
                
                if (cantidad != null && nombre.isNotEmpty()) {
                    val producto = Producto(
                        nombre = nombre,
                        cantidad = cantidad,
                        unidad = when(unidad) {
                            "kg", "kilogramos" -> "kg"
                            "g", "gramos", "gr" -> "g"
                            "l", "litros" -> "l"
                            "m", "metros" -> "m"
                            else -> unidad
                        }
                    )
                    productos.add(producto)
                    println("✅ Producto decimal original: ${producto.nombre} - ${producto.cantidad}${producto.unidad}")
                }
            }
            
            if (productos.size >= 2) {
                println("🎯 Múltiples productos decimales detectados: ${productos.size}")
                return productos
            }
        }
        
        return emptyList()
    }

    private fun procesarFragmento(fragmento: String): Producto? {
        if (esFragmentoBasura(fragmento)) return null
        
        var texto = fragmento.trim()
        var nota: String? = null
        var marcas = mutableListOf<String>()
        
        // MEJORA CRÍTICA: Extraer marcas con patrón mejorado para casos como "(marca: Frutos del Maipo o Minuto Verde)"
        val marcaRegexComplejo = Regex("\\(marca[s]?:\\s*([^)]+)\\)", RegexOption.IGNORE_CASE)
        val marcaMatchComplejo = marcaRegexComplejo.find(texto)
        if (marcaMatchComplejo != null) {
            val marcasTexto = marcaMatchComplejo.groupValues[1].trim()
            // Separar marcas por " o "
            val marcasList = marcasTexto.split(Regex("\\s+o\\s+")).map { it.trim() }
            marcas.addAll(marcasList)
            texto = texto.replace(marcaMatchComplejo.value, "").trim()
        } else {
            // MEJORA: Extraer marcas con patrón genérico "marca X"
            val marcaRegex = Regex("marca\\s+([\\w\\s\\-]+)", RegexOption.IGNORE_CASE)
            val marcaMatch = marcaRegex.find(texto)
            if (marcaMatch != null) {
                val marca = marcaMatch.groupValues[1].trim()
                marcas.add(marca)
                texto = texto.replace(marcaMatch.value, "").trim()
            }
        }
        
        // Extraer nota entre paréntesis (que no sea marca)
        val notaMatch = Regex("\\(([^)]+)\\)").find(texto)
        if (notaMatch != null && !notaMatch.value.contains("marca", ignoreCase = true)) {
            nota = notaMatch.groupValues[1].trim()
            texto = texto.replace(notaMatch.value, "").trim()
        }
        
        // Extraer cantidad y unidad
        val extraccion = extraerCantidadYUnidad(texto)
        
        return Producto(
            nombre = extraccion.nombre,
            cantidad = extraccion.cantidad,
            unidad = extraccion.unidad,
            marcas = marcas,
            nota = nota,
            original = fragmento
        )
    }

    private fun extraerCantidadYUnidad(texto: String): Extraccion {
        var nombre = texto
        var cantidad: Double? = null
        var unidad: String? = null
        
        // MEJORA CRÍTICA: Patrón para 'x2', 'x6', etc. (ej: "Leche sin lactosa (x2)")
        val patronX = Regex("^(.+?)\\s*\\(x(\\d+)(?:\\s+(\\w+))?\\)", RegexOption.IGNORE_CASE)
        val matchX = patronX.find(texto)
        if (matchX != null) {
            nombre = matchX.groupValues[1].trim()
            cantidad = matchX.groupValues[2].toDoubleOrNull()
            unidad = matchX.groupValues[3].takeIf { it.isNotBlank() }
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // MEJORA CRÍTICA: Patrón cantidad + unidad + "de" + nombre (ej: "2 metros de cable")
        val patronConDe = Regex("^(\\d+(?:\\.\\d+)?)\\s+(\\w+)\\s+de\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchConDe = patronConDe.find(texto)
        if (matchConDe != null && ParserUtils.esUnidadConocida(matchConDe.groupValues[2])) {
            cantidad = matchConDe.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchConDe.groupValues[2])
            nombre = matchConDe.groupValues[3].trim()
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // MEJORA: Patrón cantidad + unidad + nombre (sin "de") (ej: "2 bolsas arroz")
        val patronCompleto = Regex("^(\\d+(?:\\.\\d+)?)\\s+(\\w+)\\s+(.*)")
        val matchCompleto = patronCompleto.find(texto)
        if (matchCompleto != null && ParserUtils.esUnidadConocida(matchCompleto.groupValues[2])) {
            cantidad = matchCompleto.groupValues[1].toDoubleOrNull()
            unidad = ParserUtils.normalizarUnidad(matchCompleto.groupValues[2])
            nombre = matchCompleto.groupValues[3].trim()
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // MEJORA CRÍTICA: Patrón para números escritos en palabras (ej: "cinco tomates", "ocho zanahorias")
        val patronPalabras = Regex("^(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce|media|medio)\\s+(.+)", RegexOption.IGNORE_CASE)
        val matchPalabras = patronPalabras.find(texto)
        if (matchPalabras != null) {
            val cantidadPalabra = matchPalabras.groupValues[1].lowercase()
            nombre = matchPalabras.groupValues[2].trim()
            cantidad = convertirPalabraANumero(cantidadPalabra)
            
            // CORRECCIÓN ESPECÍFICA: "media docena" = 6, no 12
            if (cantidadPalabra == "media" && nombre.startsWith("docena")) {
                cantidad = 6.0
                nombre = nombre.replace("docena de ", "").replace("docena", "").trim()
            }
            
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // MEJORA: Patrón cantidad entre paréntesis (ej: "Tomates (2 kg)")
        val patronParentesis = Regex("^(.+?)\\s*\\((\\d+(?:\\.\\d+)?)\\s*(\\w+)?\\)", RegexOption.IGNORE_CASE)
        val matchParentesis = patronParentesis.find(texto)
        if (matchParentesis != null) {
            nombre = matchParentesis.groupValues[1].trim()
            cantidad = matchParentesis.groupValues[2].toDoubleOrNull()
            val unidadParentesis = matchParentesis.groupValues[3]
            if (unidadParentesis.isNotBlank() && ParserUtils.esUnidadConocida(unidadParentesis)) {
                unidad = ParserUtils.normalizarUnidad(unidadParentesis)
            }
            return Extraccion(cantidad, unidad, nombre)
        }
        
        // Patrón: cantidad+unidad pegadas (ej: "1bolsa", "500ml")
        val patronPegado = Regex("^(\\d+(?:\\.\\d+)?)([a-zA-Záéíóúüñ]+)\\s*(.*)")
        val matchPegado = patronPegado.find(texto)
        if (matchPegado != null) {
            val cant = matchPegado.groupValues[1].toDoubleOrNull()
            val unid = matchPegado.groupValues[2]
            val resto = matchPegado.groupValues[3]
            
            // Verificar si es unidad conocida
            if (ParserUtils.esUnidadConocida(unid)) {
                cantidad = cant
                unidad = ParserUtils.normalizarUnidad(unid)
                nombre = resto.ifBlank { unid }
            } else {
                nombre = "$unid $resto".trim()
                cantidad = cant
            }
        } else {
            // CORRECCIÓN CRÍTICA MEJORADA: Patrón cantidad al final (ej: "Leche sin lactosa 2", "Shampoo anticaspa 1.", "Esponjas 5 en oferta")
            val patronCantidadAlFinal = Regex("^(.+?)\\s+(\\d+(?:\\.\\d+)?)\\s*([a-zA-Záéíóúüñ]*)\\s*\\.?.*$")
            val matchAlFinal = patronCantidadAlFinal.find(texto)
            if (matchAlFinal != null) {
                val nombreBase = matchAlFinal.groupValues[1].trim()
                val cant = matchAlFinal.groupValues[2].toDoubleOrNull()
                val posibleUnidad = matchAlFinal.groupValues[3].trim()
                
                println("🔍 DEBUG patronCantidadAlFinal: '$texto'")
                println("   nombreBase: '$nombreBase'")
                println("   cant: $cant") 
                println("   posibleUnidad: '$posibleUnidad'")
                
                // Verificar que el nombre base no sea muy corto y la cantidad sea válida
                if (nombreBase.length >= 3 && cant != null && cant > 0) {
                    // Verificar si es unidad conocida
                    if (posibleUnidad.isNotEmpty() && ParserUtils.esUnidadConocida(posibleUnidad)) {
                        cantidad = cant
                        unidad = ParserUtils.normalizarUnidad(posibleUnidad)
                        nombre = nombreBase
                        println("   → Con unidad: cantidad=$cantidad, unidad='$unidad', nombre='$nombre'")
                    } else {
                        cantidad = cant
                        nombre = nombreBase
                        // Para casos como "Esponjas 5 en oferta", preservar toda la descripción adicional
                        if (posibleUnidad.isNotEmpty() && !ParserUtils.esUnidadConocida(posibleUnidad)) {
                            nombre = nombreBase  // Solo el nombre base, la descripción se maneja en otro lugar
                        }
                        println("   → Sin unidad: cantidad=$cantidad, nombre='$nombre'")
                    }
                    return Extraccion(cantidad, unidad, nombre)
                }
            }
            
            // Patrón: cantidad al inicio (sin unidad clara)
            val patronInicio = Regex("^(\\d+(?:\\.\\d+)?)\\s+(.+)")
            val matchInicio = patronInicio.find(texto)
            if (matchInicio != null) {
                cantidad = matchInicio.groupValues[1].toDoubleOrNull()
                nombre = matchInicio.groupValues[2]
            }
        }
        
        return Extraccion(cantidad, unidad, nombre.trim())
    }

    // Funciones esUnidadConocida y normalizarUnidad ahora están en ParserUtils

    private fun esLineaBasura(linea: String): Boolean {
        val lineaLimpia = linea.trim().lowercase()
        return lineaLimpia.contains("no son cantidades") ||
               lineaLimpia.matches(Regex("^tipo\\s+\\d+.*")) ||
               lineaLimpia.isBlank()
    }

    private fun esFragmentoBasura(fragmento: String): Boolean {
        val fragLimpio = fragmento.trim().lowercase()
        
        // Fragmentos muy cortos o vacíos
        if (fragLimpio.isBlank() || fragLimpio.length <= 2) return true
        
        // MEJORA: Filtrar fragmentos que empiecen con preposiciones
        if (fragLimpio.startsWith("de ") || 
            fragLimpio.startsWith("en ") || 
            fragLimpio.startsWith("y ") ||
            fragLimpio.startsWith("o ") ||
            fragLimpio.startsWith("con ")) return true
            
        // MEJORA: Filtrar fragmentos que sean solo preposiciones + sustantivo
        val patronPreposicion = Regex("^(de|en|y|o|con)\\s+\\w+$")
        if (patronPreposicion.matches(fragLimpio)) return true
        
        // MEJORA: Filtrar fragmentos que sean solo conjunciones
        val conjunciones = setOf("y", "o", "de", "en", "con", "para", "por")
        if (conjunciones.contains(fragLimpio)) return true
        
        return false
    }

    private fun esProductoValido(producto: Producto): Boolean {
        return producto.nombre.trim().isNotBlank() && producto.nombre.trim().length > 2
    }

    private fun normalizarNumeros(texto: String): String {
        val numerosTexto = mapOf(
            "medio" to "0.5", "media" to "0.5",
            "un" to "1", "una" to "1", "uno" to "1",
            "dos" to "2", "tres" to "3", "cuatro" to "4", "cinco" to "5",
            "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9", "diez" to "10",
            "once" to "11", "doce" to "12", "trece" to "13", "catorce" to "14", "quince" to "15",
            "dieciséis" to "16", "diecisiete" to "17", "dieciocho" to "18", "diecinueve" to "19", "veinte" to "20"
        )
        
        var resultado = texto
        
        // CORRECCIÓN ESPECÍFICA: "media docena" = 6
        resultado = resultado.replace(Regex("\\bmedia\\s+docena\\b", RegexOption.IGNORE_CASE), "6")
        
        // Aplicar otras normalizaciones
        for ((palabra, numero) in numerosTexto) {
            resultado = resultado.replace(Regex("\\b$palabra\\b", RegexOption.IGNORE_CASE), numero)
        }
        
        return resultado
    }

    // NUEVA FUNCIÓN: Convertir palabras numéricas a números
    private fun convertirPalabraANumero(palabra: String): Double {
        return when (palabra.lowercase()) {
            "uno" -> 1.0
            "dos" -> 2.0
            "tres" -> 3.0
            "cuatro" -> 4.0
            "cinco" -> 5.0
            "seis" -> 6.0
            "siete" -> 7.0
            "ocho" -> 8.0
            "nueve" -> 9.0
            "diez" -> 10.0
            "once" -> 11.0
            "doce" -> 12.0
            "media", "medio" -> 0.5
            else -> 1.0
        }
    }

    private data class Extraccion(
        val cantidad: Double?,
        val unidad: String?,
        val nombre: String
    )
}
 