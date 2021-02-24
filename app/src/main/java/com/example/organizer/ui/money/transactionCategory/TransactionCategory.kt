package com.example.organizer.ui.money.transactionCategory

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Category
import com.example.organizer.ui.money.selectTransactionType.SelectTransactionTypeViewModel

class TransactionCategory : Fragment() {

    companion object {
        fun newInstance() = TransactionCategory()
    }

    private lateinit var viewModel: TransactionCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transaction_category_fragment, container, false)
    }

    private fun updateCategoryList(categories: List<Category>, view: View) {
        view.findViewById<RecyclerView>(R.id.category_list)
            .adapter = CategoryListAdapter(
            categories,
            viewModel,
            view
        )
    }

    private fun setCategoryListForType(view: View) {
        val categoryDAO = AppDatabase.getInstance(requireContext()).categoryDao()
        categoryDAO.getCategoriesByType(viewModel.transactionType.value!!.typeCode).observe(this, Observer {
            updateCategoryList(it, view)
        })
    }

    private fun updateTransactionTypeView(button: Button) {
        button.text = viewModel.transactionType.value!!.name
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), viewModel.transactionType.value!!.color))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionCategoryViewModel::class.java)
        val typeViewModel = ViewModelProvider(requireActivity()).get(SelectTransactionTypeViewModel::class.java)
        if(viewModel.navigatedToSet == TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE) {
            viewModel.transactionType.value = typeViewModel.transactionType
        }
        println(typeViewModel.transactionType)
        setCategoryListForType(view)
        viewModel.transactionType.observe(this, Observer {
            setCategoryListForType(view)
        })
        val button = view.findViewById<Button>(R.id.transaction_type_button);
        updateTransactionTypeView(button);
        button.setOnClickListener {
            viewModel.navigatedToSet = TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE
            val action = TransactionCategoryDirections.actionTransactionCategoryToSelectTransactionType()
            findNavController().navigate(action)
        }
        view.findViewById<View>(R.id.create_button)
            .setOnClickListener {
                val action = TransactionCategoryDirections.actionTransactionCategoryToEditCategory(null)
                action.transactionType = viewModel.transactionType.value!!.typeCode
                findNavController().navigate(action)
            }
    }

}

class CategoryListAdapter(
    private val categories: List<Category>,
    private val viewModel: TransactionCategoryViewModel,
    private val parentView: View
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    lateinit var context: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label: TextView = view.findViewById(R.id.category_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = categories.get(position)
        holder.label.text = type.categoryName
        holder.itemView.setOnClickListener {
//            parentView.findNavController().popBackStack()
        }
    }
}