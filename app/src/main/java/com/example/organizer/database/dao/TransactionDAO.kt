package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.relation.TransactionDetails

@Dao
interface TransactionDAO {
    @Insert
    suspend fun insert(vararg transaction: Transaction)

    @Update
    suspend fun updateAccount(vararg account: Account)

    @Query("Select * From accounts where id = :id")
    suspend fun getAccount(id:String): Account

    @androidx.room.Transaction
    suspend fun insertAndUpdateAccount(transaction: Transaction) {
        if(transaction.toAccount != null) {
            var account = getAccount(transaction.toAccount!!)
            account.balance += transaction.amount
            updateAccount(account)
        }
        if(transaction.fromAccount != null) {
            var account = getAccount(transaction.fromAccount!!)
            account.balance -= transaction.amount
            updateAccount(account)
        }
        insert(transaction)
    }

    @Query("Delete from transactions where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From transactions")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("Select * From transactions where id = :id")
    fun getTransactionById(id: String): LiveData<Transaction>

    @androidx.room.Transaction
    @Query("Select * From transactions")
    fun getAllTransactionDetails(): LiveData<List<TransactionDetails>>

    @androidx.room.Transaction
    @Query("Select * From transactions where from_account = :accountId or to_account = :accountId ORDER BY transacted_at DESC")
    fun getAllTransactionDetails(accountId: String): LiveData<List<TransactionDetails>>

}