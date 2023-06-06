package com.stevennt.movemate.preference

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.*
import com.stevennt.movemate.data.network.UserSession

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    private val token = stringPreferencesKey("token")
    private val isLoggedIn = booleanPreferencesKey("is_logged_in")

    fun getUserSession(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                preferences[token] ?: "",
            )
        }
    }

    suspend fun saveUserSession(userSession: UserSession) {
        dataStore.edit { preferences ->
            preferences[token] = userSession.token ?: ""
        }
    }

    suspend fun destroyUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(token)
        }
    }

    suspend fun saveTokenLoginStatus(token: String, isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[this.token] = token
            preferences[this.isLoggedIn] = isLoggedIn
        }
    }

}