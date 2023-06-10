package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class GetTypeResp(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: Type? = null,
)

data class Type(
    @field:SerializedName("type")
    val type: String? = null,
)
