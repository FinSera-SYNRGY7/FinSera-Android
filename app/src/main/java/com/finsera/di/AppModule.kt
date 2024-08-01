package com.finsera.di

import com.finsera.common.utils.sharedpref.SharedPreferenceManager
import com.finsera.data.implementation.auth.AuthRepositoryImpl
import com.finsera.data.implementation.infosaldo.SaldoRepositoryImpl
import com.finsera.data.source.local.LocalDataSource
import com.finsera.data.source.remote.RemoteDataSource
import com.finsera.domain.repository.IAuthRepository
import com.finsera.domain.repository.ISaldoRepository
import com.finsera.domain.usecase.auth.CheckLoggedInUseCase
import com.finsera.domain.usecase.auth.LoginPinUserUseCase
import com.finsera.domain.usecase.auth.LoginUserUseCase
import com.finsera.domain.usecase.infosaldo.InfoSaldoUseCase
import com.finsera.ui.fragments.auth.viewmodels.LoginPinViewModel
import com.finsera.ui.fragments.auth.viewmodels.LoginViewModel
import com.finsera.ui.fragments.home.viewmodel.HomeViewModel
import com.finsera.ui.fragments.info.saldo.viewmodel.InfoSaldoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {
    val appModule = module {
        single { LocalDataSource(get()) }
        single { RemoteDataSource(get()) }
        single {
            SharedPreferenceManager.init(get())
            SharedPreferenceManager
        }
    }

    val repositoryModule = module {
        factory<IAuthRepository> { AuthRepositoryImpl(get(), get()) }
        factory<ISaldoRepository> { SaldoRepositoryImpl(get(), get())}
    }

    val viewModelModule = module {
        viewModel { LoginViewModel(get(), get(), get()) }
        viewModel { LoginPinViewModel(get(), get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { InfoSaldoViewModel(get()) }
    }

    val useCaseModule = module {
        factory { LoginUserUseCase(get()) }
        factory { CheckLoggedInUseCase(get()) }
        factory { LoginPinUserUseCase(get()) }
        factory { InfoSaldoUseCase(get()) }
    }
}