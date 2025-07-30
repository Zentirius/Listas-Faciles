package com.example.proyectorestaurado.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var name: String,
    var quantity: Double = 1.0,
    var unit: String = "u",
    var isChecked: Boolean = false,
    var timestamp: Long = System.currentTimeMillis(),
    var brand: String? = null,
    var notes: String? = null,
    var category: String = "General",
    var priceRange: PriceRange = PriceRange.NORMAL
) : Parcelable {

    // Method to create a copy of the item with updated values
    fun copyWith(
        name: String = this.name,
        quantity: Double = this.quantity,
        unit: String = this.unit,
        isChecked: Boolean = this.isChecked,
        brand: String? = this.brand,
        notes: String? = this.notes,
        category: String = this.category,
        priceRange: PriceRange = this.priceRange
    ): ShoppingItem {
        return ShoppingItem(
            id = this.id,
            timestamp = this.timestamp, // Keep original timestamp
            name = name,
            quantity = quantity,
            unit = unit,
            isChecked = isChecked,
            brand = brand,
            notes = notes,
            category = category,
            priceRange = priceRange
        )
    }
}

@Parcelize
enum class PriceRange : Parcelable {
    ECONOMY, NORMAL, PREMIUM
}
