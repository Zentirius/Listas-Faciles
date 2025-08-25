package com.listafacilnueva

/**
 * ⚡ CONFIGURACIÓN DE RENDIMIENTO
 * 
 * Configuraciones para optimizar el rendimiento y evitar congelamiento
 */
object PerformanceConfig {
    
    /**
     * 🔧 LÍMITES DE SEGURIDAD PARA PARSER
     */
    object Parser {
        const val MAX_LINEAS_PROCESAR = 2000        // ✅ AJUSTADO: De 10000 a 2000 (más razonable)
        const val MAX_FRAGMENTOS_POR_LINEA = 500    // ✅ AJUSTADO: De 1000 a 500 (más razonable)
        const val MAX_PRODUCTOS_ENUMERADOS = 500    // ✅ AJUSTADO: De 1000 a 500 (más razonable)
        const val MAX_MATCHES_REGEX = 200           // ✅ AJUSTADO: De 500 a 200 (más razonable)
        const val MAX_LONGITUD_TEXTO = 100000       // ✅ AJUSTADO: De 1000000 a 100000 (más razonable)
    }
    
    /**
     * 📱 LÍMITES DE SEGURIDAD PARA UI
     */
    object UI {
        const val MAX_PRODUCTOS_EN_LISTA = 5000     // ✅ AJUSTADO: De 10000 a 5000 (más razonable)
        const val MAX_BUSQUEDA_RESULTADOS = 500     // ✅ AJUSTADO: De 1000 a 500 (más razonable)
        const val DEBOUNCE_BUSQUEDA_MS = 300L
        const val MAX_ITEMS_LAZY_COLUMN = 2000      // ✅ AJUSTADO: De 5000 a 2000 (más razonable)
    }
    
    /**
     * 📷 LÍMITES DE SEGURIDAD PARA CÁMARA
     */
    object Camera {
        const val MAX_FRAMES_POR_SEGUNDO = 10
        const val TIMEOUT_PROCESAMIENTO_MS = 5000L
        const val MAX_INTENTOS_OCR = 3
        const val INTERVALO_PROCESAMIENTO_MS = 1000L
    }
    
    /**
     * 🚫 CONFIGURACIONES ANTI-CONGELAMIENTO
     */
    object AntiFreeze {
        const val DEBUG_MODE_DESHABILITADO = true
        const val LOGGING_MINIMO = true
        const val PROCESAMIENTO_ASINCRONO = true
        const val LIMITES_ESTRICTOS = false         // ✅ MANTENIDO: false para permitir más productos
    }
}
