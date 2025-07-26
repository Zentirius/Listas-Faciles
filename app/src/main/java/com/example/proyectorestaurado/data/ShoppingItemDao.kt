package com.example.proyectorestaurado.data

import androidx.room.*
import com.example.proyectorestaurado.data.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
        @Query("SELECT * FROM shopping_items ORDER BY priceRange DESC, timestamp DESC")
    fun getAllItems(): Flow<List<ShoppingItem>>

        @Query("SELECT * FROM shopping_items WHERE category = :category ORDER BY priceRange DESC, timestamp DESC")
    fun getItemsByCategory(category: String): Flow<List<ShoppingItem>>

        @Query("SELECT * FROM shopping_items WHERE name LIKE '%' || :query || '%' ORDER BY priceRange DESC, timestamp DESC")
    fun searchItems(query: String): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isChecked = 1")
    fun getCheckedItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ShoppingItem>)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("DELETE FROM shopping_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAll()
}
