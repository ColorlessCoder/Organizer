package com.example.organizer.ui.backup.database

import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.UserSettings

class DatabaseBackupViewModel : ViewModel() {
    var navigationPurpose = Purpose.NONE
    lateinit var userSettings:UserSettings

    companion object {
        enum class Purpose {
            IMPORT, EXPORT, NONE
        }
    }
}