package cl.duoc.tecexpress.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USERNAME = stringPreferencesKey("username")
        val PASSWORD = stringPreferencesKey("password")
    }

    val storedUser: Flow<Pair<String, String>> = context.dataStore.data
        .map {
            val username = it[PreferencesKeys.USERNAME] ?: ""
            val password = it[PreferencesKeys.PASSWORD] ?: ""
            Pair(username, password)
        }

    suspend fun saveUser(username: String, pass: String) {
        context.dataStore.edit {
            it[PreferencesKeys.USERNAME] = username
            it[PreferencesKeys.PASSWORD] = pass
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit {
            it.clear()
        }
    }
}