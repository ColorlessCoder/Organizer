package com.example.organizer.ui.money.selectAccount

import com.example.organizer.database.entity.Account
import com.example.organizer.ui.money.common.CommonSelectViewModel

class SelectAccountViewModel : CommonSelectViewModel<Account>() {
    override fun areSameRecord(a: Account, b: Account): Boolean {
        return a.id == b.id
    }
}