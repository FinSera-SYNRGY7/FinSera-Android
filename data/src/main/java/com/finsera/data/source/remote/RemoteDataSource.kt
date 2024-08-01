package com.finsera.data.source.remote

import android.util.Log
import com.finsera.common.utils.Resource
import com.finsera.data.source.remote.response.info_saldo.InfoSaldoResponse
import com.finsera.data.source.remote.response.login.LoginResponse
import com.finsera.data.source.remote.response.refresh_token.RefreshTokenResponse
import com.finsera.data.source.remote.response.relogin.ReloginResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {
    suspend fun authLoginUser(username: String, password: String) : LoginResponse {
        val param = JsonObject().apply {
            addProperty("username", username)
            addProperty("password", password)
        }

        return apiService.loginUser(param)
    }

    suspend fun reloginUser(token: String, mpin: String) : ReloginResponse {
        val param = JsonObject().apply {
            addProperty("mpinAuth", mpin)
        }

        val accessToken = "Bearer $token"

        return apiService.reloginUser(accessToken, param)
    }

    suspend fun refreshAccessToken(token: String) : RefreshTokenResponse {
        val param = JsonObject().apply {
            addProperty("refreshToken", token)
        }

        return apiService.refreshToken(param)
    }

    suspend fun getSaldo(token: String) : InfoSaldoResponse {
        val accessToken = "Bearer $token"
        return apiService.getSaldo(accessToken)
    }
}