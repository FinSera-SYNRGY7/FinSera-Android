package com.finsera.ui.fragments.home.uistate

import com.finsera.domain.model.Saldo

data class SaldoUiState(
    val isLoading: Boolean = false,
    val data: Saldo? = null,
    val message: String? = null
)
