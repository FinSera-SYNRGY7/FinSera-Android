package com.finsera.data.source.remote.response.login


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: LoginData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)