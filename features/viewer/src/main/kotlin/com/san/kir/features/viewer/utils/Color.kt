package com.san.kir.features.viewer.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap

internal fun contentColorBy(containerColor: Int): Int {
    return if (ColorUtils.calculateLuminance(containerColor) > 0.5f)
        Color.BLACK
    else
        Color.WHITE
}

internal fun Context.circleDrawable(containerColor: Int): Drawable {
    val color = ColorDrawable(containerColor).toBitmap(100, 100)
    val icon = RoundedBitmapDrawableFactory.create(resources, color)
    icon.isCircular = true
    return icon
}

internal fun Context.setContainerColor(containerColor: Int, vararg view: View) {
    val icon = circleDrawable(containerColor)
    view.forEach {
        when (it) {
            is ImageButton, is ProgressBar -> it.background = icon
            else -> it.setBackgroundColor(containerColor)
        }
    }
}

internal fun setContentColor(containerColor: Int, vararg view: View) {
    val contentColor = contentColorBy(containerColor)
    view.forEach {
        when (it) {
            is TextView -> it.setTextColor(contentColor)
            is ImageView -> it.drawable.setTint(contentColor)
            is ProgressBar -> it.indeterminateTintList = ColorStateList.valueOf(contentColor)
        }
    }
}
