package com.example.organizer.ui.money;

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.organizer.R
import com.example.organizer.ui.Utils.ShpaeUtil

class ColorSpinnerAdapter(val colors: List<Int>, val activity: FragmentActivity?) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.color_spinner, null);
        val item = view.findViewById<View>(R.id.colorSpinnerItem)
        item.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  colors[position], null)
        return view
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return colors.size
    }
}
