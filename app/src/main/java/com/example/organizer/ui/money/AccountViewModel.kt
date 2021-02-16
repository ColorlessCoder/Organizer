package com.example.organizer.ui.money

import android.graphics.Color
import androidx.lifecycle.ViewModel

class AccountViewModel : ViewModel(){
    var label: String = "Account"
    var balance: Float = 0.toFloat()
    var backgroundColor: Int = Color.WHITE
    var textColor: Int = Color.BLACK
    var unit:String = "BDT"
}