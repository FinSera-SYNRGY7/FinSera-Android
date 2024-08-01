package com.finsera.domain.repository

import com.finsera.domain.model.CekRekening
import com.finsera.domain.model.TransferSesama

interface ITransferRepository {

    suspend fun cekDataRekening(
        token: String,
        accountNumRecipient: String,
        nominal: Double,
        note: String
    ): CekRekening
    suspend fun transferSesamaBank(
        token: String,
        idUser: Int,
        accountNumRecipient: String,
        nominal: Double,
        note: String,
        pin: String
    ): TransferSesama

}