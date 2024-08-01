package com.finsera.data.source.remote.response.detail_user

import com.google.gson.annotations.SerializedName

data class DetailUserResponse(

	@field:SerializedName("customer")
	val customer: Customer
)

data class Customer(

	@field:SerializedName("income")
	val income: Int,

	@field:SerializedName("nik")
	val nik: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("father_name")
	val fatherName: String,

	@field:SerializedName("mother_name")
	val motherName: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("status_user")
	val statusUser: String,

	@field:SerializedName("phone_number")
	val phoneNumber: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("mpin")
	val mpin: String,

	@field:SerializedName("username")
	val username: String
)
