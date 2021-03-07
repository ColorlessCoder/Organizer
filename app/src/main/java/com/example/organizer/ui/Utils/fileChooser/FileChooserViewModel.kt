package com.example.organizer.ui.Utils.fileChooser

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class FileChooserViewModel : ViewModel() {
    val currentDirectory = MutableLiveData<File>()
    var selectedDirectory: File? = null
    var chooseDirectory: Boolean = false
    val directoryTree = mutableListOf<File>()
    init {
        clearModel()
    }

    fun clearModel() {
        selectedDirectory = null
        selectDir(Environment.getExternalStorageDirectory(), true)
    }

    fun selectDir( dir: File, clearTree: Boolean) {
        if(clearTree) {
            directoryTree.clear()
        }
        currentDirectory.value = dir
        directoryTree.add(dir)
    }

    fun backToPreviousDir(): Boolean {
        directoryTree.removeAt(directoryTree.size-1)
        if(directoryTree.isNotEmpty()) {
            currentDirectory.value = directoryTree.last()
        }
        return directoryTree.isEmpty();
    }
}