package com.example.organizer.ui.money.selectDebtType

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.enums.DebtType
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.money.common.CommonSelectFragment
import com.example.organizer.ui.money.common.CommonSelectRecyclerListAdapter
import com.example.organizer.ui.money.common.CommonSelectViewHolder

class SelectDebtType :
    CommonSelectFragment<DebtType, SelectDebtTypeViewModel, DebtTypeAdapter.ViewHolder, DebtTypeAdapter>() {

    companion object {
        fun newInstance() = SelectDebtType()
    }

    private lateinit var currentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_debt_type_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentView = view
        selectViewModel =
            ViewModelProvider(requireActivity()).get(SelectDebtTypeViewModel::class.java)
        if (selectViewModel.allSelected) {
            selectViewModel.selectAll()
        }
        setSelectGridAdapter(
            DebtType.values().asList(),
            DebtTypeAdapter(
                DebtType.values().asList(),
                selectViewModel,
                view
            )
        )
        this.handleCommonSelectButtons(view)
    }

    override fun getSelectRecyclerView(): RecyclerView {
        return currentView.findViewById(R.id.selectDebtTypeList);
    }
}

class DebtTypeAdapter(
    private val debtTypeList: List<DebtType>,
    private val viewModel: SelectDebtTypeViewModel,
    private val parentView: View
) : CommonSelectRecyclerListAdapter<DebtTypeAdapter.ViewHolder, DebtType, SelectDebtTypeViewModel>(
    debtTypeList,
    viewModel,
    parentView
) {
    lateinit var context: Context

    class ViewHolder(view: View) : CommonSelectViewHolder(view) {
        val label: TextView = view.findViewById(R.id.transactionTypeLabel)
        override fun getDrawableElement(): TextView {
            return label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_type_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return debtTypeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val type = debtTypeList.get(position)
        holder.label.text = type.name
        holder.label.background = ShpaeUtil.getRoundCornerShape(
            15.toFloat(),
            ContextCompat.getColor(context, type.color),
            null
        )
    }
}