package com.listafacilnueva

/**
 * Configuración global de la aplicación
 * Aquí puedes habilitar/deshabilitar funcionalidades para debugging
 */
object AppConfig {
    
    /**
     * Habilita o deshabilita la funcionalidad de cámara OCR
     * Si tienes problemas con la cámara, cambia esto a false
     */
    const val CAMERA_OCR_ENABLED = true
    
    /**
     * Habilita o deshabilita el modo debug del parser
     */
    const val PARSER_DEBUG_ENABLED = true
    
    /**
     * Habilita o deshabilita el logging detallado
     */
    const val DETAILED_LOGGING = true
    
    /**
     * Tiempo máximo de espera para operaciones de cámara (en milisegundos)
     */
    const val CAMERA_TIMEOUT_MS = 10000L
    
    /**
     * Número máximo de intentos para inicializar la cámara
     */
    const val MAX_CAMERA_RETRIES = 3
}
