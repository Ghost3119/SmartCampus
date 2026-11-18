package com.example.smartcampus.ui.components

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
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(if (maxWidth >= 700.dp) 32.dp else 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}
@Composable fun Panel(modifier: Modifier = Modifier, action: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier, shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Column(Modifier.then(if (action != null) Modifier.clickable(onClick = action) else Modifier).padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = content)
    }
}
@Composable fun Head(text: String) { Text(text, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
@Composable fun Title(text: String) { Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
@Composable fun Sub(text: String) { Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
@Composable fun Tag(text: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) { Text(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}
@Composable fun Choice(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        OutlinedButton({ open = true }) { Text("$label · $value  ▾") }
        DropdownMenu(open, { open = false }) {
            options.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { open = false; onSelect(it) }) }
        }
    }
}
@Composable fun Detail(title: String, close: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    AlertDialog(onDismissRequest = close, title = { Title(title) }, text = { Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp), content = content) }, confirmButton = { TextButton(close) { Text("Cerrar") } })
}
@Composable fun Confirm(title: String, body: String, accept: String, close: () -> Unit, action: () -> Unit) {
    AlertDialog(onDismissRequest = close, title = { Title(title) }, text = { Text(body) }, confirmButton = { Button(action) { Text(accept) } }, dismissButton = { TextButton(close) { Text("Cancelar") } })
}

