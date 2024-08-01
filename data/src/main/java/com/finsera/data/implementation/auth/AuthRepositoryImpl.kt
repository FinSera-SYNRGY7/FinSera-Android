package com.finsera.data.implementation.auth

import com.finsera.data.source.local.LocalDataSource
import com.finsera.data.source.remote.RemoteDataSource
import com.finsera.data.utils.DataMapper
import com.finsera.domain.model.Login
import com.finsera.domain.model.Relogin
import com.finsera.domain.repository.IAuthRepository

class AuthRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : IAuthRepository {
    override suspend fun login(username: String, password: String): Login {
        val response = remoteDataSource.authLoginUser(username, password)

        response.data.accessToken?.let {
            setAccessToken(it)
        }
        response.data.refreshToken?.let {
            setRefreshToken(it)
        }

        return DataMapper.loginDataToDomain(response)
    }

    override suspend fun relogin(mpin: String): Relogin {
        val accessToken = getAccessToken()
        val response = remoteDataSource.reloginUser(accessToken, mpin)

        return DataMapper.reloginResponseToDomain(response)
    }

    override suspend fun refreshAccessToken(accessToken: String) {
        val getRefreshToken = getRefreshToken()
        val response = remoteDataSource.refreshAccessToken(getRefreshToken)
        response.data?.let {
            setAccessToken(it.accessToken)
        }
    }

    override fun setAccessToken(token: String) {
        localDataSource.setAccessToken(token)
    }

    override fun getAccessToken(): String {
        return localDataSource.getAccessToken()
    }

    override fun setRefreshToken(token: String) {
        localDataSource.setRefreshToken(token)
    }

    override fun getRefreshToken(): String {
        return localDataSource.getRefreshToken()
    }

    override fun setLoginStatus(status: Boolean) {
        localDataSource.setLoginStatus(status)
    }

    override fun getLoginStatus(): Boolean {
        return localDataSource.getLoginStatus()
    }
}