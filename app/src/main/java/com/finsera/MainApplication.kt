package com.finsera

import android.app.Application
import com.finsera.common.utils.sharedpref.SharedPreferenceManager
import com.finsera.di.AppModule.appModule
import com.finsera.di.AppModule.repositoryModule
import com.finsera.di.AppModule.useCaseModule
import com.finsera.di.AppModule.viewModelModule
import com.finsera.di.NetworkModule.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule, networkModule, repositoryModule, viewModelModule, useCaseModule)
        }
    }
}