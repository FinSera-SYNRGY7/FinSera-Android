package com.finsera.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.finsera.common.utils.Constant.Companion.BASE_URL
import com.finsera.common.utils.network.AuthInterceptor
import com.finsera.data.source.remote.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    val networkModule = module {
        single { AuthInterceptor() }
        single { ChuckerInterceptor(get()) }
        single { provideInterceptor() }
        factory { provideOkHttpClient(get(), get(), get()) }
        single { provideRetrofit(get()) }
        single { provideRetrofitApi(get()) }
    }

    private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun provideOkHttpClient(authInterceptor: AuthInterceptor, httpLoggingInterceptor: HttpLoggingInterceptor, chuckerInterceptor: ChuckerInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(chuckerInterceptor)
            .build()
    }

    private fun provideInterceptor() : HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun provideRetrofitApi(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }
}