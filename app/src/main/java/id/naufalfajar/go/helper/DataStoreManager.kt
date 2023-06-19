package id.naufalfajar.go.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_FIRST_TIME_KEY = booleanPreferencesKey("is_first_time")
    }

    val isFirstTimeUser: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_FIRST_TIME_KEY] ?: true
    }

    suspend fun setFirstTimeUser(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_TIME_KEY] = isFirstTime
        }
    }
}