package com.finsera.data.source.remote.response.mutasi


import com.google.gson.annotations.SerializedName

data class MutasiData(
    @SerializedName("accountNumber")
    val accountNumber: String,
    @SerializedName("amountTransfer")
    val amountTransfer: AmountTransfer,
    @SerializedName("noTransaction")
    val noTransaction: String,
    @SerializedName("transactionDate")
    val transactionDate: String,
    @SerializedName("transactionId")
    val transactionId: Int,
    @SerializedName("transactionInformation")
    val transactionInformation: String
)