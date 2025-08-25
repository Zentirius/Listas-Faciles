package com.listafacilnueva.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ✅ ESQUEMA DE COLORES CLARO
private val LightColorScheme = lightColorScheme(
    primary = ShoppingBlue,
    onPrimary = Color.White,
    primaryContainer = ShoppingBlue.copy(alpha = 0.1f),
    onPrimaryContainer = ShoppingBlue,
    secondary = ShoppingGreen,
    onSecondary = Color.White,
    secondaryContainer = ShoppingGreen.copy(alpha = 0.1f),
    onSecondaryContainer = ShoppingGreen,
    tertiary = ShoppingOrange,
    onTertiary = Color.White,
    tertiaryContainer = ShoppingOrange.copy(alpha = 0.1f),
    onTertiaryContainer = ShoppingOrange,
    error = ShoppingRed,
    onError = Color.White,
    errorContainer = ShoppingRed.copy(alpha = 0.1f),
    onErrorContainer = ShoppingRed,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666),
    outline = ShoppingGray,
    outlineVariant = ShoppingGray.copy(alpha = 0.5f)
)

// ✅ ESQUEMA DE COLORES OSCURO
private val DarkColorScheme = darkColorScheme(
    primary = ShoppingBlue.copy(alpha = 0.8f),
    onPrimary = Color.White,
    primaryContainer = ShoppingBlue.copy(alpha = 0.2f),
    onPrimaryContainer = ShoppingBlue.copy(alpha = 0.9f),
    secondary = ShoppingGreen.copy(alpha = 0.8f),
    onSecondary = Color.White,
    secondaryContainer = ShoppingGreen.copy(alpha = 0.2f),
    onSecondaryContainer = ShoppingGreen.copy(alpha = 0.9f),
    tertiary = ShoppingOrange.copy(alpha = 0.8f),
    onTertiary = Color.White,
    tertiaryContainer = ShoppingOrange.copy(alpha = 0.2f),
    onTertiaryContainer = ShoppingOrange.copy(alpha = 0.9f),
    error = ShoppingRed.copy(alpha = 0.8f),
    onError = Color.White,
    errorContainer = ShoppingRed.copy(alpha = 0.2f),
    onErrorContainer = ShoppingRed.copy(alpha = 0.9f),
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = ShoppingGray.copy(alpha = 0.7f),
    outlineVariant = ShoppingGray.copy(alpha = 0.3f)
)

@Composable
fun ListaFacilNuevaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
