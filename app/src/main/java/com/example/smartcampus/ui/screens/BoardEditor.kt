package com.example.smartcampus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.smartcampus.data.local.*
import com.example.smartcampus.domain.*
import com.example.smartcampus.ui.*
import com.example.smartcampus.ui.components.*
import java.time.LocalDate

@Composable internal fun BoardEditor(events: Boolean, notice: Notice?, event: CampusEvent?, today: LocalDate, close: () -> Unit, model: CampusViewModel) {
    var title by rememberSaveable { mutableStateOf(event?.title ?: notice?.title.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(event?.description ?: notice?.description.orEmpty()) }
    var date by rememberSaveable { mutableStateOf(event?.date ?: notice?.date ?: today.toString()) }
    var time by rememberSaveable { mutableStateOf(event?.time ?: "10:00") }
    var place by rememberSaveable { mutableStateOf(event?.place.orEmpty()) }
    var category by rememberSaveable { mutableStateOf(notice?.category ?: "Académico") }
    var priority by rememberSaveable { mutableStateOf(notice?.priority ?: "Normal") }
    val validDate = isValidDate(date)
    val validTime = isValidTime(time)
    val valid = if (events) isEventValid(title, description, place, date, time) else isNoticeValid(title, description, date)
    var busy by remember { mutableStateOf(false) }
    Page {
        Head((if (notice != null || event != null) "Editar " else "Crear ") + if (events) "evento" else "aviso")
        Sub("Administración local   /   " + if (events) "Eventos" else "Avisos")
        CardGrid(listOf("Título", "Descripción", "Fecha", "Clasificación"), 2) { field ->
            Panel(Modifier.fillMaxWidth().fillMaxHeight()) {
                when (field) {
                    "Título" -> OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Título") })
                    "Descripción" -> OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Descripción") })
                    "Fecha" -> OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = !validDate,
                        label = { Text("Fecha (AAAA-MM-DD)") },
                        supportingText = if (!validDate) {
                            { Text("Usa el formato AAAA-MM-DD, por ejemplo ${today}.") }
                        } else null
                    )
                    else -> if (events) {
                        OutlinedTextField(place, { place = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Ubicación") })
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !validTime,
                            label = { Text("Hora (HH:mm)") },
                            supportingText = if (!validTime) {
                                { Text("Usa formato 24 h HH:mm, por ejemplo 10:00.") }
                            } else null
                        )
                    } else {
                        Choice("Categoría", category, categories) { category = it }
                        Choice("Prioridad", priority, listOf("Normal", "Alta")) { priority = it }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(close, Modifier.campusFocus(CircleShape)) { Text("Cancelar") }
            Button(
                modifier = Modifier.campusFocus(CircleShape),
                enabled = !busy && valid,
                onClick = {
                    busy = true
                    if (events) {
                        model.save(
                            CampusEvent(event?.id ?: 0, title.trim(), description.trim(), place.trim(), date, time),
                            done = close,
                            onFinished = { busy = false }
                        )
                    } else {
                        model.save(
                            Notice(notice?.id ?: 0, title.trim(), description.trim(), category, date, priority),
                            done = close,
                            onFinished = { busy = false }
                        )
                    }
                }
            ) { Text("Guardar") }
        }
    }
}
