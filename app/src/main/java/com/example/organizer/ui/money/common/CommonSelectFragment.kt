package com.example.organizer.ui.money.common

import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R

abstract class CommonSelectFragment<T, VM:CommonSelectViewModel<T>, VH:CommonSelectViewHolder, AT: CommonSelectRecyclerListAdapter<VH, T, VM>> : Fragment() {

    lateinit var selectViewModel: VM

    abstract fun getSelectRecyclerView(): RecyclerView

    fun handleCommonSelectButtons(view: View, forceHide: Boolean = false) {
        val selectAllButton = view.findViewById<View>(R.id.select_all);
        val selectNoneButton = view.findViewById<View>(R.id.select_none);
        val selectLayout = view.findViewById<View>(R.id.common_select_layout_include)
        if(selectViewModel.mode == CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE && !forceHide) {
            selectLayout.visibility = View.VISIBLE
            selectAllButton.setOnClickListener {
                selectViewModel.selectAll()
                (getSelectRecyclerView().adapter as AT).notifyDataSetChanged();
            }
            selectNoneButton.setOnClickListener {
                selectViewModel.selectNone()
                (getSelectRecyclerView().adapter as AT).notifyDataSetChanged();
            }
        } else {
            selectLayout.visibility = View.GONE
            selectAllButton.setOnClickListener(null)
            selectNoneButton.setOnClickListener(null)
        }
    }

    fun setSelectGridAdapter(list: List<T>, adapter: AT) {
        selectViewModel.setCurrentList(list.toMutableList())
        getSelectRecyclerView().adapter = adapter
    }
}