package com.listafacilnueva.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.listafacilnueva.model.Producto

@Composable
fun EditProductDialog(
    initialProduct: Producto,
    onDismiss: () -> Unit,
    onSave: (Producto) -> Unit
) {
    var nombre by remember { mutableStateOf(initialProduct.nombre) }
    var cantidad by remember { mutableStateOf(initialProduct.cantidad?.toString() ?: "") }
    var unidad by remember { mutableStateOf(initialProduct.unidad ?: "") }
    var marcas by remember { mutableStateOf(initialProduct.marcas.joinToString(", ")) }
    var nota by remember { mutableStateOf(initialProduct.nota ?: "") }
    var error by remember { mutableStateOf("") }

    // ✅ OPTIMIZADO: Validación en tiempo real
    val isFormValid = remember(nombre, cantidad) {
        nombre.isNotBlank() && (cantidad.isBlank() || cantidad.toDoubleOrNull() != null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar producto") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { 
                        nombre = it
                        if (error.isNotEmpty()) error = ""
                    },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nombre.isBlank() && error.isNotEmpty()
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { 
                        cantidad = it
                        if (error.isNotEmpty()) error = ""
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = cantidad.isNotBlank() && cantidad.toDoubleOrNull() == null
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it },
                    label = { Text("Unidad") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = marcas,
                    onValueChange = { marcas = it },
                    label = { Text("Marcas (separadas por coma)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    label = { Text("Nota") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Text(
                        error, 
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "El nombre no puede estar vacío"
                        return@Button
                    }
                    if (cantidad.isNotBlank() && cantidad.toDoubleOrNull() == null) {
                        error = "La cantidad debe ser un número válido"
                        return@Button
                    }
                    
                    val cantidadDouble = cantidad.toDoubleOrNull()
                    val marcasList = marcas.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    
                    onSave(
                        Producto(
                            nombre = nombre.trim(),
                            cantidad = cantidadDouble,
                            unidad = unidad.trim().ifBlank { null },
                            marcas = marcasList,
                            nota = nota.trim().ifBlank { null },
                            original = initialProduct.original
                        )
                    )
                    onDismiss()
                },
                enabled = isFormValid
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}