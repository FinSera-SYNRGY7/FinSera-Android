package com.finsera.data.source.remote.response.login


import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("userId")
    val userId: Int?
)