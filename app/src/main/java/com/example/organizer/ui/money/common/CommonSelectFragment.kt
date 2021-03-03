package com.example.organizer.ui.money.common

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.money.common.CommonSelectRecyclerListAdapter
import com.example.organizer.ui.money.common.CommonSelectViewHolder
import com.example.organizer.ui.money.common.CommonSelectViewModel

abstract class CommonSelectFragment<T, VM:CommonSelectViewModel<T>, VH:CommonSelectViewHolder, AT: CommonSelectRecyclerListAdapter<VH, T, VM>> : Fragment() {

    private lateinit var viewModel: VM

    abstract fun getSelectRecyclerView(): RecyclerView

    fun handleCommonSelectButtons(view: View) {
        val selectAllButton = view.findViewById<View>(R.id.select_all);
        val selectNoneButton = view.findViewById<View>(R.id.select_none);
        val selectLayout = view.findViewById<View>(R.id.select_button_layout)
        if(viewModel.mode == CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE) {
            selectLayout.visibility = View.VISIBLE
            selectAllButton.setOnClickListener {
                viewModel.selectAll()
                (getSelectRecyclerView().adapter as AT).notifyDataSetChanged();
            }
            selectNoneButton.setOnClickListener {
                viewModel.selectNone()
                (getSelectRecyclerView().adapter as AT).notifyDataSetChanged();
            }
        } else {
            selectLayout.visibility = View.GONE
            selectAllButton.setOnClickListener(null)
            selectNoneButton.setOnClickListener(null)
        }
    }
}