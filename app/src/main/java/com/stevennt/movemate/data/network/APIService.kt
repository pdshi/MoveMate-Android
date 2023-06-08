package com.stevennt.movemate.data.network

import com.stevennt.movemate.data.network.response.*
import retrofit2.http.*

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

    @GET("userdata/get")
    suspend fun userdata(
        @Header("authorization") authorization: String,
    ) : GetUserResp

}