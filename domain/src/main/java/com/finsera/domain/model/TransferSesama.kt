package com.finsera.domain.model

data class TransferSesama(
    val transactionDate: String,
    val note: String,
    val nominal: String,
    val nameRecipient: String,
    val transactionNum: Long,
    val accountnumRecipient: String,
    val nameSender: String,
    val accountnumSender: String
)


