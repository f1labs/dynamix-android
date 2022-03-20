package com.dynamix.core.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

/**
 * Created by user on 15/09/2021.
 */

@ColorInt
fun Context.colorFromTheme(@AttrRes attrColor: Int): Int {
    return resolveThemeAttribute(attrColor)
}

fun Context.resolveThemeAttribute(@AttrRes attr: Int): Int {
    return TypedValue().let {
        theme.resolveAttribute(attr, it, true)
        it.data
    }
}

fun Context.themeTintedDrawable(
        @DrawableRes resId: Int,
        @AttrRes attrColor: Int
): Drawable {
    val color = colorFromTheme(attrColor)
    val drawable = drawableFor(resId)
    drawable.setTint(color)
    return drawable
}

fun Context.themeTintedDrawableFromTheme(
        @AttrRes drawableAttr: Int,
        @AttrRes attrColor: Int
): Drawable {
    val color = colorFromTheme(attrColor)
    val drawable = drawableFromTheme(drawableAttr)
    drawable.setTint(color)
    return drawable
}


fun Context.drawableFromTheme(@AttrRes attrDrawable: Int): Drawable {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrDrawable, typedValue, true)
    val drawableId = typedValue.resourceId
    return drawableFor(drawableId)
}

fun Context.colorFor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun Context.drawableFor(@DrawableRes drawable: Int): Drawable {
    return ContextCompat.getDrawable(this, drawable)!!
}

fun Int.dp(context: Context): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
            .toInt()
}

fun Float.dp(context: Context): Float {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics)
}
