package com.finsera.ui.fragments.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsera.common.utils.Resource
import com.finsera.domain.model.Saldo
import com.finsera.domain.usecase.infosaldo.InfoSaldoUseCase
import com.finsera.ui.fragments.auth.uistate.LoginUiState
import com.finsera.ui.fragments.home.uistate.SaldoUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val infoSaldoUseCase: InfoSaldoUseCase) : ViewModel() {

    private val _saldoUIState = MutableStateFlow(SaldoUiState())
    val saldoUiState = _saldoUIState.asStateFlow()

    private val _isSaldoVisible = MutableLiveData<Boolean>().apply { value = false }
    val isSaldoVisible: LiveData<Boolean> get() = _isSaldoVisible

    init {
        getSaldo()
    }

    private fun getSaldo(){
        viewModelScope.launch {
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
                            currentState.copy(isLoading = false, data = null, message = it.message)
                    }
                }
            }
        }
    }

    fun toggleSaldoVisibility() {
        _isSaldoVisible.value = _isSaldoVisible.value != true
    }
}