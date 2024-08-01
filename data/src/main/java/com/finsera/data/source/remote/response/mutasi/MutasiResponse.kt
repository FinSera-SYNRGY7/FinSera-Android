package com.finsera.data.source.remote.response.mutasi


import com.google.gson.annotations.SerializedName

data class MutasiResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<MutasiData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)