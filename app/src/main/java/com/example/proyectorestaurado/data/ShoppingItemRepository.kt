package com.example.proyectorestaurado.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.proyectorestaurado.data.ShoppingItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShoppingItemRepository(private val shoppingItemDao: ShoppingItemDao) {
    private val TAG = "ShoppingRepository"
    
    init {
        Log.d(TAG, "Inicializando ShoppingItemRepository")
    }
    
    val allItems: LiveData<List<ShoppingItem>> = try {
        Log.d(TAG, "Obteniendo todos los items")
        val result = shoppingItemDao.getAllItems().asLiveData()
        Log.d(TAG, "LiveData de todos los items obtenido correctamente")
        result
    } catch (e: Exception) {
        Log.e(TAG, "Error al obtener todos los items: ${e.message}")
        Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
        throw e
    }
    
    fun getItemsByCategory(category: String): LiveData<List<ShoppingItem>> = try {
        Log.d(TAG, "Obteniendo items por categoría: $category")
        val result = shoppingItemDao.getItemsByCategory(category).asLiveData()
        Log.d(TAG, "Items por categoría obtenidos correctamente")
        result
    } catch (e: Exception) {
        Log.e(TAG, "Error al obtener items por categoría: ${e.message}")
        Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
        throw e
    }
    
    fun searchItems(query: String): LiveData<List<ShoppingItem>> = try {
        Log.d(TAG, "Buscando items con query: $query")
        val result = shoppingItemDao.searchItems(query).asLiveData()
        Log.d(TAG, "Búsqueda de items completada correctamente")
        result
    } catch (e: Exception) {
        Log.e(TAG, "Error al buscar items: ${e.message}")
        Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
        throw e
    }
    
    fun getCheckedItems(): LiveData<List<ShoppingItem>> = try {
        Log.d(TAG, "Obteniendo items marcados")
        val result = shoppingItemDao.getCheckedItems().asLiveData()
        Log.d(TAG, "Items marcados obtenidos correctamente")
        result
    } catch (e: Exception) {
        Log.e(TAG, "Error al obtener items marcados: ${e.message}")
        Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
        throw e
    }
    
    suspend fun insertItem(item: ShoppingItem) {
        try {
            Log.d(TAG, "Insertando item: ${item.name}, categoría: ${item.category}")
            shoppingItemDao.insertItem(item)
            Log.d(TAG, "Item insertado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar item: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun insertItems(items: List<ShoppingItem>) {
        try {
            Log.d(TAG, "Insertando ${items.size} items")
            shoppingItemDao.insertItems(items)
            Log.d(TAG, "${items.size} items insertados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar items: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    suspend fun updateItem(item: ShoppingItem) {
        try {
            Log.d(TAG, "Actualizando item: ${item.id} - ${item.name}, isChecked: ${item.isChecked}")
            shoppingItemDao.updateItem(item)
            Log.d(TAG, "Item actualizado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar item: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    suspend fun deleteItem(id: Long) {
        try {
            Log.d(TAG, "Eliminando item con id: $id")
            shoppingItemDao.deleteItem(id)
            Log.d(TAG, "Item eliminado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar item: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
    
    suspend fun deleteCheckedItems() {
        try {
            Log.d(TAG, "Eliminando items marcados")
            shoppingItemDao.deleteCheckedItems()
            Log.d(TAG, "Items marcados eliminados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar items marcados: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun deleteAll() {
        try {
            Log.d(TAG, "Eliminando todos los items")
            shoppingItemDao.deleteAll()
            Log.d(TAG, "Todos los items eliminados correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar todos los items: ${e.message}")
            Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
            throw e
        }
    }
}
