package com.example.organizer.database.dao

interface BaseDAO {
    fun createInQuery(column: String, value: List<Any>):String {
        val queryString: StringBuilder = StringBuilder()
        queryString.append(" $column IN (?").append(", ?".repeat(value.size-1)).append(") ")
        return queryString.toString()
    }
}