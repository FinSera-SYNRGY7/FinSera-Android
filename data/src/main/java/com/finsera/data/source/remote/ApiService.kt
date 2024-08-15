package com.finsera.data.source.remote

import com.finsera.data.source.remote.response.cek_rekening_sesama_bank.CekRekeningResponse
import com.finsera.data.source.remote.response.info_saldo.InfoSaldoResponse
import com.finsera.data.source.remote.response.list_bank.ListBankResponse
import com.finsera.data.source.remote.response.login.LoginResponse
import com.finsera.data.source.remote.response.mutasi.MutasiResponse
import com.finsera.data.source.remote.response.refresh_token.RefreshTokenResponse
import com.finsera.data.source.remote.response.relogin.ReloginResponse
import com.finsera.data.source.remote.response.transfer_sesama_bank.TransferSesamaResponse
import com.finsera.data.source.remote.response.virtual_account.CheckVaResponse
import com.finsera.data.source.remote.response.virtual_account.TransferVaResponse
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/user/login")
    suspend fun loginUser(
        @Body raw: JsonObject
    ): LoginResponse

    @POST("auth/relogin")
    suspend fun reloginUser(
        @Header("Authorization") accessToken: String,
        @Body raw: JsonObject
    ): ReloginResponse

    @POST("auth/user/refresh-token")
    suspend fun refreshToken(
        @Body raw: JsonObject
    ) : RefreshTokenResponse

    @GET("amount")
    suspend fun getSaldo(
        @Header("Authorization") accessToken: String
    ): InfoSaldoResponse

    @GET("mutasi")
    suspend fun getMutasi(
        @Header("Authorization") accessToken: String,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
    ): MutasiResponse

    @POST("transaction/transaction-intra/check")
    suspend fun cekRekeningSesamaBank(
        @Header("Authorization") accessToken: String,
        @Body raw: JsonObject
    ) : CekRekeningResponse

    @POST("transaction/transaction-intra/create")
    suspend fun transferSesamaBank(
        @Header("Authorization") accessToken: String,
        @Body raw: JsonObject
    ) : TransferSesamaResponse

    @POST("va/check-virtual-account")
    suspend fun cekVirtualAccount(
        @Header("Authorization") accessToken: String,
        @Body raw: JsonObject
    ) : CheckVaResponse

    @POST("va/transfer-va")
    suspend fun transferVirtualAccount(
        @Header("Authorization") accessToken: String,
        @Body raw: JsonObject
    ) : TransferVaResponse

    @GET("mutasi/download")
    suspend fun getDownloadMutasi(
        @Header("Authorization") accessToken: String,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
    ): ResponseBody

    @GET("bank/get-all")
    suspend fun getListBank(
        @Header("Authorization") accessToken: String
    ) : ListBankResponse
}