package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Debt


@Dao
interface DebtDAO: BaseDAO {
    @Query("Select * From debts ORDER BY created_at")
    fun getAllDebts(): LiveData<List<Debt>>

    @Query("Select * From debts WHERE completed_at is NULL ORDER BY created_at")
    fun getAllIncompleteDebts(): LiveData<List<Debt>>

    @Query("Select * From debts WHERE id=:id")
    fun getDebtById(id: String): LiveData<Debt>

    @Query("Delete From debts WHERE id=:id")
    suspend fun deleteDebtById(id: String)
    @Update
    suspend fun update(debt:Debt)


    @Insert
    suspend fun insertTransaction(transaction: com.example.organizer.database.entity.Transaction)
}