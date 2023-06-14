package com.stevennt.movemate.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.stevennt.movemate.data.network.APIService
import com.stevennt.movemate.data.model.UserData
import com.stevennt.movemate.data.model.UserHistory
import com.stevennt.movemate.data.model.UserReps
import com.stevennt.movemate.data.model.UserSession
import com.stevennt.movemate.data.network.response.*
import com.stevennt.movemate.preference.UserPreferences

class MoveMateRepo(private val apiService: APIService, private val userPreferences: UserPreferences,) {

    fun login(email: String, pass: String): LiveData<Resource<LoginResp>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.login(email, pass)
            if(response.success == true) {
                userPreferences.saveUserSession(
                    UserSession(
                        response.token,
                    )
                )
                emit(Resource.Success(response))
            }else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.d("login", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

     fun loginWithFirebase(authToken: String): LiveData<Resource<FirebaseResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.firebase("Bearer $authToken")

            if(response.success == true) {
                userPreferences.saveUserSession(
                    UserSession(
                        response.token,
                    )
                )
                emit(Resource.Success(response))
            }else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.d("login_with_firebase", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun register(
        email: String,
        pass: String,

    ): LiveData<Resource<RegisterResp>> = liveData {
        emit(Resource.Loading)
        try {

            val response = apiService.register(email, pass, key = "supersecretapikey")
            emit(Resource.Success(response))

        } catch (e: Exception) {
            Log.d("register", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getUserData(authToken: String): LiveData<Resource<GetUserResp>> = liveData{
        emit(Resource.Loading)
        try {

            val response = apiService.getUserData("Bearer $authToken")
            if(response.success == true) {
                userPreferences.saveUserData(
                    UserData(
                        response.data?.id,
                        response.data?.userId,
                        response.data?.displayName,
                        response.data?.photoUrl,
                        response.data?.gender,
                        response.data?.age,
                        response.data?.height,
                        response.data?.weight,
                        response.data?.bmi,
                        response.data?.bmiStatus,
                        response.data?.goal,
                        response.data?.goalWeight,
                        response.data?.frequency,
                        response.data?.dayStart,
                        response.data?.woTime,
                        response.data?.createdAt,
                        response.data?.updatedAt

                    )
                )
                emit(Resource.Success(response))
            }else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }

        } catch (e: Exception) {
            Log.d("get_user_data", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun inputUserData(
        authToken: String,
        displayName: String,
        photoUrl: String,
        gender: String,
        age: String,
        height: String,
        weight: String,
        goal: String,
        goalWeight: String,
        frequency: String,
        dayStart: String,
        woTime: String,
    ): LiveData<Resource<InputUserDataResp>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.inputUserData(
                "Bearer $authToken",
                displayName,
                photoUrl,
                gender,
                age,
                height,
                weight,
                goal,
                goalWeight,
                frequency,
                dayStart,
                woTime,
            )
            emit(Resource.Success(response))

        } catch (e: Exception) {
            Log.d("get_user_data", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }


    fun getUserHistory(authToken: String, dateFrom: String, dateTo: String): LiveData<Resource<GetUserHistoryResp>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.getUserHistory("Bearer $authToken", dateFrom, dateTo)
            if (response.success == true) {
                response.history?.forEach { historyEntry ->
                    userPreferences.saveUserHistory(
                        UserHistory(
                            historyEntry.id,
                            historyEntry.userId,
                            historyEntry.type,
                            historyEntry.time,
                            historyEntry.calories,
                            historyEntry.date
                        )
                    )
                }
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Log.d("get_user_data", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun inputUserHistory(authToken: String, type: String, time: Int, reps: Int): LiveData<Resource<InputUserResp>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.inputUserHistory("Bearer $authToken", type, time, reps)
            emit(Resource.Success(response))

        } catch (e: Exception) {
            Log.d("get_user_data", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }

    fun getUserReps(authToken: String, currentDate: String): LiveData<Resource<GetUserRepsResp>> = liveData{
        emit(Resource.Loading)
        try {

            val response = apiService.getUserReps("Bearer $authToken", currentDate)
            if (response.success == true) {
                val repsList = response.reps
                if (repsList != null && repsList.isNotEmpty()) {
                    for (reps in repsList) {
                        userPreferences.saveUserReps(
                            UserReps(
                                reps.id,
                                reps.userId,
                                reps.type,
                                reps.reps,
                                reps.sets,
                                reps.date,
                                reps.start,
                                reps.end,
                                reps.createdAt,
                                reps.updatedAt,
                            )
                        )
                    }
                }
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message ?: "Unknown error"))
            }

        } catch (e: Exception) {
            Log.d("get_user_reps", e.message.toString())
            emit(Resource.Error(e.message.toString()))
        }
    }


}