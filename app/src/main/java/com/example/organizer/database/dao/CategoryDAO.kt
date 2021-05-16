package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.relation.TransactionDetails
import java.util.*

@Dao
interface CategoryDAO: BaseDAO {
    @Insert
    suspend fun insert(vararg category: Category)

    @Update
    suspend fun update(vararg category: Category)

    @Delete
    suspend fun delete(vararg category: Category)

    @Query("Select * From categories where id = :id")
    suspend fun getById(id:String): Category

    @Query("Select * From categories where id = :id")
    fun getCategory(id:String): LiveData<Category>

    @Query("Select * From categories where transaction_type = :type")
    fun getCategoriesByType(type:Int): LiveData<List<Category>>

    @Query("Select * From categories where category_name like :group")
    suspend fun getCategoriesLikeGroup(group: String): List<Category>

    @Query("Select * From categories")
    fun getAllCategories(): LiveData<List<Category>>

    @RawQuery
    suspend fun getCategories(query: SupportSQLiteQuery): List<Category>

    fun getQueryForCategoryTypeIn(type: List<Int>?): SimpleSQLiteQuery {
        var queryString: StringBuilder = StringBuilder()
        var args: MutableList<Any> = mutableListOf()
        queryString.append("Select * From categories Where 1=1 ")
        if(type != null) {
            if(type.isEmpty()) {
                queryString.append(" AND transaction_type IS NULL ")
            } else {
                queryString.append(" AND ${createInQuery("transaction_type", type)} ")
                args.addAll(type)
            }
        }
        queryString.append(" ORDER BY transaction_type DESC ")
        println(queryString.toString())
        val query = SimpleSQLiteQuery(queryString.toString(), args.toTypedArray())
        return query
    }
}