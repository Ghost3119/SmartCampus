package com.example.smartcampus.ui.screens

import androidx.compose.ui.semantics.*
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

@Composable fun Configuration(s: CampusState.Ready, model: CampusViewModel) {
    var restore by rememberSaveable { mutableStateOf(false) }
    val admin by model.admin.collectAsState()
    Page {
        Head("Configuración")
        Panel(Modifier.fillMaxWidth()) {
            Title("Preferencias de pantalla")
            Choice("Grupo preferido", s.settings.group, s.campus.academic.groups.map { it.code }, model::group)
            Choice("Tema", s.settings.theme, listOf("Sistema", "Claro", "Oscuro"), model::theme)
            Toggle("Pantalla completa", s.settings.fullscreen, model::fullscreen)
        }
        Panel(Modifier.fillMaxWidth()) {
            Title("Administración local")
            Toggle("Editar avisos y eventos", admin) { model.admin.value = it }
            Sub("Activa la edición y abre Avisos o Eventos para gestionar las publicaciones de este dispositivo.")
            OutlinedButton({ restore = true }) { Text("Restaurar datos de demostración") }
        }
        Panel(Modifier.fillMaxWidth()) {
            Title("Acerca de SmartCampus")
            Text("Universidad Tecnológica de Coahuila · Unidad 3 de Desarrollo de Dispositivos Inteligentes.")
            Sub("Demostración académica no oficial. Horarios, docentes y publicaciones son ficticios. La disponibilidad se estima con las sesiones registradas.")
        }
    }
    if (restore) Confirm("¿Restaurar la demostración?", "Se reemplazarán los avisos y eventos locales por los ejemplos iniciales con fechas actualizadas.", "Restaurar", { restore = false }) { model.restore(); restore = false }
}
@Composable private fun Toggle(label: String, value: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Switch(value, change, Modifier.semantics { contentDescription = label })
    }
}




