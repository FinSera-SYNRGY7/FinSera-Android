package com.finsera.presentation.fragments.info.saldo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsera.common.utils.Resource
import com.finsera.common.utils.network.ConnectivityManager
import com.finsera.domain.model.Saldo
import com.finsera.domain.usecase.infosaldo.InfoSaldoUseCase
import com.finsera.presentation.fragments.home.uistate.SaldoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InfoSaldoViewModel(private val connectivityManager: ConnectivityManager, private val infoSaldoUseCase: InfoSaldoUseCase) : ViewModel() {
    private val _saldoUIState = MutableStateFlow(SaldoUiState())
    val saldoUiState = _saldoUIState.asStateFlow()



    init {
        getSaldo()
    }

    private fun getSaldo(){
        viewModelScope.launch {
            if(connectivityManager.hasInternetConnection()){
                infoSaldoUseCase.invoke().collect {
                    val currentState= _saldoUIState.value
                    when(it) {
                        is Resource.Loading -> {
                            _saldoUIState.value =
                                currentState.copy(isLoading = true, data = null, message = null)
                        }

                        is Resource.Success -> {
                            _saldoUIState.value =
                                currentState.copy(isLoading = false, data = it.data, message = null)
                        }

                        is Resource.Error -> {
                            _saldoUIState.value =
                                currentState.copy(isLoading = true, data = null, message = it.message)
                        }
                    }
                }
            }else{
                _saldoUIState.value = _saldoUIState.value.copy(message = "No Internet Connection", isLoading = true)
            }

        }
    }
}