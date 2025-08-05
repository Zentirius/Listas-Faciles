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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar producto") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth()
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
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nombre.isBlank()) {
                    error = "El nombre no puede estar vacío"
                    return@Button
                }
                val cantidadDouble = cantidad.toDoubleOrNull()
                val marcasList = marcas.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                onSave(
                    Producto(
                        nombre = nombre,
                        cantidad = cantidadDouble,
                        unidad = unidad.ifBlank { null },
                        marcas = marcasList,
                        nota = nota.ifBlank { null },
                        original = initialProduct.original
                    )
                )
                onDismiss()
            }) {
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