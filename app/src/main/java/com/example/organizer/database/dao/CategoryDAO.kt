package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.relation.TransactionDetails

@Dao
interface CategoryDAO {
    @Insert
    suspend fun insert(vararg category: Category)

    @Update
    suspend fun update(vararg category: Category)

    @Delete
    suspend fun delete(vararg category: Category)

    @Query("Select * From categories where id = :id")
    fun getCategory(id:String): LiveData<Category>

    @Query("Select * From categories where transaction_type = :type")
    fun getCategoriesByType(type:Int): LiveData<List<Category>>

    @Query("Select * From categories")
    fun getAllCategories(): LiveData<List<Category>>

}