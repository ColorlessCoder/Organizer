package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.SalatSettings

@Dao
interface SalatSettingsDAO:BaseDAO {
    companion object {
        val DefaultSettingsName = "DEFAULT"
    }
    @Update
    suspend fun update(vararg settings: SalatSettings)

    @Insert
    suspend fun insert(vararg settings: SalatSettings)

    @Query("Delete from salat_settings where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From salat_settings where active = 1")
    suspend fun getActiveSalatSettings(): SalatSettings?

    @Query("Select * From salat_settings where active = 1")
    fun getActiveSalatSettingsLive(): LiveData<SalatSettings>

    @Query("Select * From salat_settings where id = :id")
    suspend fun getById(id: String): SalatSettings
}