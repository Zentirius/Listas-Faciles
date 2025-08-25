package com.listafacilnueva.parser

import android.util.Log
import com.listafacilnueva.model.Producto
import java.util.Locale

object QuantityParser {

    private var DEBUG_MODE = false
    private const val TAG = "PARSER"
    
    fun setDebugMode(enabled: Boolean) {
        DEBUG_MODE = enabled
        TextPreprocessor.setDebugMode(enabled)
        TextFragmenter.setDebugMode(enabled)
        DecimalAnalyzer.setDebugMode(enabled)
        EnumerationAnalyzer.setDebugMode(enabled)
        QuantityExtractor.setDebugMode(enabled)
    }
    
    private fun log(message: String) {
        if (DEBUG_MODE) {
            Log.d(TAG, message)
        }
    }

    private fun capitalizarNombre(nombre: String): String {
        if (nombre.isBlank()) return nombre
        return nombre.replaceFirstChar { ch ->
            if (ch.isLowerCase()) ch.titlecase(Locale.getDefault()) else ch.toString()
        }
    }

    fun parse(texto: String): List<Producto> {
        if (texto.isBlank()) return emptyList()
        
        log("Iniciando parse de texto: ${texto.take(100)}...")
        
        val productos = mutableListOf<Producto>()
        
        // Paso 1: Preprocesar texto con mejoras avanzadas
        val textoPreprocesado = TextPreprocessor.preprocess(texto)
        log("Texto preprocesado: ${textoPreprocesado.take(100)}...")
        
        // Paso 2: Normalizar números
        val textoNormalizado = ParserUtils.normalizarNumeros(textoPreprocesado)
        log("Texto normalizado: ${textoNormalizado.take(100)}...")
        
        // Paso 3: Dividir en líneas
        val lineas = textoNormalizado.split(Regex("[\n;]+")).filter { linea -> linea.isNotBlank() }
        log("Líneas encontradas: ${lineas.size}")
        
        for ((indice, linea) in lineas.withIndex()) {
            if (ValidationUtils.esLineaBasura(linea)) continue
            
            log("Procesando línea: '$linea'")
            
            // Paso 4: Extraer cantidad implícita
            val lineaConCantidadImplicita = extraerCantidadImplicita(linea, indice, lineas)
            
            // Paso 5a: Heurística para enumeración con punto pegado al número decimal aparente
            // Ej: "1.2metros cable 2.bombilla grande" debe interpretarse como "1. 2 metros de cable" y "2. bombilla grande"
            val pareceEnumConDecimalPegado =
                Regex("^\\s*\\d+\\.\\d+[a-zA-Záéíóúüñ]").containsMatchIn(lineaConCantidadImplicita) &&
                Regex("\\b\\d+\\.[a-zA-Záéíóúüñ]").containsMatchIn(lineaConCantidadImplicita)
            if (pareceEnumConDecimalPegado) {
                log("Heurística enum-decimal activada, procesando con EnumerationAnalyzer")
                val productosNumeracion = EnumerationAnalyzer.procesarNumeracion(lineaConCantidadImplicita)
                if (productosNumeracion.isNotEmpty()) {
                    val ajustados = productosNumeracion.map { it.copy(nombre = capitalizarNombre(it.nombre)) }
                    productos.addAll(ajustados)
                    continue
                }
            }
            
            // Paso 5: Analizar cantidades decimales (CRÍTICO para "1.2metros de madera 3.8 metros de cable")
            val productosDecimales = DecimalAnalyzer.analizarCantidadesDecimales(lineaConCantidadImplicita)
            if (productosDecimales.isNotEmpty()) {
                val filtrados = ValidationUtils.filtrarProductosBasura(productosDecimales)
                val ajustados = filtrados.map { it.copy(nombre = capitalizarNombre(it.nombre)) }
                productos.addAll(ajustados)
                log("Productos decimales encontrados: ${productosDecimales.size}")
                // NO usar continue aquí para permitir procesamiento adicional
            }
            
            // Paso 6: Verificar si es numeración de lista (CRÍTICO para "1.1papa mediana2.sandia grande")
            if (EnumerationAnalyzer.tieneSecuenciaEnumeracion(lineaConCantidadImplicita)) {
                log("Detectada numeración de lista, procesando con EnumerationAnalyzer")
                val productosNumeracion = EnumerationAnalyzer.procesarNumeracion(lineaConCantidadImplicita)
                val filtrados = ValidationUtils.filtrarProductosBasura(productosNumeracion)
                val ajustados = filtrados.map { it.copy(nombre = capitalizarNombre(it.nombre)) }
                productos.addAll(ajustados)
                continue
            }
            
            // Paso 7: Limpiar numeración
            val lineaLimpia = EnumerationAnalyzer.limpiarNumeracionCompuesta(lineaConCantidadImplicita)
            val lineaLimpiaFinal = EnumerationAnalyzer.limpiarNumeracionLista(lineaLimpia)
            
            // Paso 8: Dividir en fragmentos con separación múltiple avanzada
            val fragmentos = TextFragmenter.dividirEnFragmentos(lineaLimpiaFinal)
            log("Fragmentos generados: ${fragmentos.size}")
            
            // Paso 9: Procesar cada fragmento
            for (fragmento in fragmentos) {
                val producto = QuantityExtractor.procesarFragmento(fragmento)
                if (producto != null && ValidationUtils.esProductoValido(producto) && !ValidationUtils.esNombreNoProducto(producto.nombre)) {
                    val pAjustado = producto.copy(nombre = capitalizarNombre(producto.nombre))
                    productos.add(pAjustado)
                    log("Producto agregado: ${producto.nombre} - ${producto.cantidad}${producto.unidad ?: ""}")
                }
            }
        }
        
        // Paso 10: Eliminar duplicados y filtrar productos válidos
        val productosUnicos = ValidationUtils
            .filtrarProductosBasura(productos)
            .distinctBy { "${it.nombre.lowercase()}_${it.cantidad}_${it.unidad ?: ""}" }
        
        log("Total productos únicos válidos: ${productosUnicos.size}")
        // APPCHK: resumen final para comparar con los tests (solo en DEBUG)
        if (DEBUG_MODE) {
            try {
                val resumen = productosUnicos.joinToString(" | ") { p ->
                    val cu = buildString {
                        if (p.cantidad != null) append(p.cantidad)
                        if (p.unidad != null) append(p.unidad)
                    }
                    if (cu.isNotEmpty()) "${p.nombre} ${cu}" else p.nombre
                }
                Log.d("APPCHK", "Productos(${productosUnicos.size}): $resumen")
            } catch (_: Throwable) {
                // evitar crashear si algo raro ocurre en joinToString
            }
        }
        
        return productosUnicos
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
}
