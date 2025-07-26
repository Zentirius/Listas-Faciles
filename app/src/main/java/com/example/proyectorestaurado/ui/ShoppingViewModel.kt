package com.example.proyectorestaurado.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.proyectorestaurado.data.AppDatabase
import com.example.proyectorestaurado.data.ShoppingItemRepository
import com.example.proyectorestaurado.data.ShoppingItem
import kotlinx.coroutines.launch

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ShoppingItemRepository
    val allItems: LiveData<List<ShoppingItem>>
    
    init {
        try {
            Log.d("ShoppingViewModel", "Inicializando ViewModel")
            Log.d("ShoppingViewModel", "Obteniendo instancia de base de datos")
            val database = AppDatabase.getDatabase(application)
            Log.d("ShoppingViewModel", "Base de datos obtenida correctamente")
            
            Log.d("ShoppingViewModel", "Obteniendo DAO")
            val shoppingItemDao = database.shoppingItemDao()
            Log.d("ShoppingViewModel", "DAO obtenido correctamente")
            
            Log.d("ShoppingViewModel", "Inicializando repositorio")
            repository = ShoppingItemRepository(shoppingItemDao)
            Log.d("ShoppingViewModel", "Repositorio inicializado correctamente")
            
            Log.d("ShoppingViewModel", "Obteniendo todos los items")
            allItems = repository.allItems
            Log.d("ShoppingViewModel", "LiveData de todos los items obtenido correctamente")
            
            Log.d("ShoppingViewModel", "ViewModel inicializado correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al inicializar ViewModel: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
            throw e // Re-lanzar la excepción para que la aplicación pueda manejarla
        }
    }
    
    fun getItemsByCategory(category: String): LiveData<List<ShoppingItem>> {
        try {
            Log.d("ShoppingViewModel", "Obteniendo items por categoría: $category")
            val result = repository.getItemsByCategory(category)
            Log.d("ShoppingViewModel", "Items por categoría obtenidos correctamente")
            return result
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al obtener items por categoría: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    fun searchItems(query: String): LiveData<List<ShoppingItem>> {
        try {
            Log.d("ShoppingViewModel", "Buscando items con query: $query")
            val result = repository.searchItems(query)
            Log.d("ShoppingViewModel", "Búsqueda de items completada correctamente")
            return result
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al buscar items: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    fun getCheckedItems(): LiveData<List<ShoppingItem>> {
        try {
            Log.d("ShoppingViewModel", "Obteniendo items marcados")
            val result = repository.getCheckedItems()
            Log.d("ShoppingViewModel", "Items marcados obtenidos correctamente")
            return result
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al obtener items marcados: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    fun addItem(item: ShoppingItem) = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Insertando item: ${item.name}, categoría: ${item.category}")
            repository.insertItem(item)
            Log.d("ShoppingViewModel", "Item insertado correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al insertar item: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }

    fun addItems(items: List<ShoppingItem>) = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Insertando ${items.size} items")
            items.forEachIndexed { index, item ->
                Log.d("ShoppingViewModel", "Item ${index + 1}: ${item.name} (${item.quantity} ${item.unit ?: "u"})")
            }
            repository.insertItems(items)
            Log.d("ShoppingViewModel", "${items.size} items insertados correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al insertar items: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }
    
    fun updateItem(item: ShoppingItem) = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Actualizando item: ${item.id} - ${item.name}, isChecked: ${item.isChecked}")
            repository.updateItem(item)
            Log.d("ShoppingViewModel", "Item actualizado correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al actualizar item: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }
    
    fun deleteItem(id: Long) = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Eliminando item con id: $id")
            repository.deleteItem(id)
            Log.d("ShoppingViewModel", "Item eliminado correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al eliminar item: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }
    
    fun deleteCheckedItems() = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Eliminando items marcados")
            repository.deleteCheckedItems()
            Log.d("ShoppingViewModel", "Items marcados eliminados correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al eliminar items marcados: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }

    fun deleteAllItems() = viewModelScope.launch {
        try {
            Log.d("ShoppingViewModel", "Eliminando todos los items")
            repository.deleteAll()
            Log.d("ShoppingViewModel", "Todos los items eliminados correctamente")
        } catch (e: Exception) {
            Log.e("ShoppingViewModel", "Error al eliminar todos los items: ${e.message}")
            Log.e("ShoppingViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }
}
