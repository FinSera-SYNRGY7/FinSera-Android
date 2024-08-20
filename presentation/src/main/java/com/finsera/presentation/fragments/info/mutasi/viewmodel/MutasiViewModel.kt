package com.finsera.presentation.fragments.info.mutasi.viewmodel

import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsera.common.utils.Resource
import com.finsera.common.utils.network.ConnectivityManager
import com.finsera.domain.usecase.auth.GetUserInfoUseCase
import com.finsera.domain.usecase.mutasi.DownloadMutasiUseCase
import com.finsera.domain.usecase.mutasi.MutasiUseCase
import com.finsera.presentation.fragments.info.mutasi.uistate.MutasiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class MutasiViewModel(
    private val connectivityManager: ConnectivityManager,
    private val mutasiUseCase: MutasiUseCase,
    private val downloadMutasiFileUseCase: DownloadMutasiUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _mutasiUiState = MutableStateFlow(MutasiUiState())
    val mutasiUiState = _mutasiUiState.asStateFlow()

    private var _userInfo : Pair<String, String>? = null
    val userInfo : Pair<String, String>?
        get() = _userInfo

    init {
        getUserInfo()
    }

    fun getMutasi(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            if(connectivityManager.hasInternetConnection()) {
                mutasiUseCase.invoke(startDate, endDate).collectLatest { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _mutasiUiState.update { uiState ->
                                uiState.copy(isLoading = true, message = null, mutasi = emptyList())
                            }
                        }

                        is Resource.Success -> {
                            _mutasiUiState.update { uiState ->
                                uiState.copy(isLoading = false, message = "Riwayat transaksi berhasil dimuat", mutasi = result.data ?: emptyList())
                            }
                        }

                        is Resource.Error -> {
                            _mutasiUiState.update { uiState ->
                                uiState.copy(isLoading = false, message = result.message, mutasi = emptyList())
                            }
                        }
                    }
                }
            } else {
                _mutasiUiState.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        mutasi = emptyList(),
                        message = "Tidak ada koneksi Internet."
                    )
                }
            }

        }
    }

    private val _downloadState = MutableLiveData<Resource<ResponseBody>>()
    val downloadState: LiveData<Resource<ResponseBody>> = _downloadState

    fun downloadMutasiFile(startDate: String, endDate: String) {
        viewModelScope.launch {
            _downloadState.postValue(Resource.Loading())
            try {
                downloadMutasiFileUseCase(startDate, endDate).collect { resource ->
                    _downloadState.postValue(resource)
                }
            } catch (t: Throwable) {
                _downloadState.postValue(Resource.Error("An unexpected error occurred"))
            }
        }
    }

    fun dataSubmittedToAdapter() {
        _mutasiUiState.update { currentUiState ->
            currentUiState.copy(mutasi = emptyList())
        }
    }

    fun messageShown() {
        _mutasiUiState.update { currentUiState ->
            currentUiState.copy(message = null)
        }
    }

    private fun getUserInfo() {
        _userInfo = getUserInfoUseCase.invoke()
    }
}