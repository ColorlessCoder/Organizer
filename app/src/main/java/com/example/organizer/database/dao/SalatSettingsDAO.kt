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

    @Query("Delete from user_settings where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From user_settings where active = 1")
    fun getActiveUserSettings(): LiveData<SalatSettings>

    @Query("Select * From user_settings where id = :id")
    suspend fun getById(id: String): SalatSettings
}