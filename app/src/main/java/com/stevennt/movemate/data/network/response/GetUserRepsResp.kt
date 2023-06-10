package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class GetUserRepsResp(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val reps: List<Reps>? = null,
)

data class Reps(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("reps")
    val reps: Int? = null,

    @field:SerializedName("sets")
    val sets: Int? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("start")
    val start: String? = null,

    @field:SerializedName("end")
    val end: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null
)
