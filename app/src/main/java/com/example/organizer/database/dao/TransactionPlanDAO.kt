package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.TransactionPlan

@Dao
interface TransactionPlanDAO {
    @Insert
    suspend fun insert(vararg entity: TransactionPlan)

    @Update
    suspend fun update(vararg entity: TransactionPlan)

    @Query("Delete from transaction_plans where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From transaction_plans")
    fun getAllTransactionPlans(): LiveData<List<TransactionPlan>>

    @Query("Select * From transaction_plans where id = :id")
    fun getTransactionPlanById(id: String): LiveData<TransactionPlan>

}