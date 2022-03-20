package com.dynamix.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat

/**
 * Created by user on 12/11/20
 * Helper class to get android resources like drawable, colors etc.
 */
object DynamixResourceUtils {

    fun getThemeTintedDrawable(
        context: Context,
        @DrawableRes resId: Int,
        @AttrRes attrColor: Int
    ): Drawable {
        val color = getResolvedThemeAttribute(context, attrColor)
        val drawable = ContextCompat.getDrawable(context, resId)
        drawable!!.setTint(color)
        return drawable
    }

    fun getDrawableFromThemeAttributes(context: Context, @AttrRes attrDrawable: Int): Drawable {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrDrawable, typedValue, true)
        val drawableId = typedValue.resourceId
        return ContextCompat.getDrawable(context, drawableId)!!
    }

    @ColorInt
    fun getColorFromThemeAttributes(context: Context, @AttrRes attrColor: Int): Int {
        return getResolvedThemeAttribute(context, attrColor)
    }

    @ColorInt
    fun getColor(context: Context, @ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    fun getResolvedThemeAttribute(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    fun getDimenFromThemeAttribute(context: Context, @AttrRes attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return TypedValue.complexToDimensionPixelSize(
            typedValue.data,
            context.resources.displayMetrics
        )
    }

    fun getDimenInt(context: Context, @DimenRes dimen: Int): Int {
        return context.resources.getDimensionPixelSize(dimen)
    }
}