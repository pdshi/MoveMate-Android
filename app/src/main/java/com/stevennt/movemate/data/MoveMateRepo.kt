package com.stevennt.movemate.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.stevennt.movemate.data.network.APIService
import com.stevennt.movemate.data.network.UserSession
import com.stevennt.movemate.data.network.response.FirebaseResponse
import com.stevennt.movemate.data.network.response.LoginResp
import com.stevennt.movemate.data.network.response.RegisterResp
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
            val response = apiService.firebase(authToken)

            if(response.success == true) {
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

}