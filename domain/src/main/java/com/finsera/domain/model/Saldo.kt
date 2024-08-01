package com.finsera.domain.model

data class Saldo(
    val amount: Double,
    val currency: String,
    val customerId: Int,
    val accountNumber: String,
    val username: String
)
