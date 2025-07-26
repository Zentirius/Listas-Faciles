package com.example.proyectorestaurado.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var name: String,
    var brand: String? = null, // Nueva propiedad para marca
    var notes: String? = null, // Campo para notas adicionales
    var isChecked: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
    var category: String = "General",
    var priceRange: PriceRange = PriceRange.NORMAL,
    var quantity: Double? = null,
    var unit: String? = null
) : Parcelable {


    // Method to create a copy of the item with updated values
    fun copyWith(
        name: String = this.name,
        brand: String? = this.brand,
        notes: String? = this.notes,
        isChecked: Boolean = this.isChecked,
        category: String = this.category,
        priceRange: PriceRange = this.priceRange,
        quantity: Double? = this.quantity,
        unit: String? = this.unit
    ): ShoppingItem {
        return ShoppingItem(
            id = this.id,
            name = name,
            brand = brand,
            notes = notes,
            isChecked = isChecked,
            timestamp = this.timestamp, // Keep original timestamp unless explicitly changed
            category = category,
            priceRange = priceRange,
            quantity = quantity,
            unit = unit
        )
    }
}

@Parcelize
enum class PriceRange : Parcelable {
    ECONOMY, NORMAL, PREMIUM
}
