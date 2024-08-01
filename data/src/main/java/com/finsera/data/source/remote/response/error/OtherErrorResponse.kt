package com.finsera.data.source.remote.response.error

import com.google.gson.annotations.SerializedName

data class OtherErrorResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)
