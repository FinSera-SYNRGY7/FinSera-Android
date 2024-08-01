package com.finsera.domain.repository

import com.finsera.common.utils.Resource
import com.finsera.domain.model.Saldo
import kotlinx.coroutines.flow.Flow

interface ISaldoRepository {
    suspend fun getSaldo(): Saldo
}