package com.example.organizer.ui.backup.database

import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.SalatSettings

class DatabaseBackupViewModel : ViewModel() {
    var navigationPurpose = Purpose.NONE
    lateinit var salatSettings:SalatSettings

    companion object {
        enum class Purpose {
            IMPORT, EXPORT, NONE
        }
    }
}