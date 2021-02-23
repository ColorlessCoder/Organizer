package com.example.organizer.ui.Utils.fileChooser

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ShpaeUtil
import java.io.File

class FileChooserAdapter(
    private val files: List<File>,
    private val viewModel: FileChooserViewModel
) : RecyclerView.Adapter<FileChooserAdapter.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileLabel: TextView = view.findViewById(R.id.file_label)
        val fileIcon: AppCompatImageView = view.findViewById(R.id.file_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.file_chooser_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files.get(position)
        holder.fileLabel.text = file.name
        holder.fileIcon.background = if (file.isDirectory)
            ContextCompat.getDrawable(context, R.drawable.ic_outline_folder_24)
        else ContextCompat.getDrawable(context, R.drawable.ic_outline_insert_drive_file_24)
        holder.itemView.setOnClickListener {
            if(file.isDirectory) {
                viewModel.selectedDirectory = file
                viewModel.selectedPath.value = file
            } else {
                viewModel.selectedFile = file
                viewModel.selectedPath.value = file
            }
        }
    }
}