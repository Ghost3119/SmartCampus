package com.example.smartcampus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.smartcampus.R
import com.example.smartcampus.ui.theme.CampusDesign

@Composable fun isTelevision(): Boolean =
    androidx.compose.ui.platform.LocalConfiguration.current.uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION

fun Modifier.campusFocus(shape: Shape = CampusDesign.corner): Modifier = composed {
    val tv = isTelevision()
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused && tv) 1.02f else 1f,
        animationSpec = tween(durationMillis = 140),
        label = "campusFocusScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
        .onFocusChanged { focused = it.hasFocus }
        .then(if (focused) Modifier.border(3.dp, MaterialTheme.colorScheme.tertiary, shape) else Modifier)
}

@Composable fun CampusLogo() {
    Image(painterResource(R.drawable.utc_institucional), "Universidad Tecnológica de Coahuila",
        Modifier.size(65.dp, 43.dp).background(CampusDesign.logoPlate, androidx.compose.foundation.shape.RoundedCornerShape(6.dp)).padding(5.dp))
}

@Composable fun <T> CardGrid(items: List<T>, maxColumns: Int = 2, content: @Composable (T) -> Unit) {
    val tv = isTelevision()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val step = if (tv && maxColumns == 3) 195.dp else 280.dp
        val columns = minOf(maxColumns, ((maxWidth + CampusDesign.gap) / step).toInt().coerceAtLeast(1))
        Column(verticalArrangement = Arrangement.spacedBy(CampusDesign.gap)) {
            items.chunked(columns).forEach { line ->
                Row(
                    Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(CampusDesign.gap)
                ) {
                    line.forEach { Box(Modifier.weight(1f).fillMaxHeight()) { content(it) } }
                    repeat(columns - line.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
