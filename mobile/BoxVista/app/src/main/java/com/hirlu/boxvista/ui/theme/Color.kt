package com.hirlu.boxvista.ui.theme

import androidx.compose.ui.graphics.Color

// Updated colors based on the provided UI design (Dark Theme)

// Backgrounds
val BoxVistaDarkBackground = Color(0xFF101418) // Very dark blue/black background
val BoxVistaDarkSurface = Color(0xFF1E242E) // Card background
val BoxVistaDarkSurfaceHighlight = Color(0xFF2B3240) // Lighter surface for interaction

// Primary & Accents
val BoxVistaBlue = Color(0xFF2979FF) // Bright Blue (Primary Actions, FAB)
val BoxVistaRed = Color(0xFFFF5252) // Red (Action Required, Mismatch)
val BoxVistaGreen = Color(0xFF00E676) // Green (Verified, Warehouse A)
val BoxVistaYellow = Color(0xFFFFAB40) // Orange/Yellow (Pending, Processing)

// Text
val BoxVistaWhite = Color(0xFFFFFFFF)
val BoxVistaGray = Color(0xFF9E9E9E) // Secondary text

// Legacy mapping (keeping these to avoid breaking other parts if any, but re-mapped)
val BoxVistaDarkPrimary = BoxVistaBlue
val BoxVistaDarkSecondary = BoxVistaGreen

val BoxVistaLightPrimary = BoxVistaBlue
val BoxVistaLightSecondary = BoxVistaDarkSurface
val BoxVistaBackground = BoxVistaDarkBackground // Force Dark for now as requested
val BoxVistaSurface = BoxVistaDarkSurface
val BoxVistaAccent = BoxVistaBlue

val Purple80 = BoxVistaBlue
val PurpleGrey80 = BoxVistaDarkSurface
val Pink80 = BoxVistaRed

val Purple40 = BoxVistaBlue
val PurpleGrey40 = BoxVistaDarkSurface
val Pink40 = BoxVistaRed
