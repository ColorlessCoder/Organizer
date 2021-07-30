package com.example.organizer.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.organizer.database.entity.*

data class TransactionChartValueDetails (
    @Embedded val chartValue: TransactionChartValue,
    @Relation(
        parentColumn = "entity_id",
        entity = Category::class,
        entityColumn = "id",
        projection = ["category_name"]
    )
    val categoryName: String?
)