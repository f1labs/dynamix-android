package com.dynamix.core.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

/**
 * Created by user on 27/10/2021.
 */

fun Context.getRoundedRect(radiusDp: Float = 4f, color: Int, thicknessDp: Int = 1): Drawable {
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.RECTANGLE
    shape.cornerRadius = radiusDp.dp(this)
    shape.setStroke(thicknessDp.dp(this), ColorStateList.valueOf(color))
    return shape
}

fun Context.getSolidRoundedRect(
    radiusDp: Float = 4f,
    bgColor: Int,
    strokeColor: Int? = null,
    thicknessDp: Int = 1
): Drawable {
    val shape = GradientDrawable()
    shape.shape = GradientDrawable.RECTANGLE
    shape.cornerRadius = radiusDp.dp(this)
    shape.color = ColorStateList.valueOf(bgColor)
    shape.setStroke(thicknessDp.dp(this), ColorStateList.valueOf(strokeColor ?: bgColor))
    return shape
}
