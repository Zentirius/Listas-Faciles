package com.listafacilnueva.model

data class Producto(
    val nombre: String,
    val cantidad: Double? = null,
    val unidad: String? = null,
    val marcas: List<String> = emptyList(),
    val nota: String? = null,
    val original: String? = null,
    val isChecked: Boolean = false
)
