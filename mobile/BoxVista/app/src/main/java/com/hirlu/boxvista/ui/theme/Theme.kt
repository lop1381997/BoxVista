package com.hirlu.boxvista.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Colores específicos para el tema oscuro, usando la paleta personalizada de BoxVista
private val DarkColorScheme = darkColorScheme(
    primary = BoxVistaDarkPrimary,
    secondary = BoxVistaDarkSurface,
    tertiary = BoxVistaAccent,
    background = BoxVistaDarkBackground,
    surface = BoxVistaDarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = BoxVistaDarkPrimary
)

// Colores específicos para el tema claro, usando la paleta personalizada de BoxVista
private val LightColorScheme = lightColorScheme(
    primary = BoxVistaLightPrimary,
    secondary = BoxVistaLightSecondary,
    tertiary = BoxVistaAccent,
    background = BoxVistaBackground,
    surface = BoxVistaSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = BoxVistaBackground
)

@Composable
fun BoxVistaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se deshabilita el color dinámico para forzar el tema personalizado.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Bloque de color dinámico modificado para respetar `dynamicColor = false`
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}