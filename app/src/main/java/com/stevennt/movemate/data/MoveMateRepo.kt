package com.stevennt.movemate.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.stevennt.movemate.data.network.APIService
import com.stevennt.movemate.data.model.UserData
import com.stevennt.movemate.data.model.UserHistory
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

    fun getUserHistory(authToken: String, dateFrom: String, dateTo: String): LiveData<Resource<GetUserHistoryResp>> = liveData{
        emit(Resource.Loading)
        try {

            val response = apiService.getUserHistory("Bearer $authToken", dateFrom, dateTo)
            if(response.success == true) {
                userPreferences.saveUserHistory(
                    UserHistory(
                        response.history?.id,
                        response.history?.userId,
                        response.history?.type,
                        response.history?.time,
                        response.history?.calories,
                        response.history?.date,

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

}