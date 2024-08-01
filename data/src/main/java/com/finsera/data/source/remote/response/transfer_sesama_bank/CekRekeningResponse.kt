package com.finsera.data.source.remote.response.transfer_sesama_bank

import com.google.gson.annotations.SerializedName

data class CekRekeningResponse(

	@field:SerializedName("data")
	val data: DataCekRekening,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)

data class DataCekRekening(

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("nominal")
	val nominal: String,

	@field:SerializedName("transaction_num")
	val transactionNum: Long,

	@field:SerializedName("accountnum_recipient")
	val accountnumRecipient: String
)
