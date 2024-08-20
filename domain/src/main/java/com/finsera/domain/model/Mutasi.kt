package com.finsera.domain.model

import com.google.gson.annotations.SerializedName

data class Mutasi(
    val amount: Double,
    val destinationName: String,
    val noTransaction: String,
    val transactionDate: String,
    val transactionInformation: String,
    val transactionType: String,
    val destinationBankName: String
)
