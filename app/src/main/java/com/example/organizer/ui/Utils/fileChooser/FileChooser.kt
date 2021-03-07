package com.example.organizer.ui.Utils.fileChooser

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
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileFilter

class FileChooser : Fragment() {

    companion object {
        fun newInstance() =
            FileChooser()
    }

    private lateinit var viewModel: FileChooserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.file_chooser_fragment, container, false)
    }

    private fun getDirs(path: File): List<File> {
        println(path)
        val result = mutableListOf<File>()
        if (path.exists()) {
            path.listFiles(FileFilter {
                it != null && it.canRead()
            })?.forEach {
                if (it != null) {
                    result.add(it)
                }
            }
            result.sortBy { it.nameWithoutExtension }
            result.sortBy { !it.isDirectory }

        }
        return result;
    }

    private fun updateList(view: View) {
        val files: List<File> = getDirs(viewModel.currentDirectory.value!!)
        val list: RecyclerView = view.findViewById(R.id.file_chooser_list)
        list.adapter = FileChooserAdapter(files, viewModel, view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("filechooser view created")
        viewModel = ViewModelProvider(this.requireActivity())
            .get(FileChooserViewModel::class.java)
        viewModel.clearModel()
        activity?.onBackPressedDispatcher?.addCallback(
            FileChooserBackButtonCallback(
                view,
                viewModel
            )
        )
        updateList(view)
        view.findViewById<View>(R.id.internal_storage)
            .setOnClickListener {
                viewModel.selectDir(Environment.getExternalStorageDirectory(), true)
            }
        view.findViewById<View>(R.id.sd_card_button)
            .setOnClickListener {
                viewModel.selectDir(File("/storage/9016-4EF8/"), true)
            }
        view.findViewById<View>(R.id.previous_directory)
            .setOnClickListener {
                if(viewModel.backToPreviousDir()) {
                    viewModel.selectDir(Environment.getExternalStorageDirectory(), true)
                }
            }
        view.findViewById<View>(R.id.select_directory)
            .setOnClickListener {
                viewModel.selectedDirectory = viewModel.currentDirectory.value
                view.findNavController().popBackStack()
            }
        view.findViewById<View>(R.id.new_folder)
            .setOnClickListener {
                val folderNameInput = EditText(requireContext())
                MaterialAlertDialogBuilder(view.context, R.style.AppTheme_CenterModal)
                    .setTitle("Folder Name")
                    .setView(folderNameInput)
                    .setPositiveButton("Create") { dialogInterface: DialogInterface, _: Int ->
                        createFolder(view, folderNameInput.text.toString().trim())
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        viewModel.currentDirectory.observe(this, Observer {
            if (it != null && it.isDirectory) {
                updateList(view)
            }
        })
    }

    private fun createFolder(view:View ,folderName: String) {
        val folder = File(viewModel.currentDirectory.value, folderName)
        if (folder.exists()) {
            Toast.makeText(requireContext(), "Duplicate folder exists", Toast.LENGTH_SHORT)
                .show()
        } else {
            try {
                folder.mkdir()
                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT)
                    .show()
                updateList(view)
                // TODO: Focus item
            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Failed due to " + ex.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}

class FileChooserBackButtonCallback(val view: View, val viewModel: FileChooserViewModel) :
    OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
        if (viewModel.backToPreviousDir()) {
            view.findNavController().popBackStack()
        }
    }
}