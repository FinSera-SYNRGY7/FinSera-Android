package com.finsera.domain.model

data class Login(
    val token: String,
    val username: String,
    val status: String
)
