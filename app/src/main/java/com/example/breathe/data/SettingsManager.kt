package com.shanacoder.breathly.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {
    companion object {
        val SOUNDS_ENABLED = booleanPreferencesKey("sounds_enabled")
        val FOLLOW_RHYTHM_ENABLED = booleanPreferencesKey("follow_rhythm_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val PROGRESS_SECTION_ORDER = stringPreferencesKey("progress_section_order")
        val HOME_SECTION_ORDER = stringPreferencesKey("home_section_order")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val DND_MODE_ENABLED = booleanPreferencesKey("dnd_mode_enabled")
    }

    val soundsEnabled: Flow<Boolean> = context.dataStore.data.map { it[SOUNDS_ENABLED] ?: true }
    val followRhythmEnabled: Flow<Boolean> = context.dataStore.data.map { it[FOLLOW_RHYTHM_ENABLED] ?: true }
    val hapticsEnabled: Flow<Boolean> = context.dataStore.data.map { it[HAPTICS_ENABLED] ?: true }
    val progressSectionOrder: Flow<String> = context.dataStore.data.map { it[PROGRESS_SECTION_ORDER] ?: "Stats,Chart,BreathHold" }
    val homeSectionOrder: Flow<String> = context.dataStore.data.map { it[HOME_SECTION_ORDER] ?: "Equal,Box,478,Hold" }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: false }
    val notificationTime: Flow<String> = context.dataStore.data.map { it[NOTIFICATION_TIME] ?: "08:00" }
    val dndModeEnabled: Flow<Boolean> = context.dataStore.data.map { it[DND_MODE_ENABLED] ?: false }

    suspend fun setSoundsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUNDS_ENABLED] = enabled }
    }

    suspend fun setFollowRhythmEnabled(enabled: Boolean) {
        context.dataStore.edit { it[FOLLOW_RHYTHM_ENABLED] = enabled }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[HAPTICS_ENABLED] = enabled }
    }

    suspend fun setProgressSectionOrder(order: String) {
        context.dataStore.edit { it[PROGRESS_SECTION_ORDER] = order }
    }

    suspend fun setHomeSectionOrder(order: String) {
        context.dataStore.edit { it[HOME_SECTION_ORDER] = order }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationTime(time: String) {
        context.dataStore.edit { it[NOTIFICATION_TIME] = time }
    }

    suspend fun setDndModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[DND_MODE_ENABLED] = enabled }
    }
}
