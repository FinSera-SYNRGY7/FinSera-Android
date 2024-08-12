package com.finsera.presentation.fragments.auth.uistate

data class LoginPinUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isPinCorrect: Boolean = false
)