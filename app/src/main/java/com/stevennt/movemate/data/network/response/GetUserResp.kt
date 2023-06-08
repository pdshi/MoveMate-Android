package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class GetUserResp (

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: Data? = null,

)

data class Data(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("display_name")
    val displayName: String? = null,

    @field:SerializedName("photo_url")
    val photoUrl: String? = null,

    @field:SerializedName("gender")
    val gender: String? = null,

    @field:SerializedName("age")
    val age: Int? = null,

    @field:SerializedName("height")
    val height: String? = null,

    @field:SerializedName("weight")
    val weight: String? = null,

    @field:SerializedName("bmi")
    val bmi: Double? = null,

    @field:SerializedName("bmi_status")
    val bmiStatus: String? = null,

    @field:SerializedName("goal")
    val goal: String? = null,

    @field:SerializedName("goal_weight")
    val goalWeight: String? = null,

    @field:SerializedName("frequency")
    val frequency: Int? = null,

    @field:SerializedName("day_start")
    val dayStart: String? = null,

    @field:SerializedName("wo_time")
    val woTime: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

)


