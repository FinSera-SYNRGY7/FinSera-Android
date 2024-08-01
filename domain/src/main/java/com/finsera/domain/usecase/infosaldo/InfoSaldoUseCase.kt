package com.finsera.domain.usecase.infosaldo

import com.finsera.common.utils.Resource
import com.finsera.domain.model.Saldo
import com.finsera.domain.repository.ISaldoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InfoSaldoUseCase (private val repository: ISaldoRepository) {
    suspend fun invoke(): Flow<Resource<Saldo>> = flow {
        emit(Resource.Loading())
        try {
            val saldo = repository.getSaldo()
            emit(Resource.Success(saldo))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}