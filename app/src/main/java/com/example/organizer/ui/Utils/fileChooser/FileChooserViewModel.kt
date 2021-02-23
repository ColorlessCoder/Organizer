package com.example.organizer.ui.Utils.fileChooser

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class FileChooserViewModel : ViewModel() {
    val currentDirectory = MutableLiveData<File>()
    var selectedFile: File? = null
    var selectedDirectory: File? = null
    val selectedPath = MutableLiveData<File>()
    init {
        currentDirectory.value = File(Environment.getExternalStorageState())
        selectedPath.value = File(Environment.getExternalStorageState())
    }
}