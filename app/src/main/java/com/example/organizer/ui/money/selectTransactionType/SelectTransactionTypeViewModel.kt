package com.example.organizer.ui.money.selectTransactionType

import com.example.organizer.database.enums.TransactionType
import com.example.organizer.ui.money.common.CommonSelectViewModel

class SelectTransactionTypeViewModel : CommonSelectViewModel<TransactionType>() {
    override fun areSameRecord(a: TransactionType, b: TransactionType): Boolean {
        return a.typeCode == b.typeCode
    }
}