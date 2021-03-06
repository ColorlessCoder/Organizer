package com.example.organizer.ui.money.common

import android.graphics.Color
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

abstract class CommonSelectRecyclerListAdapter<VH : CommonSelectViewHolder, T, VM : CommonSelectViewModel<T>>(
    private val recordList: List<T>,
    private val viewModel: VM,
    private val parentView: View
) : RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        val record = recordList[position]
        holder.itemView.setOnClickListener {
            println("Clicked")
            if (viewModel.selectRecord(record)) {
                this.notifyDataSetChanged()
            }
            if (viewModel.isRecordSelected(record)) {
                holder.getDrawableElement().compoundDrawables[2]?.setTint(holder.getTintForSelect())
            } else {
                holder.getDrawableElement().compoundDrawables[2]?.setTint(Color.TRANSPARENT)
            }
            if (viewModel.mode == CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE) {
                parentView.findNavController().popBackStack()
            }
        }
        if (viewModel.isRecordSelected(record)) {
            holder.getDrawableElement().compoundDrawables[2]?.setTint(holder.getTintForSelect())
        } else {
            holder.getDrawableElement().compoundDrawables[2]?.setTint(Color.TRANSPARENT)
        }
    }
}
