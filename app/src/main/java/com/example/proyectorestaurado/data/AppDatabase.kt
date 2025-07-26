package com.example.proyectorestaurado.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectorestaurado.data.PriceRangeConverter
import com.example.proyectorestaurado.data.ShoppingItem

@Database(entities = [ShoppingItem::class], version = 3, exportSchema = false)
@TypeConverters(PriceRangeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingItemDao(): ShoppingItemDao

    companion object {
        private const val TAG = "AppDatabase"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            Log.d(TAG, "Solicitando instancia de base de datos")
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "INSTANCE es null, creando nueva instancia de base de datos")
                try {
                    Log.d(TAG, "Iniciando construcción de base de datos")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "shopping_list_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    Log.d(TAG, "Base de datos construida exitosamente")
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e(TAG, "Error al crear la base de datos: ${e.message}")
                    Log.e(TAG, "StackTrace: ${e.stackTraceToString()}")
                    throw e
                }
            }
        }
    }
}
