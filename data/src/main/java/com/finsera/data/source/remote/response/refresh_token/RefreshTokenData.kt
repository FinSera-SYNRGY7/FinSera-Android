package com.finsera.data.source.remote.response.refresh_token


import com.google.gson.annotations.SerializedName

data class RefreshTokenData(
    @SerializedName("accessToken")
    val accessToken: String
)