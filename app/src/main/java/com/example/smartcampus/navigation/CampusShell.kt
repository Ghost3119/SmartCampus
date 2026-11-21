package com.example.smartcampus.navigation

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
import android.content.res.Configuration.UI_MODE_TYPE_MASK
import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.*
import androidx.compose.ui.semantics.*
import androidx.navigation.compose.*
import com.example.smartcampus.R
import com.example.smartcampus.ui.CampusViewModel
import com.example.smartcampus.ui.screens.*
import com.example.smartcampus.ui.theme.CampusDesign

private val sections = linkedMapOf("Inicio" to R.drawable.nav_inicio, "Horarios" to R.drawable.nav_horarios,
    "Aulas" to R.drawable.nav_aulas, "Avisos" to R.drawable.nav_avisos, "Eventos" to R.drawable.nav_eventos, "Configuración" to R.drawable.nav_ajustes)

@Composable private fun Destination(name: String, active: Boolean, tv: Boolean, modifier: Modifier, go: () -> Unit) {
    Row(modifier.heightIn(min = 48.dp).background(if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, CampusDesign.corner)
        .campusFocus().clickable(onClick = go).semantics { selected = active }.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(name, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        if (tv) Icon(painterResource(sections.getValue(name)), null, Modifier.size(18.dp))
    }
}

@Composable fun CampusShell(state: CampusState.Ready, model: CampusViewModel) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "Inicio"
    val tv = isTelevision()
    val first = remember { FocusRequester() }
    val go: (String) -> Unit = { name -> nav.navigate(name) { popUpTo("Inicio") { saveState = true }; launchSingleTop = true; restoreState = true } }
    var target by rememberSaveable { mutableStateOf<Long?>(null) }
    val host = remember { SnackbarHostState() }
    val message by model.message.collectAsState()
    LaunchedEffect(message) { message?.let { host.showSnackbar(it); model.dismiss() } }
    var menu by remember { mutableStateOf(false) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val horizontal = !tv && maxWidth >= 760.dp
        val display = route == "Display"
        val time = clock(state.now.hour * 60 + state.now.minute)
        Row(Modifier.fillMaxSize().padding(if (tv) 32.dp else 0.dp)) {
            if (tv && !display) Column(Modifier.width(176.dp).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CampusLogo(); Text("SmartCampus", style = MaterialTheme.typography.titleMedium, maxLines = 1)
                Spacer(Modifier.height(12.dp))
                sections.keys.forEach { name ->
                    Destination(name, route == name, true, Modifier.fillMaxWidth().then(if (name == "Inicio") Modifier.focusRequester(first) else Modifier)) { go(name) }
                }
                Sub("Demostración no oficial")
                LaunchedEffect(Unit) { if (route == "Inicio") first.requestFocus() }
            }
            Column(Modifier.weight(1f)) {
                if (!display) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = if (horizontal) 32.dp else 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (!tv) {
                            if (!horizontal) Box {
                                IconButton({ menu = true }) { Icon(painterResource(R.drawable.nav_inicio), "Abrir navegación") }
                                DropdownMenu(menu, { menu = false }) { sections.keys.forEach { DropdownMenuItem(text = { Text(it) }, onClick = { menu = false; go(it) }) } }
                            }
                            CampusLogo(); Text("SmartCampus", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.weight(1f))
                        if ((tv || horizontal) && route == "Inicio") Sub("Resumen del campus")
                        if (horizontal) Sub("DEMOSTRACIÓN NO OFICIAL")
                        Text(time, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    }
                    if (horizontal) Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sections.keys.forEach { Destination(it, it == route, false, Modifier.weight(1f)) { go(it) } }
                    }
                }
                NavHost(nav, "Inicio", Modifier.weight(1f)) {
                    composable("Inicio") { Dashboard(state, go) { module, id -> target = id; go(module) } }
                    composable("Horarios") { Schedule(state, model) }
                    composable("Aulas") { Rooms(state) }
                    composable("Avisos") { Board(state, model, false, target) { target = null } }
                    composable("Eventos") { Board(state, model, true, target) { target = null } }
                    composable("Configuración") { Configuration(state, model, go) { nav.navigate("Display") } }
                    composable("Display") { DisplayMode(state) { nav.popBackStack() } }
                }
            }
        }
        SnackbarHost(host, Modifier.align(Alignment.BottomCenter))
    }
}
