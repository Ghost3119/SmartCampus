package com.example.smartcampus.ui.screens

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
import java.time.LocalDate

@Composable fun Board(s: CampusState.Ready, model: CampusViewModel, events: Boolean, target: Long?, consumed: () -> Unit) {
    val admin by model.admin.collectAsState()
    var category by rememberSaveable { mutableStateOf("Todas") }
    var selected by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(target) { if (target != null) { selected = target; consumed() } }
    var editor by rememberSaveable { mutableStateOf(false) }
    var confirm by rememberSaveable { mutableStateOf(false) }
    val notice = s.campus.notices.find { !events && it.id == selected }
    val event = s.campus.events.find { events && it.id == selected }
    val close = { selected = null; editor = false; confirm = false }
    if (editor && admin) {
        androidx.activity.compose.BackHandler(onBack = close)
        BoardEditor(events, notice, event, s.now.toLocalDate(), close, model)
        return
    }
    val noun = if (events) "evento" else "aviso"
    Page {
        Head(if (events) "Eventos del campus" else "Avisos universitarios")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Sub(if (admin) "Administración de demostración" else if (events) "Agenda académica y cultural" else "Comunicados del campus")
        }
        if (admin) Button({ selected = null; editor = true }) { Text("Crear $noun") }
        if (events) {
            val entries = if (admin) s.campus.events else upcomingEvents(s.campus.events, s.now)
            CardGrid(entries) {
                Panel(Modifier.fillMaxWidth(), { selected = it.id }) {
                    Tag(LocalDate.parse(it.date).label("d MMM yyyy") + "  ·  " + it.time)
                    Title(it.title); Sub(it.place)
                }
            }
            if (entries.isEmpty()) Sub("Sin eventos programados.")
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (listOf("Todas") + categories).forEach { option ->
                    FilterChip(category == option, { category = option }, { Text(option) }, Modifier.campusFocus())
                }
            }
            val entries = notices(s.campus.notices, category)
            CardGrid(entries) {
                Panel(Modifier.fillMaxWidth(), { selected = it.id }) {
                    Tag(if (it.priority == "Alta") "★  Destacado  ·  " + it.category else it.category)
                    Title(it.title); Sub(LocalDate.parse(it.date).label("d MMM yyyy") + "  ·  Prioridad " + it.priority.lowercase())
                    Text(it.description, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
            if (entries.isEmpty()) Sub("Sin avisos en esta categoría.")
        }
    }
    when {
        confirm && admin -> Confirm("Eliminar $noun", "Se eliminará «" + (event?.title ?: notice?.title.orEmpty()) + "» de este dispositivo.", "Eliminar", { confirm = false }) {
            notice?.let { model.delete(it, close) }; event?.let { model.delete(it, close) }
        }
        event != null || notice != null -> Detail(event?.title ?: notice!!.title, close) {
            if (event != null) { Tag(event.date + "  ·  " + event.time); Title(event.place); Text(event.description) }
            if (notice != null) { Tag(notice.category + "  ·  Prioridad " + notice.priority); Sub(notice.date); Text(notice.description) }
            if (admin) Row { TextButton({ editor = true }) { Text("Editar") }; TextButton({ confirm = true }) { Text("Eliminar") } }
        }
    }
}

