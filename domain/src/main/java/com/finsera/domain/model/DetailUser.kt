package com.finsera.domain.model

data class DetailUser(
    val id: Int,
    val mpin: String,
    val username: String,
    val income: Int,
    val nik: String,
    val address: String,
    val gender: String,
    val fatherName: String,
    val motherName: String,
    val name: String,
    val statusUser: String,
    val phoneNumber: String
)
