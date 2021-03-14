package com.example.organizer.ui.money.debt.selectDebtType

import com.example.organizer.database.enums.DebtType
import com.example.organizer.ui.money.common.CommonSelectViewModel

class SelectDebtTypeViewModel : CommonSelectViewModel<DebtType>() {
    override fun areSameRecord(a: DebtType, b: DebtType): Boolean {
        return a.typeCode == b.typeCode
    }
}