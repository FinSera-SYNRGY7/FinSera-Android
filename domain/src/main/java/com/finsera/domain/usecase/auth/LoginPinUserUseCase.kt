package com.finsera.domain.usecase.auth

import com.finsera.common.utils.Resource
import com.finsera.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class LoginPinUserUseCase(private val repository: IAuthRepository) {
    suspend operator fun invoke(mpin: String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            repository.relogin(mpin)
            val getRefreshToken = repository.getRefreshToken()
            repository.refreshAccessToken(getRefreshToken) // refresh access token if login pin success
            emit(Resource.Success("Berhasil Login"))
        } catch (t: Throwable) {
            when (t) {
                is HttpException -> {
                    val response =
                        t.response()?.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response != null) {
                        try {
                            val jsonObject = JSONObject(response)
                            val error = jsonObject.getString("message")

                            if (error == "Pin is invalid") {
                                emit(Resource.Error("PIN Anda invalid. Silahkan coba lagi."))
                            } else {
                                emit(Resource.Error("Kesalahan pada server. Silahkan coba beberapa saat lagi."))
                            }
                        } catch (e: Exception) {
                            when(e) {
                                is JSONException -> {
                                    emit(Resource.Error("Kesalahan pada server. Silahkan coba beberapa saat lagi."))
                                }
                            }
                        }
                    } else {
                        emit(Resource.Error("Kesalahan pada server. Silahkan coba beberapa saat lagi."))
                    }
                }

                is IOException -> {
                    emit(Resource.Error("Ada masalah pada koneksi internet anda. Silahkan coba lagi."))
                }

                is JSONException -> {
                    emit(Resource.Error("Kesalahan pada server. Silahkan coba beberapa saat lagi."))
                }
            }
        }
    }
}