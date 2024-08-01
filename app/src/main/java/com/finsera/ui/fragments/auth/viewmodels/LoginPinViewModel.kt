package com.finsera.ui.fragments.auth.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.finsera.MainApplication
import com.finsera.common.utils.Resource
import com.finsera.domain.usecase.auth.LoginPinUserUseCase
import com.finsera.ui.fragments.auth.uistate.LoginPinUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginPinViewModel(
    app: Application,
    private val loginPinUserUseCase: LoginPinUserUseCase
) : AndroidViewModel(app) {
    private val _loginPinScreenUIState = MutableStateFlow(LoginPinUiState())
    val loginPinScreenUIState = _loginPinScreenUIState.asStateFlow()

    fun loginWithMpin(mpin: String) {
        viewModelScope.launch {
            if(hasInternetConnection()) {
                loginPinUserUseCase.invoke(mpin).collectLatest { result ->
                    when(result) {
                        is Resource.Loading -> {
                            _loginPinScreenUIState.update { uiState ->
                                uiState.copy(isLoading = true, message = null, isPinCorrect = false)
                            }
                        }

                        is Resource.Success -> {
                            _loginPinScreenUIState.update { uiState ->
                                uiState.copy(isLoading = false, message = result.data, isPinCorrect = true)
                            }
                        }

                        is Resource.Error -> {
                            _loginPinScreenUIState.update { uiState ->
                                uiState.copy(isLoading = false, message = result.message, isPinCorrect = false)
                            }
                        }
                    }
                }
            } else {
                _loginPinScreenUIState.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        message = "Tidak ada Koneksi Internet",
                        isPinCorrect = false
                    )
                }
            }
        }
    }

    fun userMessageShown() {
        _loginPinScreenUIState.update { currentUiState ->
            currentUiState.copy(message = null)
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MainApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }


}