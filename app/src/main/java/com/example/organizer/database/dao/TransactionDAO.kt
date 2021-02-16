package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.organizer.database.entity.Transaction

@Dao
interface TransactionDAO {
    @Insert
    suspend fun insert(vararg transaction: Transaction)

    @Query("Delete from transactions where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From transactions")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("Select * From transactions where id = :id")
    fun getTransactionById(id: String): LiveData<Transaction>

}