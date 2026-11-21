package com.example.smartcampus.ui.screens

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
import androidx.compose.ui.res.painterResource
import com.example.smartcampus.R

private data class Summary(val label: String, val title: String, val detail: String, val destination: String)

@Composable fun Dashboard(s: CampusState.Ready, go: (String) -> Unit, publication: (String, Long) -> Unit) {
    val academic = s.campus.academic
    val group = s.settings.group
    val sessions = academic.sessions.filter { it.groupCode == group }
    val current = sessions.find { classStatus(it, s.now) == ClassStatus.IN_PROGRESS }
    val next = upcoming(sessions, s.now).firstOrNull()
    val session = current ?: next?.session
    val notice = s.campus.notices.firstOrNull { it.priority == "Alta" }
    val event = upcomingEvents(s.campus.events, s.now).firstOrNull()
    val free = academic.rooms.count { available(it.name, academic.sessions, s.now) }
    val cards = listOf(
        Summary(if (current != null) "En curso" else "Próxima clase", session?.subject ?: "Sin clases",
            session?.let { (if (current == null) next?.date?.label("EEE d MMM") + "   ·   " else "") + clock(it.start) + " — " + clock(it.end) + "   ·   " + it.room }.orEmpty(), "Horarios"),
        Summary("Disponibilidad estimada", "$free de ${academic.rooms.size} aulas libres", "Consultar espacios", "Aulas"),
        Summary("Aviso destacado", notice?.title ?: "Sin avisos destacados", notice?.let { it.category + "   ·   Prioridad alta" } ?: "Consultar cartelera", "Avisos"),
        Summary("Próximo evento", event?.title ?: "Sin eventos próximos", event?.let { LocalDateLabel(it.date) + "   ·   " + it.place } ?: "Consultar agenda", "Eventos"))
    Page {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Head(when(s.now.hour) { in 6..11 -> "Buenos días"; in 12..18 -> "Buenas tardes"; else -> "Buenas noches" })
                Sub(s.now.toLocalDate().label("EEEE d 'de' MMMM") + "   /   Grupo " + group)
            }
            BoxWithConstraints(Modifier.fillMaxWidth(.28f)) {
                Image(painterResource(R.drawable.arquitectura), null, Modifier.widthIn(max = 246.dp).fillMaxWidth().aspectRatio(370f/145))
            }
        }
        if (!isTelevision() && androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp < 760) Sub("Resumen del campus")
        CardGrid(cards) { card ->
            Panel(Modifier.fillMaxWidth(), {
                when {
                    card.destination == "Avisos" && notice != null -> publication("Avisos", notice.id)
                    card.destination == "Eventos" && event != null -> publication("Eventos", event.id)
                    else -> go(card.destination)
                }
            }, selected = card.destination == "Horarios") {
                Box(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(end = 12.dp).heightIn(min = 128.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(card.label.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Title(card.title); Sub(card.detail)
                    if (current != null && card.destination == "Horarios" && next != null) Sub("Después   /   " + clock(next.session.start) + "   " + next.session.subject)
                }
                Text("↗", Modifier.align(Alignment.BottomEnd), color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
internal fun LocalDateLabel(date: String) = java.time.LocalDate.parse(date).label("d MMM")
