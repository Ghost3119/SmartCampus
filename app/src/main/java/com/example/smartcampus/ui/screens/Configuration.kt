package com.example.smartcampus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import com.example.smartcampus.ui.CampusState
import com.example.smartcampus.ui.CampusViewModel
import com.example.smartcampus.ui.components.*
import com.example.smartcampus.ui.theme.CampusDesign

private val configCards = listOf(
    "PREFERENCIAS" to "Grupo preferido",
    "APARIENCIA" to "Tema de pantalla",
    "VISUALIZACIÓN" to "Pantalla completa",
    "GESTIÓN LOCAL" to "Administración local",
    "DATOS DE DEMOSTRACIÓN" to "Restaurar ejemplos",
    "ACERCA DE" to "Acerca de SmartCampus"
)

@Composable fun Configuration(s: CampusState.Ready, model: CampusViewModel, manage: (String) -> Unit, startDisplay: () -> Unit) {
    var restore by rememberSaveable { mutableStateOf(false) }
    var returningFromDisplay by rememberSaveable { mutableStateOf(false) }
    val displayButton = remember { FocusRequester() }
    val tv = isTelevision()
    val admin by model.admin.collectAsState()
    Page {
        Head("Configuración")
        Sub("Preferencias de pantalla")
        CardGrid(configCards, 3) { (kicker, name) ->
            Panel(Modifier.fillMaxWidth().fillMaxHeight()) {
                Text(kicker, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Title(name, compact = tv)
                when (name) {
                    "Grupo preferido" -> Choice("Grupo", s.settings.group, s.campus.academic.groups.map { it.code }, model::group)
                    "Tema de pantalla" -> Choice("Tema", s.settings.theme, listOf("Sistema","Claro","Oscuro"), model::theme)
                    "Pantalla completa" -> Toggle("Ocultar barras", s.settings.fullscreen, model::fullscreen)
                    "Administración local" -> {
                        Toggle("Editar avisos y eventos", admin) { model.admin.value = it }
                        if (admin) {
                            TextButton({ manage("Avisos") }, Modifier.campusFocus(CircleShape)) { Text("Gestionar avisos") }
                            TextButton({ manage("Eventos") }, Modifier.campusFocus(CircleShape)) { Text("Gestionar eventos") }
                        }
                    }
                    "Restaurar ejemplos" -> OutlinedButton(
                        onClick = { restore = true },
                        modifier = Modifier.campusFocus(CircleShape),
                        contentPadding = PaddingValues(horizontal = if (tv) 14.dp else 20.dp, vertical = 10.dp)
                    ) { Text("Restaurar datos de demostración") }
                    else -> Sub("Universidad Tecnológica de Coahuila · Unidad 3. Demostración académica con datos ficticios.")
                }
            }
        }
        Panel(Modifier.fillMaxWidth()) {
            Title("SmartCampus Display Mode")
            Choice("Rotación", "${s.settings.rotationSeconds} s", listOf("8 s","12 s","20 s","30 s")) { model.interval(it.substringBefore(" ").toInt()) }
            Button({ returningFromDisplay = true; startDisplay() }, Modifier.focusRequester(displayButton).campusFocus(CircleShape)) { Text("Iniciar Display Mode") }
            LaunchedEffect(Unit) {
                if (returningFromDisplay && tv) displayButton.requestFocus()
                returningFromDisplay = false
            }
        }
    }
    if (restore) Confirm("¿Restaurar la demostración?", "Se reemplazarán los avisos y eventos locales por los ejemplos iniciales con fechas actualizadas.", "Restaurar", { restore = false }) { model.restore(); restore = false }
}
@Composable private fun Toggle(label: String, value: Boolean, change: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .campusFocus()
            .clip(CampusDesign.corner)
            .toggleable(value, role = Role.Switch, onValueChange = change)
            .semantics { contentDescription = label }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, Modifier.weight(1f))
        Switch(value, null)
    }
}
