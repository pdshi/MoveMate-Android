package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class GetUserHistoryResp(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val history: List<History>? = null,
)

data class History(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("time")
    val time: Double? = null,

    @SerializedName("calories")
    val calories: Double? = null,

    @SerializedName("date")
    val date: String? = null
)
