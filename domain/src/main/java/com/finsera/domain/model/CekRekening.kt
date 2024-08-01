package com.finsera.domain.model

data class CekRekening(
    val note: String,
    val nominal: String,
    val transactionNum: Long,
    val accountnumRecipient: String
)
