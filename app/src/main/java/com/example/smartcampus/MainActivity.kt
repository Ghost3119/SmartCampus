package com.example.smartcampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcampus.navigation.CampusShell
import com.example.smartcampus.ui.*
import com.example.smartcampus.ui.components.campusFocus
import com.example.smartcampus.ui.theme.CampusTheme

class MainActivity : ComponentActivity() {
    private val model by viewModels<CampusViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by model.state.collectAsStateWithLifecycle()
            val ready = state as? CampusState.Ready
            LaunchedEffect(ready?.settings?.fullscreen) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                if (ready?.settings?.fullscreen == true) controller.hide(WindowInsetsCompat.Type.systemBars())
                else controller.show(WindowInsetsCompat.Type.systemBars())
            }
            CampusTheme(ready?.settings?.theme ?: "Claro") {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = Alignment.Center) {
                        when {
                            ready != null -> CampusShell(ready, model)
                            state is CampusState.Error -> Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("No se pudieron cargar los datos locales.")
                                Button(model::retry, Modifier.campusFocus(CircleShape)) { Text("Reintentar") }
                            }
                            else -> CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
