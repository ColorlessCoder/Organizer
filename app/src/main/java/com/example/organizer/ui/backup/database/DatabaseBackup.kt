package com.example.organizer.ui.backup.database

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.organizer.R

class DatabaseBackup : Fragment() {

    companion object {
        fun newInstance() = DatabaseBackup()
    }

    private lateinit var viewModel: DatabaseBackupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.database_backup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DatabaseBackupViewModel::class.java)
        // TODO: Use the ViewModel
    }

}