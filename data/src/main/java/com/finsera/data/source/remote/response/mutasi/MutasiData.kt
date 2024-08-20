package com.finsera.data.source.remote.response.mutasi


import com.google.gson.annotations.SerializedName

data class MutasiData(
    @SerializedName("amountTransfer")
    val amountTransfer: MutasiAmount,
    @SerializedName("destinationNameAccountNumber")
    val destinationNameAccountNumber: String,
    @SerializedName("noTransaction")
    val noTransaction: String,
    @SerializedName("transactionDate")
    val transactionDate: String,
    @SerializedName("transactionId")
    val transactionId: Int,
    @SerializedName("transactionInformation")
    val transactionInformation: String,
    @SerializedName("transactionsType")
    val transactionsType: String,
    @SerializedName("destinationBankName")
    val destinationBankName: String
)