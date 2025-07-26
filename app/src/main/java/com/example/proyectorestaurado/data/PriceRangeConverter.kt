package com.example.proyectorestaurado.data

import androidx.room.TypeConverter

class PriceRangeConverter {
    @TypeConverter
    fun fromPriceRange(priceRange: PriceRange?): String {
        return priceRange?.name ?: PriceRange.NORMAL.name
    }

    @TypeConverter
    fun toPriceRange(priceRange: String?): PriceRange {
        return try {
            PriceRange.valueOf(priceRange ?: PriceRange.NORMAL.name)
        } catch (e: IllegalArgumentException) {
            PriceRange.NORMAL
        }
    }
}
