package com.example.smartcampus.ui.components

import androidx.compose.ui.focus.*
import androidx.compose.ui.window.*
import com.example.smartcampus.ui.theme.CampusDesign
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.smartcampus.ui.components.*
import com.example.smartcampus.data.local.*
import com.example.smartcampus.ui.*
import com.example.smartcampus.domain.*

@Composable fun Page(content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = if (maxWidth >= 700.dp) 44.dp else 16.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}
@Composable fun Panel(modifier: Modifier = Modifier, action: (() -> Unit)? = null, selected: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier, shape = CampusDesign.corner, color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface) {
        Column(Modifier.then(if (action != null) Modifier.campusFocus().clickable(onClick = action) else Modifier).padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}
@Composable fun Head(text: String) { Text(text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
@Composable fun Title(text: String) { Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
@Composable fun Sub(text: String) { Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
@Composable fun Tag(
    text: String,
    container: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(shape = RoundedCornerShape(6.dp), color = container) { Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = content) }
}
@Composable fun Choice(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton({ open = true }, Modifier.campusFocus()) { Text("$label · $value  ▾") }
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
                OutlinedButton(close, Modifier.focusRequester(first).campusFocus()) { Text("Cerrar") }
            }
        }
        LaunchedEffect(Unit) { first.requestFocus() }
    }
}
@Composable fun Confirm(title: String, body: String, accept: String, close: () -> Unit, action: () -> Unit) {
    val cancel = remember { FocusRequester() }
    AlertDialog(onDismissRequest = close, title = { Title(title) }, text = { Text(body) }, confirmButton = { Button(action) { Text(accept) } }, dismissButton = { TextButton(close, Modifier.focusRequester(cancel).campusFocus()) { Text("Cancelar") }; LaunchedEffect(Unit) { cancel.requestFocus() } })
}
