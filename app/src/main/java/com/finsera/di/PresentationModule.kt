package com.finsera.di

import com.finsera.presentation.fragments.auth.viewmodels.LoginPinViewModel
import com.finsera.presentation.fragments.auth.viewmodels.LoginViewModel
import com.finsera.presentation.fragments.home.viewmodel.HomeViewModel
import com.finsera.presentation.fragments.info.mutasi.viewmodel.MutasiViewModel
import com.finsera.presentation.fragments.info.saldo.viewmodel.InfoSaldoViewModel
import com.finsera.presentation.fragments.transfer.antar_bank.viewmodel.CekRekeningAntarViewModel
import com.finsera.presentation.fragments.transfer.sesama_bank.viewmodel.CekRekeningSesamaViewModel
import com.finsera.presentation.fragments.transfer.sesama_bank.viewmodel.TransferSesamaBankFormViewModel
import com.finsera.presentation.fragments.transfer.sesama_bank.viewmodel.TransferSesamaBankViewModel
import com.finsera.presentation.fragments.transfer.va.viewmodel.CheckVaViewModel
import com.finsera.presentation.fragments.transfer.va.viewmodel.TransferVaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object PresentationModule {
    val viewModelModule = module {
        viewModel { LoginViewModel(get(), get(), get(),get()) }
        viewModel { LoginPinViewModel(get(), get(),get()) }
        viewModel { HomeViewModel(get(), get()) }
        viewModel { InfoSaldoViewModel(get(), get()) }
        viewModel { MutasiViewModel(get(), get(), get()) }
        viewModel { TransferSesamaBankFormViewModel(get(), get()) }
        viewModel { TransferSesamaBankViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { CekRekeningSesamaViewModel(get(), get()) }
        viewModel { CheckVaViewModel(get(), get()) }
        viewModel { TransferVaViewModel(get(), get(),get(),get(),get()) }
        viewModel { CekRekeningAntarViewModel(get(), get()) }
    }
}