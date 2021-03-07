package com.example.organizer.ui.backup.database

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.ui.Utils.fileChooser.FileChooserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DatabaseBackup : Fragment() {

    companion object {
        fun newInstance() = DatabaseBackup()
    }

    private lateinit var viewModel: DatabaseBackupViewModel
    private lateinit var fileChooserViewModel: FileChooserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.database_backup_fragment, container, false)
    }

    fun showToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun exportBackup(dialogInterface: DialogInterface, directory: File, fileName: String) {
        val file = File(directory, "$fileName.db")
        if(file.exists()) {
            showToastMessage("Duplicate File Exists")
        } else {
            try {
                val currentDbFile = requireContext().applicationContext.getDatabasePath("organizer.db")
                if(currentDbFile.exists()) {
                    val db = AppDatabase.getInstance(requireContext())
                    lifecycleScope.launch {
                        db.utilDAO().checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
                        val src = FileInputStream(currentDbFile).channel
                        val dst = FileOutputStream(file).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                    }
                } else {
                    showToastMessage("Database does not exist")
                }
            } catch (ex: Exception) {
                println(ex)
                showToastMessage("Cannot backup due to "+ ex.message)
            }
        }
    }

    fun importBackup(file: File) {
        if(!file.exists()) {
            showToastMessage("File does not exists")
        } else {
            try {
                val currentDbFile = requireContext().applicationContext.getDatabasePath("organizer.db")
                val db = AppDatabase.getInstance(requireContext())
                val write = db.openHelper.writableDatabase.path
                AppDatabase.destroyInstance()
                val dst = FileOutputStream(write).channel
                val src = FileInputStream(file).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
            } catch (ex: Exception) {
                println(ex)
                showToastMessage("Cannot import backup due to "+ ex.message)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DatabaseBackupViewModel::class.java)
        fileChooserViewModel = ViewModelProvider(requireActivity()).get(FileChooserViewModel::class.java)
        if(viewModel.navigationPurpose == DatabaseBackupViewModel.Companion.Purpose.EXPORT && fileChooserViewModel.selectedDirectory != null) {
            val folderNameInput = EditText(requireContext())
            MaterialAlertDialogBuilder(view.context, R.style.AppTheme_CenterModal)
                .setTitle("File name")
                .setView(folderNameInput)
                .setPositiveButton("Export") { dialogInterface: DialogInterface, _: Int ->
                    exportBackup(dialogInterface, fileChooserViewModel.selectedDirectory!!, folderNameInput.text.toString())
                    dialogInterface.dismiss()
                }
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.cancel()
                }
                .show()
        } else  if (viewModel.navigationPurpose == DatabaseBackupViewModel.Companion.Purpose.IMPORT && fileChooserViewModel.selectedDirectory != null) {
            importBackup(fileChooserViewModel.selectedDirectory!!)
        }
        viewModel.navigationPurpose = DatabaseBackupViewModel.Companion.Purpose.NONE
        view.findViewById<View>(R.id.importButton)
            .setOnClickListener {
                val action = DatabaseBackupDirections.actionNavDatabaseBackupToFileChooser("/storage")
                action.chooseDirectory = false
                viewModel.navigationPurpose = DatabaseBackupViewModel.Companion.Purpose.IMPORT
                findNavController().navigate(action)
            }
        view.findViewById<View>(R.id.exportButton)
            .setOnClickListener {
                val action = DatabaseBackupDirections.actionNavDatabaseBackupToFileChooser("/storage")
                action.chooseDirectory = true
                viewModel.navigationPurpose = DatabaseBackupViewModel.Companion.Purpose.EXPORT
                findNavController().navigate(action)
            }
    }

}