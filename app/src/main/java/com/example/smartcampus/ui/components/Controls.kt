package com.example.smartcampus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.example.smartcampus.ui.theme.CampusDesign

internal val LocalSectionTick = compositionLocalOf { 0 }

@OptIn(ExperimentalFoundationApi::class)
private class TvBringIntoViewSpec(
    private val topMarginPx: Float,
    private val bottomMarginPx: Float
) : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
        val maxMargin = (containerSize - size).coerceAtLeast(0f) / 2f
        val leading = minOf(topMarginPx, maxMargin)
        val trailing = minOf(bottomMarginPx, maxMargin)
        val trailingEdge = offset + size
        return when {
            offset < leading -> offset - leading
            trailingEdge > containerSize - trailing -> trailingEdge - (containerSize - trailing)
            else -> 0f
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable fun Page(
    verticalPadding: Dp? = null,
    spacing: Dp? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val tv = isTelevision()
    val scroll = rememberScrollState()
    val sectionTick = LocalSectionTick.current
    var lastTick by rememberSaveable { mutableIntStateOf(sectionTick) }
    LaunchedEffect(sectionTick) {
        if (sectionTick != lastTick) {
            lastTick = sectionTick
            scroll.scrollTo(0)
        }
    }
    val density = LocalDensity.current
    val defaultBringIntoViewSpec = LocalBringIntoViewSpec.current
    val bringIntoViewSpec = remember(tv, density, defaultBringIntoViewSpec) {
        if (tv) with(density) { TvBringIntoViewSpec(110.dp.toPx(), 20.dp.toPx()) }
        else defaultBringIntoViewSpec
    }
    val vPad = verticalPadding ?: if (tv) 14.dp else 22.dp
    val gap = spacing ?: if (tv) 12.dp else 16.dp
    CompositionLocalProvider(LocalBringIntoViewSpec provides bringIntoViewSpec) {
        BoxWithConstraints {
            val hPad = if (tv) 28.dp else if (maxWidth >= 700.dp) 44.dp else 16.dp
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(horizontal = hPad, vertical = vPad),
                verticalArrangement = Arrangement.spacedBy(gap),
                content = content
            )
        }
    }
}

@Composable fun Panel(
    modifier: Modifier = Modifier,
    action: (() -> Unit)? = null,
    selected: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val tv = isTelevision()
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val innerPadding = if (tv) 16.dp else 20.dp
    if (action != null) {
        Surface(
            onClick = action,
            modifier = modifier.campusFocus(),
            shape = CampusDesign.corner,
            color = bg
        ) {
            Column(
                Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    } else {
        Surface(modifier = modifier, shape = CampusDesign.corner, color = bg) {
            Column(
                Modifier.fillMaxSize().padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = content
            )
        }
    }
}

@Composable fun Head(text: String) { Text(text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
@Composable fun Title(text: String, compact: Boolean = false) {
    Text(
        text,
        style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}
@Composable fun Sub(text: String) { Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
@Composable fun Tag(text: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(
            text,
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}
@Composable fun Choice(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val tv = isTelevision()
    Box {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.campusFocus(CircleShape),
            contentPadding = PaddingValues(horizontal = if (tv) 14.dp else 20.dp, vertical = 10.dp)
        ) { Text("$label · $value ▾", maxLines = 1) }
        DropdownMenu(open, { open = false }) {
            options.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { open = false; onSelect(it) }) }
        }
    }
}
@Composable fun Detail(title: String, close: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val first = remember { FocusRequester() }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(Modifier.fillMaxWidth(.92f).widthIn(max = 1050.dp).padding(16.dp), shape = CampusDesign.corner) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Head(title)
                content()
                OutlinedButton(close, Modifier.focusRequester(first).campusFocus(CircleShape)) { Text("Cerrar") }
            }
        }
        LaunchedEffect(Unit) { first.requestFocus() }
    }
}
@Composable fun Confirm(title: String, body: String, accept: String, close: () -> Unit, action: () -> Unit) {
    val cancel = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = close,
        title = { Title(title) },
        text = { Text(body) },
        confirmButton = { Button(action, Modifier.campusFocus(CircleShape)) { Text(accept) } },
        dismissButton = {
            TextButton(close, Modifier.focusRequester(cancel).campusFocus(CircleShape)) { Text("Cancelar") }
            LaunchedEffect(Unit) { cancel.requestFocus() }
        }
    )
}

