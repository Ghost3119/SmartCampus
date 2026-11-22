package com.example.smartcampus.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.smartcampus.domain.*
import com.example.smartcampus.ui.CampusState
import com.example.smartcampus.ui.components.*
import kotlinx.coroutines.delay

private val scenes = listOf("Bienvenida", "Horarios del día", "Próximos eventos", "Avisos importantes", "Aulas disponibles")

@Composable fun DisplayMode(s: CampusState.Ready, exit: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var paused by rememberSaveable { mutableStateOf(false) }
    var manualTurn by remember { mutableIntStateOf(0) }
    val previousScene: () -> Unit = { index = (index - 1 + scenes.size) % scenes.size; manualTurn++ }
    val nextScene: () -> Unit = { index = (index + 1) % scenes.size; manualTurn++ }
    val owner = LocalLifecycleOwner.current
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val tv = isTelevision()
    DisposableEffect(view) {
        val previous = view.keepScreenOn
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = previous }
    }
    LaunchedEffect(paused, s.settings.rotationSeconds, owner, manualTurn) {
        if (!paused) owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) { delay(s.settings.rotationSeconds * 1000L); index = (index + 1) % scenes.size }
        }
    }
    val pauseControl = remember { FocusRequester() }
    Box(
        Modifier
            .fillMaxSize()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_LEFT -> {
                            if (!focusManager.moveFocus(FocusDirection.Left)) previousScene()
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            if (!focusManager.moveFocus(FocusDirection.Right)) nextScene()
                            true
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        Page {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CampusLogo()
                Column(Modifier.weight(1f)) { Title("SmartCampus / Display Mode"); Sub("Demostración no oficial") }
                Sub(s.now.toLocalDate().label("d MMM yyyy") + "   " + clock(s.now.hour * 60 + s.now.minute))
            }
            Crossfade(index, label = "Escena informativa") { scene ->
                Column(verticalArrangement = Arrangement.spacedBy(if (tv) 14.dp else 22.dp)) {
                    Head(scenes[scene])
                    val a = s.campus.academic
                    val (header, lines) = when (scene) {
                        0 -> null to listOf("Universidad Tecnológica de Coahuila", s.now.toLocalDate().label("EEEE d 'de' MMMM"))
                        1 -> {
                            val todaySessions = timetable(a.sessions, s.settings.group, s.now.dayOfWeek.value)
                            if (todaySessions.isNotEmpty()) {
                                null to todaySessions.map { clock(it.start) + "   " + it.subject + "   ·   " + it.room }
                            } else {
                                val nextSessions = upcoming(a.sessions.filter { it.groupCode == s.settings.group }, s.now).take(3)
                                if (nextSessions.isEmpty()) null to emptyList()
                                else "Sin clases hoy · Próximas sesiones:" to nextSessions.map {
                                    "${it.date.label("EEE d MMM")}  ${clock(it.session.start)}  ${it.session.subject}  ·  ${it.session.room}"
                                }
                            }
                        }
                        2 -> null to upcomingEvents(s.campus.events, s.now).take(3).map { LocalDateLabel(it.date) + "   " + it.title + "   ·   " + it.place }
                        3 -> null to s.campus.notices.filter { it.priority == "Alta" }.take(3).flatMap { listOf(it.title, it.description) }
                        else -> {
                            val free = a.rooms.filter { available(it.name, a.sessions, s.now) }
                            null to listOf("${free.size} de ${a.rooms.size} aulas disponibles", free.joinToString("   /   ") { it.name }, "Estimación según horarios")
                        }
                    }
                    Panel(
                        Modifier
                            .fillMaxWidth()
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.type == KeyEventType.KeyDown) {
                                    when (keyEvent.nativeKeyEvent.keyCode) {
                                        android.view.KeyEvent.KEYCODE_DPAD_LEFT -> { previousScene(); true }
                                        android.view.KeyEvent.KEYCODE_DPAD_RIGHT -> { nextScene(); true }
                                        else -> false
                                    }
                                } else false
                            }
                            .then(if (tv) Modifier.campusFocus().focusable() else Modifier)
                    ) {
                        header?.let {
                            Text(it, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        lines.ifEmpty { listOf("Sin registros para mostrar") }.forEach {
                            Title(it, compact = tv && header != null)
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scenes.indices.forEach { dot ->
                            val active = dot == scene
                            Box(
                                Modifier
                                    .size(width = if (active) 24.dp else 8.dp, height = 8.dp)
                                    .background(
                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = CircleShape
                                    )
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Sub("A continuación   /   " + scenes[(scene + 1) % scenes.size] + "      " + (scene + 1) + " / " + scenes.size)
                    }
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(previousScene, Modifier.campusFocus(CircleShape)) { Text("Anterior") }
                Button({ paused = !paused }, Modifier.focusRequester(pauseControl).campusFocus(CircleShape)) { Text(if (paused) "Reanudar" else "Pausar") }
                OutlinedButton(nextScene, Modifier.campusFocus(CircleShape)) { Text("Siguiente") }
                OutlinedButton(exit, Modifier.campusFocus(CircleShape)) { Text("Salir") }
            }
            LaunchedEffect(Unit) { pauseControl.requestFocus() }
        }
    }
}
