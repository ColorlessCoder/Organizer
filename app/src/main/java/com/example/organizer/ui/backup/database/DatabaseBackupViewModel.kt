package com.example.organizer.ui.backup.database

import androidx.lifecycle.ViewModel

class DatabaseBackupViewModel : ViewModel() {
    var navigationPurpose = Purpose.NONE

    companion object {
        enum class Purpose {
            IMPORT, EXPORT, NONE
        }
    }
}