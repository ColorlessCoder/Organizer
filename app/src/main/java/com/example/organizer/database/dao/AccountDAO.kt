package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account
import com.example.organizer.database.enums.TransactionType
import java.util.*

@Dao
interface AccountDAO {
    @Insert
    suspend fun insert(vararg account: Account)

    @androidx.room.Transaction
    suspend fun createAccount(account: Account) {
        insert(account)
        insertTransaction(
            com.example.organizer.database.entity.Transaction(
                0,
                TransactionType.INITIAL.typeCode,
                account.balance,
                null,
                account.id,
                null,
                null,
                null,
                Date().time,
                null,
                null,
                null,
                null,
                null,
            ),
        )
    }

    @Insert
    suspend fun insertTransaction(transaction: com.example.organizer.database.entity.Transaction)

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