package com.example.smartcampus

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.example.smartcampus.data.local.*
import com.example.smartcampus.data.repository.CampusRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import java.util.UUID

class CampusDeviceTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun navigate(label: String) {
        val menu = compose.onAllNodesWithContentDescription("Abrir navegación")
        if (menu.fetchSemanticsNodes().isNotEmpty()) menu[0].performClick()
        compose.onNodeWithText(label).performClick()
    }
    @Test fun navigatesAndFilters() {
        compose.waitUntil(30000) { compose.onAllNodesWithText("Resumen del campus").fetchSemanticsNodes().isNotEmpty() }
        navigate("Horarios")
        compose.onNodeWithText("Horarios académicos").assertIsDisplayed()
        compose.onNodeWithText("Día ·", substring = true).performClick()
        compose.onNodeWithText("Domingo").performClick()
        compose.onNodeWithText("Día sin clases").assertIsDisplayed()
        navigate("Aulas")
        compose.onNodeWithText("Buscar por nombre o número").performTextInput("101")
        compose.onNodeWithText("A-101").performClick()
        compose.onNodeWithText("Próximas clases").assertExists()
        compose.onNodeWithText("Cerrar").performClick()
        navigate("Avisos")
        compose.onNodeWithText("Avisos universitarios").assertIsDisplayed()
        navigate("Eventos")
        compose.onNodeWithText("Eventos del campus").assertIsDisplayed()
        navigate("Configuración")
        compose.onNodeWithText("Preferencias de pantalla").assertIsDisplayed()
        navigate("Inicio")
        compose.onNodeWithText("Resumen del campus").assertIsDisplayed()
    }
    @Test fun displayRotatesPausesAndReturns() {
        compose.waitUntil(30000) { compose.onAllNodesWithText("Resumen del campus").fetchSemanticsNodes().isNotEmpty() }
        navigate("Configuración")
        compose.onNodeWithText("Rotación ·", substring = true).performScrollTo().performClick()
        compose.onNodeWithText("8 s").performClick()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(30000) { compose.onAllNodesWithText("Rotación · 8 s", substring = true).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Iniciar Display Mode").performScrollTo().performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(8300)
        compose.waitUntil(16000) { compose.onAllNodesWithText("Horarios del día").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Pausar").performClick()
        compose.mainClock.advanceTimeBy(9000)
        compose.onNodeWithText("Horarios del día").assertExists()
        compose.onNodeWithText("Reanudar").performClick()
        compose.waitForIdle()
        compose.mainClock.advanceTimeBy(8300)
        compose.waitUntil(16000) { compose.onAllNodesWithText("Próximos eventos").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.onNodeWithText("Preferencias de pantalla").assertExists()
        if (compose.activity.packageManager.hasSystemFeature("android.software.leanback")) {
            compose.onNodeWithText("Iniciar Display Mode").assertIsFocused()
        }
    }
    @Test fun remoteMovesFocusAndOpensSchedule() {
        val tv = compose.activity.packageManager.hasSystemFeature("android.software.leanback")
        Assume.assumeTrue(tv)
        compose.waitUntil(30000) { compose.onAllNodesWithText("Resumen del campus").fetchSemanticsNodes().isNotEmpty() }
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_DPAD_DOWN)
        compose.onNodeWithText("Horarios").assertIsFocused()
        instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_DPAD_CENTER)
        compose.onNodeWithText("Horarios académicos").assertIsDisplayed()
    }
    @Test fun adminCreatesNoticeFromForm() {
        compose.waitUntil(30000) { compose.onAllNodesWithText("Resumen del campus").fetchSemanticsNodes().isNotEmpty() }
        navigate("Configuración")
        compose.onNodeWithContentDescription("Editar avisos y eventos").performScrollTo().performClick()
        navigate("Avisos")
        compose.onNodeWithText("Crear aviso").performClick()
        val title = "Prueba " + UUID.randomUUID().toString().take(8)
        compose.onNodeWithText("Guardar").assertIsNotEnabled()
        compose.onNodeWithText("Título").performTextInput(title)
        compose.onNodeWithText("Descripción").performTextInput("Comunicado de prueba")
        compose.onNodeWithText("Guardar").performScrollTo().performClick()
        compose.onNodeWithText(title).performScrollTo().performClick()
        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Eliminar").performClick()
        compose.waitUntil { compose.onAllNodesWithText(title).fetchSemanticsNodes().isEmpty() }
    }
    @Test fun boardPersistsAcrossDatabaseReopen() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val file = "test-" + UUID.randomUUID()
        fun open() = Room.databaseBuilder(ctx, CampusDatabase::class.java, file).build()
        val db = open()
        val notice = Notice(100, "Nuevo", "Detalle", "Cultural", "2026-11-17", "Normal")
        val event = CampusEvent(100, "Muestra", "Detalle", "Aula", "2026-11-18", "09:00")
        try {
            val repo = CampusRepository(db)
            repo.initialize()
            db.dao().save(notice); db.dao().save(event)
            db.dao().save(notice.copy(title = "Editado")); db.dao().save(event.copy(place = "Patio"))
        } finally { db.close() }
        val reopened = open()
        try {
            val repo = CampusRepository(reopened)
            repo.initialize()
            val dao = reopened.dao()
            assertEquals("Editado", dao.notices().first().single { it.id == 100L }.title)
            assertEquals("Patio", dao.events().first().single { it.id == 100L }.place)
            dao.delete(notice); dao.delete(event)
            assertFalse(dao.notices().first().any { it.id == 100L })
            assertFalse(dao.events().first().any { it.id == 100L })
        } finally { reopened.close(); ctx.deleteDatabase(file) }
    }
}
