package com.example.smartcampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.example.smartcampus.R

object CampusDesign {
    val corner = RoundedCornerShape(16.dp)
    val gap = 16.dp
    val logoPlate = Color(0xFF103D2E)
}
private val light = lightColorScheme(
    primary = Color(0xFF006448), onPrimary = Color.White,
    primaryContainer = Color(0xFFCCEBDD), onPrimaryContainer = Color(0xFF103D2E),
    secondary = Color(0xFF385366), onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCEBDD), onSecondaryContainer = Color(0xFF103D2E),
    tertiary = Color(0xFF795300), onTertiary = Color.White,
    background = Color(0xFFF2F6F3), onBackground = Color(0xFF142822),
    surface = Color.White, onSurface = Color(0xFF142822),
    surfaceVariant = Color(0xFFDFEBE5), onSurfaceVariant = Color(0xFF455D54),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF2F6F3),
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color(0xFFDFEBE5),
    outline = Color(0xFF6B837A),
    outlineVariant = Color(0xFFC5D8CF),
    error = Color(0xFF9A302C), onError = Color.White
)
private val dark = darkColorScheme(
    primary = Color(0xFF93E5BF), onPrimary = Color(0xFF103D2E),
    primaryContainer = Color(0xFF245342), onPrimaryContainer = Color(0xFFF1F7F3),
    secondary = Color(0xFFB7CADA), onSecondary = Color(0xFF0C191B),
    secondaryContainer = Color(0xFF103D2E), onSecondaryContainer = Color(0xFFCCEBDD),
    tertiary = Color(0xFFFFE186), onTertiary = Color(0xFF2B2000),
    background = Color(0xFF0C191B), onBackground = Color(0xFFF1F7F3),
    surface = Color(0xFF15282A), onSurface = Color(0xFFF1F7F3),
    surfaceVariant = Color(0xFF203638), onSurfaceVariant = Color(0xFFB3C7C2),
    surfaceContainerLowest = Color(0xFF0C191B),
    surfaceContainerLow = Color(0xFF102022),
    surfaceContainer = Color(0xFF15282A),
    surfaceContainerHigh = Color(0xFF203638),
    surfaceContainerHighest = Color(0xFF2A4447),
    outline = Color(0xFF6B8580),
    outlineVariant = Color(0xFF2F4A4C),
    error = Color(0xFFFFB7AE), onError = Color(0xFF4E1412)
)
private val inter = FontFamily(Font(R.font.inter_regular, FontWeight.Normal), Font(R.font.inter_bold, FontWeight.Bold))
private fun type(size: Int, bold: Boolean = false) = TextStyle(fontFamily = inter, fontSize = size.sp,
    lineHeight = (size * 1.24f).sp, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
private val campusType = Typography(displayMedium = type(52,true), headlineMedium = type(38,true),
    headlineLarge = type(44,true), titleLarge = type(26,true), titleMedium = type(22,true),
    bodyLarge = type(18), bodyMedium = type(16), labelLarge = type(16,true), labelMedium = type(13,true))
@Composable fun CampusTheme(mode: String, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (mode == "Oscuro" || mode == "Sistema" && isSystemInDarkTheme()) dark else light,
        typography = campusType, content = content)
}
