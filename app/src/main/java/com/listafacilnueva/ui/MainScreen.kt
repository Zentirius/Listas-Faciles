package com.listafacilnueva.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search     
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

private val compose: Any

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
                        Icons.Default.Add,
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
                    
                    // Barra de búsqueda
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar productos...") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    
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
                            
                            // Botón de reordenar elegante y visible
                            IconButton(
                                onClick = { invertido = !invertido },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    if (invertido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Reordenar lista",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        // Botones de acción elegantes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Botón Borrar Todo - Diseño sólido
                            Button(
                                onClick = { showDeleteDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(58.dp)
                                    .weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete, 
                                        contentDescription = "Borrar todo",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Borrar Todo",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            // Botón Borrar Marcados - Diseño outlined
                            OutlinedButton(
                                onClick = { 
                                    productos = productos.filter { !it.isChecked }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(58.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    "Borrar Marcados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
                
                // Cantidad y unidad - Mostrar "1u" por defecto cuando cantidad es 1
                if (producto.cantidad != null) {
                    val cantidadText = if (producto.cantidad % 1.0 == 0.0) {
                        producto.cantidad.toInt().toString()
                    } else {
                        producto.cantidad.toString()
                    }
                    val unidadText = producto.unidad ?: "u"
                    infoItems.add("$cantidadText$unidadText")
                } else {
                    // Si no hay cantidad específica, mostrar "1u" por defecto
                    infoItems.add("1u")
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
