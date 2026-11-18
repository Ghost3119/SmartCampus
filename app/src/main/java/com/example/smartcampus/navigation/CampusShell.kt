package com.example.smartcampus.navigation

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
import androidx.compose.ui.semantics.*
import androidx.navigation.compose.*
import com.example.smartcampus.R
import com.example.smartcampus.ui.screens.*

private val sections = linkedMapOf("Inicio" to R.drawable.nav_inicio, "Horarios" to R.drawable.nav_horarios, "Aulas" to R.drawable.nav_aulas, "Avisos" to R.drawable.nav_avisos, "Eventos" to R.drawable.nav_eventos, "Configuración" to R.drawable.nav_ajustes)
@Composable fun CampusShell(state: CampusState.Ready, model: CampusViewModel) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val selected = entry?.destination?.route ?: "Inicio"
    val go: (String) -> Unit = { route -> nav.navigate(route) { popUpTo("Inicio") { saveState = true }; launchSingleTop = true; restoreState = true } }
    var menu by remember { mutableStateOf(false) }
    val host = remember { SnackbarHostState() }
    val message by model.message.collectAsState()
    LaunchedEffect(message) { message?.let { host.showSnackbar(it); model.dismiss() } }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 840.dp
        Row {
            if (wide) Column(Modifier.width(208.dp).fillMaxHeight().background(Color(0xFF102D4C)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Spacer(Modifier.height(10.dp))
                Text("SmartCampus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 23.sp)
                Spacer(Modifier.height(30.dp))
                sections.forEach { (name, res) ->
                    val active = name == selected
                    Surface(onClick = { go(name) }, shape = RoundedCornerShape(12.dp), color = if (active) Color(0xFF2456A0) else Color.Transparent, contentColor = Color.White) {
                        Row(Modifier.fillMaxWidth().heightIn(min = 50.dp).padding(horizontal = 14.dp).semantics { this.selected = active }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Icon(painterResource(res), null, Modifier.size(21.dp)); Text(name)
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Text("UTC  /  Coahuila", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Desarrollo de Dispositivos\nInteligentes · Unidad 3", color = Color(0xFFBFD3EA), fontSize = 12.sp, lineHeight = 18.sp)
            }
            Column {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = if (wide) 32.dp else 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (!wide) Box {
                            IconButton({ menu = true }) { Icon(painterResource(R.drawable.nav_inicio), "Abrir navegación") }
                            DropdownMenu(menu, { menu = false }) { sections.keys.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { menu = false; go(it) }) } }
                        }
                        Column(Modifier.weight(1f)) { Title(if (wide) "Universidad Tecnológica de Coahuila" else "SmartCampus"); Sub("Demostración no oficial") }
                        Text(state.now.toLocalDate().label(if (wide) "EEE, d MMM yyyy" else "d MMM") + "   " + clock(state.now.hour * 60 + state.now.minute), style = MaterialTheme.typography.labelLarge)
                    }
                }
                NavHost(nav, "Inicio", Modifier.weight(1f)) {
                    composable("Inicio") { Dashboard(state, go) }
                    composable("Horarios") { Schedule(state) }
                    composable("Aulas") { Rooms(state) }
                    composable("Avisos") { Board(state, model, false) }
                    composable("Eventos") { Board(state, model, true) }
                    composable("Configuración") { Configuration(state, model) }
                }
            }
        }
        SnackbarHost(host, Modifier.align(Alignment.BottomCenter))
    }
}

