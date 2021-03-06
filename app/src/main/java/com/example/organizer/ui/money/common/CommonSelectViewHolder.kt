package com.example.organizer.ui.money.common

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

abstract class CommonSelectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun getDrawableElement(): TextView
    open fun getTintForSelect(): Int {
        return Color.WHITE;
    }
}