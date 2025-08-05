package com.listafacilnueva.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility     
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.listafacilnueva.model.Producto
import com.listafacilnueva.parser.QuantityParser
import com.listafacilnueva.ocr.CameraOCRScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var productoAEditar by remember { mutableStateOf<Producto?>(null) }
    var invertido by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOCRScreen by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            // Solo mostrar el botón flotante cuando NO esté abierta la pantalla OCR
            if (!showOCRScreen) {
                FloatingActionButton(
                    onClick = {
                        println("📷 BOTÓN CÁMARA PRESIONADO")
                        println("🔄 CAMBIANDO showOCRScreen a true")
                        showOCRScreen = true
                        println("🔍 showOCRScreen = $showOCRScreen")
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Escanear con cámara",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Sección de entrada
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Campo de entrada y botón agregar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("Agregar item o lista") },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(max = 120.dp),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4,
                            singleLine = false
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                println("🔴 BOTÓN AGREGAR PRESIONADO")
                                println("📝 TEXTO INPUT: '$inputText'")
                                if (inputText.isNotBlank()) {
                                    println("✅ TEXTO NO ESTÁ VACÍO, PARSEANDO...")
                                    val nuevosProductos = QuantityParser.parse(inputText)
                                    println("🧠 PRODUCTOS PARSEADOS: ${nuevosProductos.size}")
                                    nuevosProductos.forEach { producto ->
                                        println("   - ${producto.nombre} (${producto.cantidad} ${producto.unidad})")
                                    }
                                    productos = productos + nuevosProductos
                                    println("📋 TOTAL PRODUCTOS EN LISTA: ${productos.size}")
                                    inputText = ""
                                } else {
                                    println("❌ TEXTO ESTÁ VACÍO")
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Controles superiores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Contador y botón invertir
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Items: ${productos.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            TextButton(onClick = { invertido = !invertido }) {
                                Text(if (invertido) "↑" else "↓")
                            }
                        }
                        
                        // Botones de acción
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Primera fila: Borrar Todo
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                TextButton(
                                    onClick = { showDeleteDialog = true },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text(
                                        "Borrar Todo",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Segunda fila: Borrar Marcados (ancho completo)
                            Button(
                                onClick = { 
                                    productos = productos.filter { !it.isChecked }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete, 
                                        contentDescription = "Eliminar marcados",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Borrar Marcados",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de productos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (productos.isEmpty()) {
                    // Vista vacía
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🛒",
                                fontSize = 48.sp
                            )
                            Text(
                                text = "La lista está vacía",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "¡Agrega tu primer item!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Lista de productos filtrada
                    val filteredProducts = if (searchText.isBlank()) {
                        productos
                    } else {
                        productos.filter { 
                            it.nombre.contains(searchText, ignoreCase = true) ||
                            it.nota?.contains(searchText, ignoreCase = true) == true ||
                            it.marcas.any { marca -> marca.contains(searchText, ignoreCase = true) }
                        }
                    }
                    
                    val lista = if (invertido) filteredProducts.reversed() else filteredProducts
                    
                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(lista) { producto ->
                            ProductItem(
                                producto = producto,
                                onCheckedChange = { isChecked ->
                                    productos = productos.map { 
                                        if (it == producto) it.copy(isChecked = isChecked) else it 
                                    }
                                },
                                onEdit = { productoAEditar = producto }
                            )
                        }
                    }
                }
            }
        }
        
    } // ← AGREGAR ESTA LÍNEA para cerrar el lambda del Scaffold
        
    // Diálogo de edición
    productoAEditar?.let { producto ->
        EditProductDialog(
            initialProduct = producto,
            onDismiss = { productoAEditar = null },
            onSave = { nuevoProducto ->
                productos = productos.map {
                    if (it == producto) nuevoProducto else it
                }
                productoAEditar = null
            }
        )
    }
    
        // Diálogo de confirmación para borrar todo
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Borrar toda la lista") },
                text = { Text("¿Estás seguro de que quieres eliminar todos los productos?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            productos = emptyList()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Sí, borrar todo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        
        // Pantalla de OCR
        if (showOCRScreen) {
            println("📱 MOSTRANDO PANTALLA OCR")
            CameraOCRScreen(
                onTextRecognized = { recognizedText ->
                    // DEBUG: Imprimir texto reconocido
                    println("🔤 OCR DETECTÓ: '$recognizedText'")
                    
                    // Procesar el texto reconocido con el parser
                    val nuevosProductos = QuantityParser.parse(recognizedText)
                    println("🧠 PARSER GENERÓ: ${nuevosProductos.size} productos")
                    nuevosProductos.forEach { producto ->
                        println("   - ${producto.nombre} (${producto.cantidad} ${producto.unidad})")
                    }
                    
                    productos = productos + nuevosProductos
                    showOCRScreen = false
                },
                onBack = {
                    println("🔙 CERRANDO PANTALLA OCR")
                    showOCRScreen = false
                }
            )
        }
    }
}

@Composable
fun ProductItem(
    producto: Producto,
    onCheckedChange: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (producto.isChecked) 1.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (producto.isChecked) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = producto.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre del producto
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (producto.isChecked) 
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                // Información adicional (cantidad, unidad, marcas, notas)
                val infoItems = mutableListOf<String>()
                
                // Cantidad y unidad
                if (producto.cantidad != null) {
                    val cantidadText = if (producto.cantidad % 1.0 == 0.0) {
                        producto.cantidad.toInt().toString()
                    } else {
                        producto.cantidad.toString()
                    }
                    val unidadText = producto.unidad ?: "un"
                    infoItems.add("$cantidadText $unidadText")
                }
                
                // Marcas
                if (producto.marcas.isNotEmpty()) {
                    infoItems.add("Marcas: ${producto.marcas.joinToString(", ")}")
                }
                
                // Notas
                if (!producto.nota.isNullOrBlank()) {
                    infoItems.add("Nota: ${producto.nota}")
                }
                
                if (infoItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = infoItems.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (producto.isChecked) 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Botón de editar
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar producto",
                    modifier = Modifier.size(24.dp),
                    tint = if (producto.isChecked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ❌ ELIMINAR TODO DESDE AQUÍ HASTA EL FINAL DEL ARCHIVO
// @Composable
// fun EditProductDialog(
//     initialProduct: Producto,
//     onDismiss: () -> Unit,
//     onSave: (Producto) -> Unit
// ) {
//     var nombre by remember { mutableStateOf(initialProduct.nombre) }
//     var cantidad by remember { mutableStateOf(initialProduct.cantidad?.toString() ?: "") }
//     var unidad by remember { mutableStateOf(initialProduct.unidad ?: "") }
//     var nota by remember { mutableStateOf(initialProduct.nota ?: "") }
//     var marcas by remember { mutableStateOf(initialProduct.marcas.joinToString(", ")) }
//
//     AlertDialog(
//         onDismissRequest = onDismiss,
//         title = { Text("Editar producto") },
//         text = {
//             Column {
//                 OutlinedTextField(
//                     value = nombre,
//                     onValueChange = { nombre = it },
//                     label = { Text("Nombre") },
//                     modifier = Modifier.fillMaxWidth()
//                 )
//                
//                 Spacer(modifier = Modifier.height(8.dp))
//                
//                 OutlinedTextField(
//                     value = cantidad,
//                     onValueChange = { cantidad = it },
//                     label = { Text("Cantidad") },
//                     modifier = Modifier.fillMaxWidth()
//                 )
//                
//                 Spacer(modifier = Modifier.height(8.dp))
//                
//                 OutlinedTextField(
//                     value = unidad,
//                     onValueChange = { unidad = it },
//                     label = { Text("Unidad") },
//                     modifier = Modifier.fillMaxWidth()
//                 )
//                
//                 Spacer(modifier = Modifier.height(8.dp))
//                
//                 OutlinedTextField(
//                     value = marcas,
//                     onValueChange = { marcas = it },
//                     label = { Text("Marcas (separadas por coma)") },
//                     modifier = Modifier.fillMaxWidth()
//                 )
//                
//                 Spacer(modifier = Modifier.height(8.dp))
//                
//                 OutlinedTextField(
//                     value = nota,
//                     onValueChange = { nota = it },
//                     label = { Text("Nota") },
//                     modifier = Modifier.fillMaxWidth()
//                 )
//             }
//         },
//         confirmButton = {
//             TextButton(
//                 onClick = {
//                     val cantidadDouble = cantidad.toDoubleOrNull()
//                     val marcasList = if (marcas.isBlank()) emptyList() else marcas.split(",").map { it.trim() }
//                     val productoEditado = initialProduct.copy(
//                         nombre = nombre,
//                         cantidad = cantidadDouble,
//                         unidad = unidad.ifBlank { null },
//                         nota = nota.ifBlank { null },
//                         marcas = marcasList
//                     )
//                     onSave(productoEditado)
//                 }
//             ) {
//                 Text("Guardar")
//             }
//         },
//         dismissButton = {
//             TextButton(onClick = onDismiss) {
//                 Text("Cancelar")
//             }
//         }
//     )
// }