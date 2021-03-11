package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Debt
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.enums.DebtType
import com.example.organizer.database.relation.DebtDetails
import com.example.organizer.database.relation.TemplateTransactionDetails
import com.example.organizer.database.relation.TransactionDetails
import java.util.*


@Dao
interface DebtDAO: BaseDAO {
    @androidx.room.Transaction
    @Query("Select * From debts ORDER BY created_at")
    fun getAllDebtDetails(): LiveData<List<DebtDetails>>

    @Query("Select * From debts ORDER BY created_at")
    fun getAllDebts(): LiveData<List<Debt>>

    @androidx.room.Transaction
    @Query("Select * From debts WHERE id=:id ORDER BY created_at")
    fun getDebtDetailsById(id: String): LiveData<DebtDetails>


    @Insert
    suspend fun insertTransaction(transaction: com.example.organizer.database.entity.Transaction)
}