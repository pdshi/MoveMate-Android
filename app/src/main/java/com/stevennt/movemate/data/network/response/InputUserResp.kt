package com.stevennt.movemate.data.network.response

import com.google.gson.annotations.SerializedName

data class InputUserResp(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,
)


