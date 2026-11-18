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
import androidx.compose.ui.res.painterResource
import com.example.smartcampus.R

@Composable fun Dashboard(s: CampusState.Ready, go: (String) -> Unit) {
    val a = s.campus.academic
    val group = s.settings.group
    val next = upcoming(a.sessions.filter { it.groupCode == group }, s.now).firstOrNull()
    val free = a.rooms.count { available(it.name, a.sessions, s.now) }
    val featured = s.campus.notices.filter { it.priority == "Alta" }
    val events = upcomingEvents(s.campus.events, s.now)
    Page {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF183E70), contentColor = Color.White) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val showArt = maxWidth > 550.dp
                if (showArt) CampusDrawing(Modifier.align(Alignment.CenterEnd).width(310.dp).height(152.dp))
                Column(Modifier.padding(24.dp).fillMaxWidth(if (showArt) .62f else 1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("PANEL DEL CAMPUS", style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp, color = Color(0xFFA9D9FC))
                    val greeting = when(s.now.hour) { in 6..11 -> "Buenos días"; in 12..18 -> "Buenas tardes"; else -> "Buenas noches" }
                    Text(greeting, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(s.now.toLocalDate().label("EEEE, d 'de' MMMM") + "   ·   Grupo " + group, color = Color(0xFFD4E6FE))
                }
            }
        }
        Title("Resumen del campus")
        BoxWithConstraints {
            val columns = if (maxWidth >= 640.dp) 3 else if (maxWidth >= 430.dp) 2 else 1
            val tiles = listOf(Triple("Aulas disponibles", "$free / ${a.rooms.size}", "Aulas"), Triple("Avisos importantes", featured.size.toString(), "Avisos"), Triple("Eventos próximos", events.size.toString(), "Eventos"))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                tiles.chunked(columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { (label, count, route) ->
                            Panel(Modifier.weight(1f), { go(route) }) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(count, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Icon(painterResource(when(route) { "Aulas" -> R.drawable.nav_aulas; "Avisos" -> R.drawable.nav_avisos; else -> R.drawable.nav_eventos }), null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Sub(label)
                            }
                        }
                        repeat(columns-row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
        BoxWithConstraints {
            val classCard: @Composable (Modifier) -> Unit = { mod ->
                Panel(mod, { go("Horarios") }) {
                    Tag("Próxima clase  ·  $group")
                    if (next == null) Sub("Sin sesiones programadas.") else {
                        Spacer(Modifier.height(5.dp)); Title(next.session.subject)
                        Text(next.date.label("EEEE d MMM") + "  ·  " + clock(next.session.start) + " – " + clock(next.session.end))
                        Sub(next.session.room + "  ·  " + a.rooms.first { it.name == next.session.room }.building)
                    }
                    TextButton({ go("Horarios") }) { Text("Consultar horarios  →") }
                }
            }
            val boardCard: @Composable (Modifier) -> Unit = { mod ->
                Panel(mod) {
                    Title("Cartelera universitaria")
                    featured.firstOrNull()?.let { TextButton({ go("Avisos") }) { Text(it.title) } }
                    events.firstOrNull()?.let { HorizontalDivider(); TextButton({ go("Eventos") }) { Text(LocalDateLabel(it.date) + "   ·   " + it.title) } }
                    if (featured.isEmpty() && events.isEmpty()) Sub("Sin publicaciones destacadas.")
                }
            }
            if (maxWidth >= 680.dp) Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) { classCard(Modifier.weight(1f)); boardCard(Modifier.weight(1f)) }
            else Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { classCard(Modifier.fillMaxWidth()); boardCard(Modifier.fillMaxWidth()) }
        }
    }
}
internal fun LocalDateLabel(date: String) = java.time.LocalDate.parse(date).label("d MMM")

