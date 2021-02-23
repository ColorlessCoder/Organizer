package com.example.organizer.ui.Utils.fileChooser

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
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
        if(path.exists()) {
            val dirs = path.listFiles(FileFilter {
                it != null && it.canRead()
            })
            dirs.forEach {
                if(it != null) {
                    result.add(it)
                }
            }
        }
        return result;
    }

    private fun updateList(view:View ) {
        val files: List<File> = getDirs(viewModel.currentDirectory.value!!)
        val list: RecyclerView = view.findViewById(R.id.file_chooser_list)
        list.adapter = FileChooserAdapter(files, viewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this.requireActivity())
            .get(FileChooserViewModel::class.java)
        updateList(view)
        viewModel.selectedPath.observe(this, Observer {
            if(it != null && it.isDirectory) {
                viewModel.currentDirectory.value = it
            }
        })
        viewModel.currentDirectory.observe(this, Observer {
            if(it != null && it.isDirectory) {
                updateList(view)
            }
        })
    }

}