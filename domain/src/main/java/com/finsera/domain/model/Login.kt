package com.finsera.domain.model

data class Login(
    val code: Int,
    val message: String,
    val accessToken: String?,
    val refreshToken: String?,
    val userId: Int?,
    val status: String?
)
