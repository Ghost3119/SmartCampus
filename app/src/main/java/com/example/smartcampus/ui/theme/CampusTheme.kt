package com.example.smartcampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val light = lightColorScheme(primary = Color(0xFF1855C8), onPrimary = Color.White, background = Color(0xFFF3F6FA), surface = Color.White, onSurface = Color(0xFF132C46), onBackground = Color(0xFF132C46), surfaceVariant = Color(0xFFE9EFF7), onSurfaceVariant = Color(0xFF495E75), outlineVariant = Color(0xFFD8E2EC))
private val dark = darkColorScheme(primary = Color(0xFFA9C7FF), background = Color(0xFF101B29), surface = Color(0xFF192A3E), onSurface = Color(0xFFE7F0FF), onBackground = Color(0xFFE7F0FF), surfaceVariant = Color(0xFF283D55), onSurfaceVariant = Color(0xFFBFCEE0))
@Composable fun CampusTheme(mode: String, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (mode == "Oscuro" || mode == "Sistema" && isSystemInDarkTheme()) dark else light, content = content)
}
