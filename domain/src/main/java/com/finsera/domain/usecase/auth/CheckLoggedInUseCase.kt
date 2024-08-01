package com.finsera.domain.usecase.auth

import com.finsera.domain.repository.IAuthRepository

class CheckLoggedInUseCase(private val repository: IAuthRepository) {
    operator fun invoke() : Boolean {
        return repository.getLoginStatus()
    }
}