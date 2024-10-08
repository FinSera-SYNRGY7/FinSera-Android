package com.finsera.data.source.local.db.daftar_tersimpan

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finsera.data.source.local.dao.daftar_tersimpan.ewallet.TransferEWalletTersimpanDao
import com.finsera.data.source.local.dao.daftar_tersimpan.transfer_antar.TransferAntarTersimpanDao
import com.finsera.data.source.local.dao.daftar_tersimpan.transfer_sesama.TransferSesamaTersimpanDao
import com.finsera.data.source.local.dao.daftar_tersimpan.virtual_account.TransferVaTersimpanDao
import com.finsera.data.source.local.entities.daftar_tersimpan.ewallet.TransferEWalletTersimpanEntity
import com.finsera.data.source.local.entities.daftar_tersimpan.transfer_antar.TransferAntarTersimpanEntity
import com.finsera.data.source.local.entities.daftar_tersimpan.transfer_sesama.TransferSesamaTersimpanEntity
import com.finsera.data.source.local.entities.daftar_tersimpan.virtual_account.TransferVaTersimpanEntity

@Database(
    entities = [TransferSesamaTersimpanEntity::class, TransferAntarTersimpanEntity::class, TransferVaTersimpanEntity::class, TransferEWalletTersimpanEntity::class],
    version = 1,
    exportSchema = false)
public abstract class DaftarTersimpanDatabase : RoomDatabase() {
    abstract fun transferSesamaTersimpanDao() : TransferSesamaTersimpanDao
    abstract fun transferAntarTersimpanDao() : TransferAntarTersimpanDao
    abstract fun transferVaTersimpanDao() : TransferVaTersimpanDao
    abstract fun transferEWalletTersimpanDao() : TransferEWalletTersimpanDao
}