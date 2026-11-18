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
    primary = Color(0xFF006448), onPrimary = Color.White, secondary = Color(0xFF385366),
    tertiary = Color(0xFF795300), primaryContainer = Color(0xFFCCEBDD), onPrimaryContainer = Color(0xFF142822),
    background = Color(0xFFF2F6F3), surface = Color.White, surfaceContainerHigh = Color.White,
    onSurface = Color(0xFF142822), onBackground = Color(0xFF142822),
    surfaceVariant = Color(0xFFDFEBE5), onSurfaceVariant = Color(0xFF455D54), error = Color(0xFF9A302C))
private val dark = darkColorScheme(
    primary = Color(0xFF93E5BF), secondary = Color(0xFFB7CADA), tertiary = Color(0xFFFFE186),
    primaryContainer = Color(0xFF245342), onPrimaryContainer = Color(0xFFF1F7F3),
    background = Color(0xFF0C191B), surface = Color(0xFF15282A), surfaceContainerHigh = Color(0xFF203638),
    onSurface = Color(0xFFF1F7F3), onBackground = Color(0xFFF1F7F3),
    surfaceVariant = Color(0xFF203638), onSurfaceVariant = Color(0xFFB3C7C2), error = Color(0xFFFFB7AE))
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
