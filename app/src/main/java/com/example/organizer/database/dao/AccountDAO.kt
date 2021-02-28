package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account

@Dao
interface AccountDAO {
    @Insert
    suspend fun insert(vararg account: Account)

    @Update
    suspend fun update(vararg account: Account)

    @Query("Delete from accounts where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From accounts")
    fun getAllAccounts(): LiveData<List<Account>>

    @Query("Select * From accounts where id = :id")
    fun getAccountById(id: String): LiveData<Account>

    @Query("Select * From accounts where id = :id")
    suspend fun getById(id: String): Account

}