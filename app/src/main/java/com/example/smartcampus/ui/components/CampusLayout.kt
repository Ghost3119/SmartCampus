package com.example.smartcampus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import com.example.smartcampus.ui.CampusState
import com.example.smartcampus.domain.*
import com.example.smartcampus.ui.components.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import com.example.smartcampus.ui.theme.CampusDesign
import com.example.smartcampus.R

@Composable fun isTelevision(): Boolean =
    androidx.compose.ui.platform.LocalConfiguration.current.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION

fun Modifier.campusFocus(): Modifier = composed {
    var focused by remember { mutableStateOf(false) }
    onFocusChanged { focused = it.hasFocus }.then(
        if (focused) Modifier.border(3.dp, MaterialTheme.colorScheme.tertiary, CampusDesign.corner) else Modifier)
}

@Composable fun CampusLogo() {
    Image(painterResource(R.drawable.utc_institucional), "Universidad Tecnológica de Coahuila",
        Modifier.size(65.dp, 43.dp).background(CampusDesign.logoPlate, androidx.compose.foundation.shape.RoundedCornerShape(6.dp)).padding(5.dp))
}

@Composable fun <T> CardGrid(items: List<T>, maxColumns: Int = 2, content: @Composable (T) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = minOf(maxColumns, ((maxWidth + CampusDesign.gap) / 280.dp).toInt().coerceAtLeast(1))
        Column(verticalArrangement = Arrangement.spacedBy(CampusDesign.gap)) {
            items.chunked(columns).forEach { line ->
                Row(horizontalArrangement = Arrangement.spacedBy(CampusDesign.gap)) {
                    line.forEach { Box(Modifier.weight(1f)) { content(it) } }
                    repeat(columns - line.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
