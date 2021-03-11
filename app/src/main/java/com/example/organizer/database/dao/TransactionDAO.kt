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
import com.example.organizer.database.relation.TemplateTransactionDetails
import com.example.organizer.database.relation.TransactionDetails
import java.util.*


@Dao
interface TransactionDAO : BaseDAO {
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
                            Date().time,
                            null
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

    @androidx.room.Transaction
    @RawQuery
    suspend fun getTransactionDetailsAsPerRawQuery(query: SupportSQLiteQuery): List<TransactionDetails>

    suspend fun getAllTransactionDetailsQueryForFilter(
        accountIds: List<String>?,
        categoryIds: List<String>?,
        type: List<Int>?,
        days: Int
    ): SimpleSQLiteQuery {
        var queryString: StringBuilder = StringBuilder()
        var args: MutableList<Any> = mutableListOf()
        val before = Date().time - (1L * days * 24 * 60 * 60 * 1000)
        queryString.append("Select * From transactions Where transacted_at > ? ")
        args.add(before)
        if (accountIds != null) {
            if (accountIds.isEmpty()) {
                queryString.append(" AND from_account IS NULL AND to_account IS NULL ")
            } else {
                queryString.append(
                    " AND ${createInQuery(
                        "from_account",
                        accountIds
                    )} or ${createInQuery("to_account", accountIds)} "
                )
                args.addAll(accountIds)
                args.addAll(accountIds)
            }
        }
        if (categoryIds != null) {
            if (categoryIds.isEmpty()) {
                queryString.append(" AND transaction_category_id IS NULL ")
            } else {
                queryString.append(" AND ${createInQuery("transaction_category_id", categoryIds)} ")
                args.addAll(categoryIds)
            }
        }
        if (type != null) {
            if (type.isEmpty()) {
                queryString.append(" AND transaction_type IS NULL ")
            } else {
                queryString.append(" AND ${createInQuery("transaction_type", type)} ")
                args.addAll(type)
            }
        }
        queryString.append(" ORDER BY transacted_at DESC ")
        println(queryString.toString())
        val query = SimpleSQLiteQuery(queryString.toString(), args.toTypedArray())
        return query
    }


    @Insert
    suspend fun insert(vararg debt: Debt)

    @Update
    suspend fun update(vararg debt: Debt)

    @androidx.room.Transaction
    suspend fun createDebt(debt: Debt) {
        insert(debt)
        val debtType = DebtType.from(debt.debtType)
        if (debtType != DebtType.INSTALLMENT) {
            insertAndUpdateAccount(
                Transaction(
                    UUID.randomUUID().toString(),
                    debtType.relatedTransactionType.typeCode,
                    debt.amount - debt.paidSoFar,
                    debt.fromAccount,
                    debt.toAccount,
                    null,
                    null,
                    debtType.name + ": " + debt.details,
                    Date().time,
                    debt.id
                )
            )
        }
    }

}