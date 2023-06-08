package com.stevennt.movemate.preference

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.*
import com.stevennt.movemate.data.model.UserSession
import com.stevennt.movemate.data.model.UserData

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
    private val id = intPreferencesKey("id")
    private val userId = stringPreferencesKey("userId")
    private val displayName = stringPreferencesKey("display_name")
    private val photoUrl = stringPreferencesKey("photo_url")
    private val gender = stringPreferencesKey("gender")
    private val age = intPreferencesKey("age")
    private val height = stringPreferencesKey("height")
    private val weight = stringPreferencesKey("weight")
    private val bmi = doublePreferencesKey("bmi")
    private val bmiStatus = stringPreferencesKey("bmi_status")
    private val goal = stringPreferencesKey("goal")
    private val goalWeight = stringPreferencesKey("goal_weight")
    private val frequency = intPreferencesKey("frequency")
    private val dayStart = stringPreferencesKey("day_start")
    private val woTime = stringPreferencesKey("wo_time")
    private val createdAt = stringPreferencesKey("createdAt")
    private val updatedAt = stringPreferencesKey("updatedAt")
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

    fun getUserData(): Flow<UserData>{
        return dataStore.data.map { preferences ->
            UserData(
                preferences[id]?.toString()?.toIntOrNull() ?: 0,
                preferences[userId] ?: "",
                preferences[displayName] ?: "",
                preferences[photoUrl] ?: "",
                preferences[gender] ?: "",
                preferences[age]?.toString()?.toIntOrNull() ?: 0,
                preferences[height] ?: "",
                preferences[weight] ?: "",
                preferences[bmi]?.toString()?.toDoubleOrNull() ?: 0.00,
                preferences[bmiStatus] ?: "",
                preferences[goal] ?: "",
                preferences[goalWeight] ?: "",
                preferences[frequency]?.toString()?.toIntOrNull() ?: 0,
                preferences[dayStart] ?: "",
                preferences[woTime] ?: "",
                preferences[createdAt] ?: "",
                preferences[updatedAt] ?: "",
            )
        }
    }

    suspend fun saveUserData(userData: UserData){
        dataStore.edit { preferences ->
            preferences[id] = userData.id ?: 0
            preferences[userId] = userData.userId ?: ""
            preferences[displayName] = userData.displayName ?: ""
            preferences[photoUrl] = userData.photoUrl ?: ""
            preferences[gender] = userData.gender ?: ""
            preferences[age] = userData.age ?: 0
            preferences[height] = userData.height ?: ""
            preferences[weight] = userData.weight ?: ""
            preferences[bmi] = userData.bmi ?: 0.00
            preferences[bmiStatus] = userData.bmiStatus ?: ""
            preferences[goal] = userData.goal ?: ""
            preferences[goalWeight] = userData.goalWeight ?: ""
            preferences[frequency] = userData.frequency ?: 0
            preferences[dayStart] = userData.dayStart ?: ""
            preferences[woTime] = userData.woTime ?: ""
            preferences[createdAt] = userData.createdAt ?: ""
            preferences[updatedAt] = userData.updatedAt ?: ""
        }
    }

    suspend fun saveTokenLoginStatus(token: String, isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[this.token] = token
            preferences[this.isLoggedIn] = isLoggedIn
        }
    }

}