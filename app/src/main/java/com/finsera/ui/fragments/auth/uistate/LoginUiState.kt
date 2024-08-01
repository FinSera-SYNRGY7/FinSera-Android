package com.finsera.ui.fragments.auth.uistate

data class LoginUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isUserLoggedIn: Boolean = false
)