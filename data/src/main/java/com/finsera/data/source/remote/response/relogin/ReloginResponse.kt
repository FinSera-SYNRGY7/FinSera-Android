package com.finsera.data.source.remote.response.relogin


import com.google.gson.annotations.SerializedName

data class ReloginResponse(
    @SerializedName("data")
    val data: String,
    @SerializedName("message")
    val message: String
)