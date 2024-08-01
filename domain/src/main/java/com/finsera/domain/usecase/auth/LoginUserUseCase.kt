package com.finsera.domain.usecase.auth

import com.finsera.common.utils.Resource
import com.finsera.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class LoginUserUseCase(private val repository: IAuthRepository) {
    suspend operator fun invoke(username: String, password: String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.login(username, password)

            when(response.status) {
                "ACTIVE" -> {
                    repository.setLoginStatus(true)
                    emit(Resource.Success("Login Berhasil"))
                }
                "INACTIVE" -> {
                    repository.setLoginStatus(false)
                    emit(Resource.Error("Akun anda nonaktif / dinonaktifkan. Silahkan hubungi Pusat Bantuan atau kunjungi kantor Bank BCA terdekat."))
                }
            }
        } catch (t: Throwable) {
            when (t) {
                is HttpException -> {
                    val response =
                        t.response()?.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response != null) {
                        try {
                            val jsonObject = JSONObject(response)
                            val error = jsonObject.getString("message")

                            if (error == "username or password invalid") {
                                emit(Resource.Error("Username atau Password Anda Salah"))
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