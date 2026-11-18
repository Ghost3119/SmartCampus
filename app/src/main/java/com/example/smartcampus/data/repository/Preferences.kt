package com.example.smartcampus.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore("campus_settings")
data class Settings(val group: String = "9IDGSB", val theme: String = "Sistema", val fullscreen: Boolean = false)
class Preferences(private val context: Context) {
    private val group = stringPreferencesKey("group")
    private val theme = stringPreferencesKey("theme")
    private val full = booleanPreferencesKey("fullscreen")
    val settings = context.store.data.map { Settings(it[group] ?: "9IDGSB", it[theme] ?: "Sistema", it[full] ?: false) }
    suspend fun group(value: String) { context.store.edit { it[group] = value } }
    suspend fun theme(value: String) { context.store.edit { it[theme] = value } }
    suspend fun fullscreen(value: Boolean) { context.store.edit { it[full] = value } }
}
