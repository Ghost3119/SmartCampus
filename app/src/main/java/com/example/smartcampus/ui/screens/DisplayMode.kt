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
import androidx.compose.animation.Crossfade
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

private val scenes = listOf("Bienvenida", "Horarios del día", "Próximos eventos", "Avisos importantes", "Aulas disponibles")

@Composable fun DisplayMode(s: CampusState.Ready, exit: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var paused by rememberSaveable { mutableStateOf(false) }
    val owner = LocalLifecycleOwner.current
    val view = LocalView.current
    DisposableEffect(view) {
        val previous = view.keepScreenOn
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = previous }
    }
    LaunchedEffect(paused, s.settings.rotationSeconds, owner) {
        if (!paused) owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) { delay(s.settings.rotationSeconds * 1000L); index = (index + 1) % scenes.size }
        }
    }
    val pauseControl = remember { FocusRequester() }
    Page {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CampusLogo()
            Column(Modifier.weight(1f)) { Title("SmartCampus / Display Mode"); Sub("Demostración no oficial") }
            Sub(s.now.toLocalDate().label("d MMM yyyy") + "   " + clock(s.now.hour * 60 + s.now.minute))
        }
        Crossfade(index, label = "Escena informativa") { scene ->
            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Head(scenes[scene])
                val a = s.campus.academic
                val lines = when (scene) {
                    0 -> listOf("Universidad Tecnológica de Coahuila", s.now.toLocalDate().label("EEEE d 'de' MMMM"))
                    1 -> timetable(a.sessions, s.settings.group, s.now.dayOfWeek.value).map { clock(it.start) + "   " + it.subject + "   ·   " + it.room }
                    2 -> upcomingEvents(s.campus.events, s.now).take(3).map { LocalDateLabel(it.date) + "   " + it.title + "   ·   " + it.place }
                    3 -> s.campus.notices.filter { it.priority == "Alta" }.take(3).flatMap { listOf(it.title, it.description) }
                    else -> {
                        val free = a.rooms.filter { available(it.name, a.sessions, s.now) }
                        listOf("${free.size} de ${a.rooms.size} aulas disponibles", free.joinToString("   /   ") { it.name }, "Estimación según horarios")
                    }
                }
                Panel(Modifier.fillMaxWidth()) {
                    lines.ifEmpty { listOf("Sin registros para mostrar") }.forEach { Title(it) }
                }
                Sub("A continuación   /   " + scenes[(scene + 1) % scenes.size] + "      " + (scene + 1) + " / 5")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button({ paused = !paused }, Modifier.focusRequester(pauseControl).campusFocus()) { Text(if (paused) "Reanudar" else "Pausar") }
            OutlinedButton(exit, Modifier.campusFocus()) { Text("Salir") }
        }
        LaunchedEffect(Unit) { pauseControl.requestFocus() }
    }
}
