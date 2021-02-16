package com.example.organizer.ui.Utils

import android.graphics.drawable.GradientDrawable

class ShpaeUtil {
    companion object {
        fun getRoundCornerShape(
            radius: Float,
            fillColor: Int,
            borderColor: Int?
        ): GradientDrawable {
            val roundShape = GradientDrawable()
            roundShape.run {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = radius
                setColor(fillColor)
                if (borderColor != null) {
                    setStroke(3, borderColor)
                }
            }
            return roundShape
        }
    }
}