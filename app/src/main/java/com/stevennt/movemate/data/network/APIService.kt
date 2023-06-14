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
        @Header("Authorization") authorization: String,
    ) : FirebaseResponse

    @FormUrlEncoded
    @POST("userdata/input")
    suspend fun inputUserData(
        @Header("Authorization") authorization: String,
        @Field("display_name") displayName: String?,
        @Field("photo_url") photoUrl: String?,
        @Field("gender") gender: String?,
        @Field("age") age: String?,
        @Field("height") height: String?,
        @Field("weight") weight: String?,
        @Field("goal") goal: String?,
        @Field("goal_weight") goalWeight: String?,
        @Field("frequency") frequency: String?,
        @Field("day_start") dayStart: String?,
        @Field("wo_time") woTime: String?,
    ): InputUserDataResp

    @GET("userdata/get")
    suspend fun getUserData(
        @Header("Authorization") authorization: String,
    ) : GetUserResp

    @FormUrlEncoded
    @POST("userhistory/input")
    suspend fun inputUserHistory(
        @Header("Authorization") authorization: String,
        @Field("type") type: String,
        @Field("time") time: Int,
        @Field("reps") reps: Int
    ): InputUserResp

    @FormUrlEncoded
    @POST("userhistory/get")
    suspend fun getUserHistory(
        @Header("Authorization") authorization: String,
        @Field("date_from") dateFrom: String,
        @Field("date_to") dateTo: String,
    ) : GetUserHistoryResp

    @FormUrlEncoded
    @POST("userreps/input")
    suspend fun inputUserReps(
        @Header("Authorization") authorization: String,
        @Field("type") type: String,
        @Field("reps") reps: String,
        @Field("sets") sets: String,
        @Field("date") date: String,
        @Field("start") start: String,
        @Field("end") end: String,
    ): InputUserResp

    @FormUrlEncoded
    @POST("userreps/get")
    suspend fun getUserReps(
        @Header("Authorization") authorization: String,
        @Field("current_date") currentDate: String,
    ): GetUserRepsResp

    @FormUrlEncoded
    @POST("workout/input")
    suspend fun inputWorkout(
        @Header("Authorization") authorization: String,
        @Field("type") type: String,
        @Field("ceil") ceil: Int,
        @Field("floor") floor: Int,
        @Field("duration") duration: Int,
        @Field("calories_per_reps") caloriesPerReps: Double,
        @Field("bicep") bicep: Boolean,
        @Field("tricep") tricep: Boolean,
        @Field("shoulder") shoulder: Boolean,
        @Field("chest") chest: Boolean,
        @Field("abs") abs: Boolean,
        @Field("thigh") thigh: Boolean,
        @Field("butt") butt: Boolean,
        @Field("leg") leg: Boolean
    )

    @FormUrlEncoded
    @POST("workout/get")
    suspend fun getWorkout(
        @Header("Authorization") authorization: String,
        @Field("type") type: String,
    ) : GetWorkoutResp

    @GET("workout/getalltype")
    suspend fun getAllType(
        @Header("Authorization") authorization: String,
    ) : GetTypeResp

}