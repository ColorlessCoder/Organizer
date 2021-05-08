package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.UserSettings
import com.example.organizer.database.enums.TransactionType
import java.util.*

@Dao
interface UserSettingsDAO:BaseDAO {
    companion object {
        val DefaultSettingsName = "DEFAULT"
    }
    @Update
    suspend fun update(vararg settings: UserSettings)

    @Query("Delete from user_settings where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From user_settings where active = 1")
    fun getActiveUserSettings(): LiveData<UserSettings>

    @Query("Select * From user_settings where id = :id")
    suspend fun getById(id: String): UserSettings
}