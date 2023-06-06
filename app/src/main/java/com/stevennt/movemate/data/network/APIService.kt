package com.stevennt.movemate.data.network

import com.stevennt.movemate.data.network.response.FirebaseResponse
import com.stevennt.movemate.data.network.response.LoginResp
import com.stevennt.movemate.data.network.response.RegisterResp
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResp

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("key") key: String
    ) : RegisterResp

    @POST("auth/firebase")
    suspend fun firebase(
        @Header("authorization") authorization: String,
    ) : FirebaseResponse

}