package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.SalatTime
import com.example.organizer.database.enums.TransactionType
import java.util.*

@Dao
interface SalatTimesDAO: BaseDAO {
    @Insert
    suspend fun insert(vararg salatTime: SalatTime)

    @Update
    suspend fun update(vararg salatTime: SalatTime)

    @Query("Select * From salat_times where date = :date and address= :address")
    suspend fun getByDateAddress(date: String, address: String): SalatTime?

    @Query("Select * From salat_times where id = :id")
    suspend fun getById(id: String): SalatTime
}