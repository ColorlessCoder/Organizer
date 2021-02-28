package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.TemplateTransaction
import com.example.organizer.database.relation.TemplateTransactionDetails

@Dao
interface TemplateTransactionDAO {
    @Insert
    suspend fun insert(vararg transaction: TemplateTransaction)

    @Update
    suspend fun update(vararg transaction: TemplateTransaction)

    @Query("Delete from transactions where id= :id")
    suspend fun deleteById(vararg id: String)

    @Query("Select * From template_transactions where id = :id")
    fun getById(id: String): LiveData<TemplateTransaction>

    @Transaction
    @Query("Select * From template_transactions where transaction_plan_id= :transactionPlanId order by `order` ASC")
    fun getAllTemplateTransactionDetails(transactionPlanId: String): LiveData<List<TemplateTransactionDetails>>

    @Transaction
    @Query("Select * From template_transactions where transaction_plan_id= :transactionPlanId order by `order` ASC")
    suspend fun getAllTemplateTransactionDetailsNonLive(transactionPlanId: String): List<TemplateTransactionDetails>

    @Transaction
    suspend fun updateList(templateTransactionList: List<TemplateTransaction>) {
        templateTransactionList.forEach { update(it) }
    }

}