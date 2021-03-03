package com.example.organizer.ui.money.common

import android.graphics.Color
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

abstract class CommonSelectRecyclerListAdapter<VH: CommonSelectViewHolder, T ,VM: CommonSelectViewModel<T>>(
    private val recordList: List<T>,
    private val viewModel: VM,
    private val parentView: View
) : RecyclerView.Adapter<VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        val record = recordList[position]
         holder.itemView.setOnClickListener {
            if(viewModel.selectRecord(record)) {
                this.notifyDataSetChanged()
            } else {
                if (viewModel.isRecordSelected(record)) {
                    holder.getDrawableElement().compoundDrawables[0]?.setTint(Color.WHITE)
                } else {
                    holder.getDrawableElement().compoundDrawables[0]?.setTint(Color.TRANSPARENT)
                }
            }
            if(viewModel.mode == CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE) {
                parentView.findNavController().popBackStack()
            }
        }
    }
}
