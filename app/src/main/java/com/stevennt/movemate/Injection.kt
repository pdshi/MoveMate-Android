package com.stevennt.movemate

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.stevennt.movemate.data.MoveMateRepo
import com.stevennt.movemate.data.network.APIConfig
import com.stevennt.movemate.preference.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus

private const val USER_PREF = "user_session"

object Injection {
    fun provideRepository(context: Context): MoveMateRepo {
        val apiService = APIConfig.getApiService(context)

        return MoveMateRepo(apiService, provideUserPreferences(context))
    }

    fun provideUserPreferences(context: Context): UserPreferences {
        val dataStore = PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(context, USER_PREF)),
            scope = CoroutineScope(Dispatchers.IO) + SupervisorJob(),
            produceFile = { context.preferencesDataStoreFile(USER_PREF) }
        )
        return UserPreferences.getInstance(dataStore)
    }
}