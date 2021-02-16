package com.example.organizer.database.services

import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.entity.Transaction
import java.lang.Exception

class TransactionsService(private val transactionDAO: TransactionDAO) {
    suspend fun insert(transaction: Transaction) {
        val transactionType = TransactionType.from(transaction.transactionType)
        if(transactionType == TransactionType.EXPENSE && transaction.fromAccount == null) {
            throw Exception("From account needs to be defined for Expense")
        }
        if(transactionType == TransactionType.INCOME && transaction.toAccount == null) {
            throw Exception("To account needs to be defined for Expense")
        }
        if(transactionType == TransactionType.TRANSFER && (transaction.fromAccount == null || transaction.toAccount == null)) {
            throw Exception("Both To account and From account need to be defined for Transfer")
        }
        transactionDAO.insert(transaction)
    }

    suspend fun delete(id: String) =  transactionDAO.deleteById(id)

    fun getAllTransactions() = transactionDAO.getAllTransactions()
}