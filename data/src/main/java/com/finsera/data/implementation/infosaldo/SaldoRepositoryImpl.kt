package com.finsera.data.implementation.infosaldo

import com.finsera.common.utils.Resource
import com.finsera.data.source.local.LocalDataSource
import com.finsera.data.source.remote.ApiService
import com.finsera.data.source.remote.RemoteDataSource
import com.finsera.data.utils.DataMapper
import com.finsera.domain.model.Saldo
import com.finsera.domain.repository.ISaldoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SaldoRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): ISaldoRepository {
    override suspend fun getSaldo(): Saldo {
        val accessToken = localDataSource.getAccessToken() // get access Token
        val response = remoteDataSource.getSaldo(accessToken)
        return DataMapper.infoSaldoResponseToDomain(response)
    }
}