package com.finsera.data.source.remote.response.transfer_sesama_bank

import com.google.gson.annotations.SerializedName

data class TransferSesamaResponse(

	@field:SerializedName("data")
	val data: DataTransfer,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: Int
)

data class DataTransfer(

	@field:SerializedName("transaction_date")
	val transactionDate: String,

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("nominal")
	val nominal: String,

	@field:SerializedName("name_recipient")
	val nameRecipient: String,

	@field:SerializedName("transaction_num")
	val transactionNum: Long,

	@field:SerializedName("accountnum_recipient")
	val accountnumRecipient: String,

	@field:SerializedName("name_sender")
	val nameSender: String,

	@field:SerializedName("accountnum_sender")
	val accountnumSender: String
)
