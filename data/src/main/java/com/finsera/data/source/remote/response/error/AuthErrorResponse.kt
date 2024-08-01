package com.finsera.data.source.remote.response.error

import com.google.gson.annotations.SerializedName

data class AuthErrorResponse(

	@field:SerializedName("data")
	val data: Any,

	@field:SerializedName("message")
	val message: String
)
