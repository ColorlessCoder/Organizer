package com.example.organizer.ui.backup.database

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.importButton)
            .setOnClickListener {
                val action = DatabaseBackupDirections.actionNavDatabaseBackupToFileChooser()
                findNavController().navigate(action)
            }
    }

}