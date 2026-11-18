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
import java.time.DayOfWeek
import java.time.format.TextStyle

private val days = DayOfWeek.entries.map { it.getDisplayName(TextStyle.FULL, spanish).replaceFirstChar(Char::uppercase) }
@Composable fun Schedule(s: CampusState.Ready) {
    val a = s.campus.academic
    var group by rememberSaveable(s.settings.group) { mutableStateOf(s.settings.group) }
    var day by rememberSaveable { mutableIntStateOf(s.now.dayOfWeek.value) }
    Page {
        Head("Horarios académicos")
        Sub("Clases y docentes ficticios · Semana recurrente")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Choice("Grupo", group, a.groups.map { it.code }) { group = it }
            Choice("Día", days[day-1], days) { day = days.indexOf(it)+1 }
        }
        val sessions = timetable(a.sessions, group, day)
        if (sessions.isEmpty()) Panel(Modifier.fillMaxWidth()) { Title("Día sin clases"); Sub("Este grupo no tiene sesiones el día seleccionado.") }
        CardGrid(sessions) {
            Panel(Modifier.fillMaxWidth().then(if (isTelevision()) Modifier.campusFocus().focusable() else Modifier), selected = status(it, s.now) == "En curso") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(clock(it.start) + " – " + clock(it.end), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Tag(status(it, s.now))
                }
                Title(it.subject)
                Sub(a.subjects.first { course -> course.name == it.subject }.teacher + "   ·   " + it.room + "   ·   " + a.rooms.first { room -> room.name == it.room }.building)
            }
        }
    }
}

@Composable fun Rooms(s: CampusState.Ready) {
    val a = s.campus.academic
    var query by rememberSaveable { mutableStateOf("") }
    var building by rememberSaveable { mutableStateOf("Todos") }
    var state by rememberSaveable { mutableStateOf("Todas") }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    fun roomState(room: Classroom) = if (available(room.name, a.sessions, s.now)) "Disponible" else "Programada"
    Page {
        Head("Consulta de aulas")
        Sub("Disponibilidad estimada a partir del horario registrado.")
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Buscar por nombre o número") })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Choice("Edificio", building, listOf("Todos") + a.rooms.map { it.building }.distinct()) { building = it }
            Choice("Estado", state, listOf("Todas", "Disponible", "Programada")) { state = it }
        }
        val matches = a.rooms.filter { it.name.contains(query.trim(), true) && (building == "Todos" || it.building == building) && (state == "Todas" || roomState(it) == state) }
        if (matches.isEmpty()) Sub("Ningún aula coincide con la búsqueda y los filtros.")
        CardGrid(matches, 3) {
            Panel(Modifier.fillMaxWidth(), { selected = it.name }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Title(it.name); Tag(roomState(it)) }
                Sub("${it.building}   ·   ${it.floor}   ·   ${it.capacity} plazas")
            }
        }
    }
    a.rooms.find { it.name == selected }?.let { room ->
        Detail("Aula " + room.name, { selected = null }) {
            Tag(roomState(room)); Text(room.description); Sub("${room.building} · ${room.floor} · ${room.capacity} plazas")
            Title("Próximas clases")
            val next = upcoming(a.sessions.filter { it.room == room.name }, s.now).take(4)
            if (next.isEmpty()) Sub("Sin clases programadas.")
            next.forEach { HorizontalDivider(); Text(it.session.subject); Sub(it.date.label("EEE d MMM") + " · " + clock(it.session.start) + " · " + it.session.groupCode) }
        }
    }
}
