package com.finsera.data.utils

import com.finsera.data.source.remote.response.detail_user.DetailUserResponse
import com.finsera.data.source.remote.response.error.AuthErrorResponse
import com.finsera.data.source.remote.response.info_saldo.InfoSaldoResponse
import com.finsera.data.source.remote.response.login.LoginResponse
import com.finsera.data.source.remote.response.relogin.ReloginResponse
import com.finsera.data.source.remote.response.transfer_sesama_bank.CekRekeningResponse
import com.finsera.data.source.remote.response.transfer_sesama_bank.TransferSesamaResponse
import com.finsera.domain.model.CekRekening
import com.finsera.domain.model.DetailUser
import com.finsera.domain.model.Login
import com.finsera.domain.model.Relogin
import com.finsera.domain.model.Saldo
import com.finsera.domain.model.TransferSesama

object DataMapper {
    fun loginDataToDomain(response: LoginResponse): Login {
        return Login(
            code = response.code,
            message = response.message,
            accessToken = response.data.accessToken,
            refreshToken = response.data.refreshToken,
            status = response.data.status,
            userId = response.data.userId
        )
    }

    fun reloginResponseToDomain(response: ReloginResponse) : Relogin {
        return Relogin(
            data = response.data,
            message = response.message
        )
    }

    fun infoSaldoResponseToDomain(response: InfoSaldoResponse): Saldo {
        return Saldo(
            amount = response.data.amount.amount,
            currency = response.data.amount.currency,
            customerId = response.data.customerId,
            accountNumber = response.data.accountNumber,
            username = response.data.username
        )
    }
//
//    fun reloginResponseToDomain(response: ReloginResponse): Relogin {
//        return Relogin(
//            username = response.data.username,
//            status = response.data.status
//        )
//    }
//
//    fun cekRekeningResponseToDomain(response: CekRekeningResponse): CekRekening {
//        return CekRekening(
//            note = response.data.note,
//            nominal = response.data.nominal,
//            transactionNum = response.data.transactionNum,
//            accountnumRecipient = response.data.accountnumRecipient
//        )
//    }
//
//    fun transferSesamaResponseToDomain(response: TransferSesamaResponse): TransferSesama {
//        return TransferSesama(
//            transactionDate = response.data.transactionDate,
//            note = response.data.note,
//            nominal = response.data.nominal,
//            nameRecipient = response.data.nameRecipient,
//            transactionNum = response.data.transactionNum,
//            accountnumRecipient = response.data.accountnumRecipient,
//            nameSender = response.data.nameSender,
//            accountnumSender = response.data.accountnumSender
//        )
//    }
//
//    fun detailUserToDomain(response: DetailUserResponse): DetailUser {
//        return DetailUser(
//            name = response.customer.name,
//            statusUser = response.customer.statusUser,
//            phoneNumber = response.customer.phoneNumber,
//            id = response.customer.id,
//            mpin = response.customer.mpin,
//            username = response.customer.username,
//            income = response.customer.income,
//            nik = response.customer.nik,
//            address = response.customer.address,
//            gender = response.customer.gender,
//            fatherName = response.customer.fatherName,
//            motherName = response.customer.motherName
//        )
//    }
//
//    fun authErrorToDomain(response: AuthErrorResponse): Error {
//        return Error(
//            message = response.message
//        )
//    }
//
//    fun otherErrorToDomain(response: AuthErrorResponse): Error {
//        return Error(
//            message = response.message
//        )
//    }


}