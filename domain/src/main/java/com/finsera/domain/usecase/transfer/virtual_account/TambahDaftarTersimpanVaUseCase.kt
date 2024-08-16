package com.finsera.domain.usecase.transfer.virtual_account

import com.finsera.domain.model.DaftarTersimpan
import com.finsera.domain.repository.IDaftarTersimpanRepository

class TambahDaftarTersimpanVaUseCase(
    private val daftarTersimpanRepository: IDaftarTersimpanRepository
) {
    suspend operator fun invoke(daftarTersimpan: DaftarTersimpan) {
        daftarTersimpanRepository.insertDaftarTersimpanVa(daftarTersimpan)
    }
}