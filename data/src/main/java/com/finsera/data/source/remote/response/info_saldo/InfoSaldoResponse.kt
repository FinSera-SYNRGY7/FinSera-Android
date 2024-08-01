package com.finsera.data.source.remote.response.info_saldo

import com.google.gson.annotations.SerializedName

data class InfoSaldoResponse(

	@field:SerializedName("code")
	val code: Int,

	@field:SerializedName("data")
	val data: DataSaldo,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Boolean
)

data class Amount(

	@field:SerializedName("amount")
	val amount: Double,

	@field:SerializedName("currency")
	val currency: String
)

data class DataSaldo(

	@field:SerializedName("amount")
	val amount: Amount,

	@field:SerializedName("customerId")
	val customerId: Int,

	@field:SerializedName("accountNumber")
	val accountNumber: String,

	@field:SerializedName("username")
	val username: String
)

//mapping data class to domain class



