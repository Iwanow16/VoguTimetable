package ru.test.data.storage.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoguStore @Inject constructor(
    @ApplicationContext context: Context
) {

    // Keys
    companion object {
        private val CSRF_TOKEN = stringPreferencesKey("csrf_token")
        private val TIMETABLE_ID = intPreferencesKey("timetable_id")
        private val TIMETABLE_TYPE = stringPreferencesKey("timetable_type")
        private val TIMETABLE_LESSON_TYPE = stringPreferencesKey("timetable_lesson_type")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timetable_settings")
    private val dataStore = context.dataStore

    // TOKEN
    val csrfToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[CSRF_TOKEN]
        }

    suspend fun saveCsrfToken(token: String) {
        dataStore.edit { preferences ->
            preferences[CSRF_TOKEN] = token
        }
    }

    // TIMETABLE ID
    val timetableId: Flow<Int?> = dataStore.data
        .map { preferences ->
            preferences[TIMETABLE_ID]
        }

    suspend fun saveTimetableId(timetableId: Int) {
        dataStore.edit { preferences ->
            preferences[TIMETABLE_ID] = timetableId
        }
    }

    // TIMETABLE TYPE
    val timetableType: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TIMETABLE_TYPE]
        }

    suspend fun saveTimetableType(timetableType: String) {
        dataStore.edit { preferences ->
            preferences[TIMETABLE_TYPE] = timetableType
        }
    }

    // LESSON TYPE
    val lessonType: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TIMETABLE_LESSON_TYPE]
        }

    suspend fun saveLessonType(lessonType: String) {
        dataStore.edit { preferences ->
            preferences[TIMETABLE_LESSON_TYPE] = lessonType
        }
    }
}