package com.stevennt.movemate.preference

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stevennt.movemate.data.model.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserPreferences(private val dataStore: DataStore<Preferences>) {

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
            preferences[id] = userData.dataId ?: 0
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

    fun getUserHistory(): Flow<List<UserHistory>> {
        return dataStore.data.map { preferences ->
            val userHistoryJson = preferences[USER_HISTORY_KEY] ?: return@map emptyList()

            val listType = object : TypeToken<List<UserHistory>>() {}.type
            val userHistoryList = Gson().fromJson<List<UserHistory>>(userHistoryJson, listType)

            userHistoryList
        }
    }

    suspend fun saveUserHistory(userHistory: UserHistory){

        val existingHistory = userHistories.find { it.historyId == userHistory.historyId }

        if (existingHistory != null) {
            return
        }

        dataStore.edit { preferences ->
            preferences[historyId] = userHistory.historyId ?: 0
            preferences[userId] = userHistory.userId ?: ""
            preferences[type] = userHistory.type ?: ""
            preferences[time] = userHistory.time ?: 0.0
            preferences[calories] = userHistory.calories ?: 0.0
            preferences[date] = userHistory.date ?: ""
        }

        userHistories.add(userHistory)
    }

    fun getUserReps(): Flow<List<UserReps>> {
        return dataStore.data.map { preferences ->
            val userRepsJson = preferences[USER_HISTORY_KEY] ?: return@map emptyList()

            val listType = object : TypeToken<List<UserReps>>() {}.type
            val userRepsList = Gson().fromJson<List<UserReps>>(userRepsJson, listType)

            userRepsList
        }
        /*return dataStore.data.map { preferences ->
            val repsId = preferences[repsId] ?: 0
            val userId = preferences[userId] ?: ""
            val type = preferences[type] ?: ""
            val reps = preferences[reps] ?: 0
            val sets = preferences[sets] ?: 0
            val date = preferences[date] ?: ""
            val start = preferences[start] ?: ""
            val end = preferences[end] ?: ""
            val createdAt = preferences[createdAt] ?: ""
            val updatedAt = preferences[updatedAt] ?: ""

            listOf(
                UserReps(
                    repsId,
                    userId,
                    type,
                    reps,
                    sets,
                    date,
                    start,
                    end,
                    createdAt,
                    updatedAt
                )
            )
        }*/
    }

    suspend fun saveUserReps(userReps: UserReps){

        val existingReps = userRepsList.find { it.repsId == userReps.repsId }

        if (existingReps != null) {
            return
        }

        dataStore.edit { preferences ->
            preferences[repsId] = userReps.repsId ?: 0
            preferences[userId] = userReps.userId ?: ""
            preferences[type] = userReps.type ?: ""
            preferences[reps] = userReps.reps ?: 0
            preferences[sets] = userReps.sets ?: 0
            preferences[date] = userReps.date ?: ""
            preferences[start] = userReps.start ?: ""
            preferences[end] = userReps.end ?: ""
            preferences[createdAt] = userReps.createdAt ?: ""
            preferences[updatedAt] = userReps.updatedAt ?: ""
        }

        userRepsList.add(userReps)
    }


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

        val userHistories: MutableList<UserHistory> = mutableListOf()
        val userRepsList: MutableList<UserReps> = mutableListOf()
        val workoutList = mutableListOf<Workouts>()
        var currentWorkoutIndex: Int = 0
        var size: Int = 0

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
        private val type = stringPreferencesKey("type")
        private val time = doublePreferencesKey("time")
        private val calories = doublePreferencesKey("calories")
        private val date = stringPreferencesKey("date")
        private val historyId = intPreferencesKey("id")
        private val repsId = intPreferencesKey("id")
        private val reps = intPreferencesKey("reps")
        private val sets = intPreferencesKey("sets")
        private val start = stringPreferencesKey("start")
        private val end = stringPreferencesKey("end")

        private val USER_HISTORY_KEY = stringPreferencesKey("user_history")

    }
}