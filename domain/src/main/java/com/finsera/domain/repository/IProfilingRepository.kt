package com.finsera.domain.repository

import com.finsera.domain.model.DetailUser

interface IProfilingRepository {
    suspend fun getDetailUser(token: String): DetailUser
}