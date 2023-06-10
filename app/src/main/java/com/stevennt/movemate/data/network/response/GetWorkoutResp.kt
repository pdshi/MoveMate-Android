package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class GetWorkoutResp (
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: Workout? = null,
)

data class Workout(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
    @SerializedName("ceil") val ceil: Int,
    @SerializedName("floor") val floor: Int,
    @SerializedName("duration") val duration: Int,
    @SerializedName("calories_per_reps") val caloriesPerReps: Double,
    @SerializedName("bicep") val bicep: Boolean,
    @SerializedName("tricep") val tricep: Boolean,
    @SerializedName("shoulder") val shoulder: Boolean,
    @SerializedName("chest") val chest: Boolean,
    @SerializedName("abs") val abs: Boolean,
    @SerializedName("thigh") val thigh: Boolean,
    @SerializedName("butt") val butt: Boolean,
    @SerializedName("leg") val leg: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("createdAt") val createdAtFormatted: String,
    @SerializedName("updatedAt") val updatedAtFormatted: String
)