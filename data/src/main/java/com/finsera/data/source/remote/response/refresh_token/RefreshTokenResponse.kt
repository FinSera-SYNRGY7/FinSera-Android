package com.finsera.data.source.remote.response.refresh_token


import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: RefreshTokenData,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)