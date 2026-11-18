package com.example.smartcampus.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.smartcampus.data.local.*
import com.example.smartcampus.data.repository.*
import com.example.smartcampus.di.CampusApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime

sealed interface CampusState {
    data object Loading : CampusState
    data object Error : CampusState
    data class Ready(val campus: Campus, val settings: Settings, val now: LocalDateTime) : CampusState
}
@OptIn(ExperimentalCoroutinesApi::class)
class CampusViewModel(app: Application) : AndroidViewModel(app) {
    private val container = app as CampusApplication
    private val repo = container.repository
    private val prefs = container.preferences
    private val reload = MutableStateFlow(0)
    private val tick = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(1000L * (60 - LocalDateTime.now().second))
        }
    }
    val state = reload.flatMapLatest {
        flow<CampusState> {
            if (it > 0) emit(CampusState.Loading)
            repo.initialize()
            emitAll(combine(repo.campus, prefs.settings, tick, CampusState::Ready))
        }.catch { emit(CampusState.Error) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(0), CampusState.Loading)
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    val admin = MutableStateFlow(false)
    fun retry() { reload.value++ }
    fun dismiss() { _message.value = null }
    private fun update(success: String? = null, done: () -> Unit = {}, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                _message.value = success
                done()
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { _message.value = "No se pudo guardar el cambio. Intenta de nuevo." }
        }
    }
    fun group(value: String) = update { prefs.group(value) }
    fun theme(value: String) = update { prefs.theme(value) }
    fun fullscreen(value: Boolean) = update { prefs.fullscreen(value) }
    fun restore() = update("Datos de demostración restaurados.") { repo.restore() }
    fun save(item: Notice, done: () -> Unit) = update("Aviso guardado.", done) { check(admin.value); repo.dao.save(item) }
    fun save(item: CampusEvent, done: () -> Unit) = update("Evento guardado.", done) { check(admin.value); repo.dao.save(item) }
    fun delete(item: Notice, done: () -> Unit) = update("Aviso eliminado.", done) { check(admin.value); repo.dao.delete(item) }
    fun delete(item: CampusEvent, done: () -> Unit) = update("Evento eliminado.", done) { check(admin.value); repo.dao.delete(item) }
}

