package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.TemplateTransaction
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.relation.TemplateTransactionDetails
import com.example.organizer.database.relation.TransactionDetails
import java.util.*

@Dao
interface TransactionDAO {
    @Insert
    suspend fun insert(vararg transaction: Transaction)

    @Update
    suspend fun updateAccount(vararg account: Account)

    @Query("Select * From accounts where id = :id")
    suspend fun getAccount(id: String): Account

    @androidx.room.Transaction
    suspend fun insertAndUpdateAccount(transaction: Transaction) {
        if (transaction.toAccount != null) {
            var account = getAccount(transaction.toAccount!!)
            account.balance += transaction.amount
            updateAccount(account)
        }
        if (transaction.fromAccount != null) {
            var account = getAccount(transaction.fromAccount!!)
            account.balance -= transaction.amount
            if (account.balance < 0) {
                throw Exception("Balance of " + account.accountName + " will become negative.")
            }
            updateAccount(account)
        }
        insert(transaction)
    }

    suspend fun transactionToString(transaction: TemplateTransactionDetails): String {
        return TransactionType.from(transaction.transaction.transactionType).name +
                " " + transaction.transaction.amount.toString() +
                (if (transaction.fromAccountName == null) "" else (" from account '" + transaction.fromAccountName + "'")) +
                (if (transaction.toAccountName == null) "" else (" to account '" + transaction.toAccountName + "'")) +
                (if (transaction.categoryName == null) "" else (" under category '" + transaction.categoryName + "'"))
    }

    @androidx.room.Transaction
    suspend fun applyTransactionPlan(templates: List<TemplateTransactionDetails>): String {
        var successfulMessage = ""
        templates
            .sortedBy { template -> template.transaction.order }
            .forEachIndexed { index, it ->
                try {
                    insertAndUpdateAccount(
                        Transaction(
                            UUID.randomUUID().toString(),
                            it.transaction.transactionType,
                            it.transaction.amount,
                            it.transaction.fromAccount,
                            it.transaction.toAccount,
                            null,
                            it.transaction.transactionCategoryId,
                            it.transaction.details,
                            Date().time
                        )
                    )
                    successfulMessage += "Template #" + (index + 1) + "\n" + transactionToString(it) + "\n\n"
                } catch (ex: Throwable) {
                    throw Exception(
                        "Template #" + (index + 1) + "\n" + "Unable to " + transactionToString(it) + "\n" + "Reason: " + ex.message
                    )
                }
            }
        return successfulMessage
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