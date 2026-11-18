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
import java.time.LocalTime

@Composable internal fun BoardEditor(events: Boolean, notice: Notice?, event: CampusEvent?, today: LocalDate, close: () -> Unit, model: CampusViewModel) {
    var title by rememberSaveable { mutableStateOf(event?.title ?: notice?.title.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(event?.description ?: notice?.description.orEmpty()) }
    var date by rememberSaveable { mutableStateOf(event?.date ?: notice?.date ?: today.toString()) }
    var time by rememberSaveable { mutableStateOf(event?.time ?: "10:00") }
    var place by rememberSaveable { mutableStateOf(event?.place.orEmpty()) }
    var category by rememberSaveable { mutableStateOf(notice?.category ?: "Académico") }
    var priority by rememberSaveable { mutableStateOf(notice?.priority ?: "Normal") }
    val validDate = date.length == 10 && runCatching { LocalDate.parse(date) }.isSuccess
    val validTime = time.length == 5 && runCatching { LocalTime.parse(time) }.isSuccess
    var busy by remember { mutableStateOf(false) }
    val message by model.message.collectAsState()
    LaunchedEffect(message) { if (message != null) busy = false }
    Page {
        Head((if (notice != null || event != null) "Editar " else "Crear ") + if (events) "evento" else "aviso")
        Sub("Administración local   /   " + if (events) "Eventos" else "Avisos")
        CardGrid(listOf("Título", "Descripción", "Fecha", "Clasificación"), 2) { field ->
            Panel(Modifier.fillMaxWidth()) {
                when (field) {
                    "Título" -> OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), singleLine = true, label = { Text("Título") })
                    "Descripción" -> OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Descripción") })
                    "Fecha" -> OutlinedTextField(date, { date = it }, Modifier.fillMaxWidth(), singleLine = true, isError = !validDate, label = { Text("Fecha (AAAA-MM-DD)") })
                    else -> if (events) {
                        OutlinedTextField(place, { place = it }, singleLine = true, label = { Text("Ubicación") })
                        OutlinedTextField(time, { time = it }, singleLine = true, isError = !validTime, label = { Text("Hora (HH:mm)") })
                    } else {
                        Choice("Categoría", category, categories) { category = it }
                        Choice("Prioridad", priority, listOf("Normal", "Alta")) { priority = it }
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(close, Modifier.campusFocus()) { Text("Cancelar") }
            Button(modifier = Modifier.campusFocus(), enabled = !busy && title.isNotBlank() && description.isNotBlank() && validDate && (!events || validTime && place.isNotBlank()), onClick = {
                busy = true
                if (events) model.save(CampusEvent(event?.id ?: 0, title.trim(), description.trim(), place.trim(), date, time), close)
                else model.save(Notice(notice?.id ?: 0, title.trim(), description.trim(), category, date, priority), close)
            }) { Text("Guardar") }
        }
    }
}
