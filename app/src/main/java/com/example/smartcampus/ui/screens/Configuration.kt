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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.*
import com.example.smartcampus.ui.CampusViewModel

@Composable fun Configuration(s: CampusState.Ready, model: CampusViewModel, manage: (String) -> Unit, startDisplay: () -> Unit) {
    var restore by rememberSaveable { mutableStateOf(false) }
    var returningFromDisplay by rememberSaveable { mutableStateOf(false) }
    val displayButton = remember { FocusRequester() }
    val tv = isTelevision()
    val admin by model.admin.collectAsState()
    Page {
        Head("Configuración")
        Sub("Preferencias de pantalla")
        CardGrid(listOf("Grupo preferido","Tema de pantalla","Pantalla completa","Administración local","Restaurar ejemplos","Acerca de SmartCampus"), 3) { name ->
            Panel(Modifier.fillMaxWidth()) {
                Title(name)
                when (name) {
                    "Grupo preferido" -> Choice("Grupo", s.settings.group, s.campus.academic.groups.map { it.code }, model::group)
                    "Tema de pantalla" -> Choice("Tema", s.settings.theme, listOf("Sistema","Claro","Oscuro"), model::theme)
                    "Pantalla completa" -> Toggle("Ocultar barras", s.settings.fullscreen, model::fullscreen)
                    "Administración local" -> {
                        Toggle("Editar avisos y eventos", admin) { model.admin.value = it }
                        if (admin) {
                            TextButton({ manage("Avisos") }, Modifier.campusFocus()) { Text("Gestionar avisos") }
                            TextButton({ manage("Eventos") }, Modifier.campusFocus()) { Text("Gestionar eventos") }
                        }
                    }
                    "Restaurar ejemplos" -> OutlinedButton({ restore = true }, Modifier.campusFocus()) { Text("Restaurar datos de demostración") }
                    else -> Sub("Universidad Tecnológica de Coahuila · Unidad 3. Demostración académica con datos ficticios.")
                }
            }
        }
        Panel(Modifier.fillMaxWidth()) {
            Title("SmartCampus Display Mode")
            Choice("Rotación", "${s.settings.rotationSeconds} s", listOf("8 s","12 s","20 s","30 s")) { model.interval(it.substringBefore(" ").toInt()) }
            Button({ returningFromDisplay = true; startDisplay() }, Modifier.focusRequester(displayButton).campusFocus()) { Text("Iniciar Display Mode") }
            LaunchedEffect(Unit) {
                if (returningFromDisplay && tv) displayButton.requestFocus()
                returningFromDisplay = false
            }
        }
    }
    if (restore) Confirm("¿Restaurar la demostración?", "Se reemplazarán los avisos y eventos locales por los ejemplos iniciales con fechas actualizadas.", "Restaurar", { restore = false }) { model.restore(); restore = false }
}
@Composable private fun Toggle(label: String, value: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().campusFocus().toggleable(value, role = Role.Switch, onValueChange = change)
        .semantics { contentDescription = label }, verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(value, null)
    }
}
