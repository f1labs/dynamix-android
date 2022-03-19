package com.dynamix.core.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.dynamix.core.R
import com.dynamix.core.utils.DynamixAmountFormatUtil.formatAmount
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixResourceUtils.getColor

object DynamixViewUtils {

    fun setVisible(target: View, isVisible: Boolean) {
        target.isVisible = isVisible
    }

    fun setInVisible(target: View, isInVisible: Boolean) {
        if (isInVisible) {
            target.isInvisible = true
        } else {
            target.isVisible = true
        }
    }

    fun getRoundedRect(context: Context, radiusDp: Float): Drawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = dpToPx(context, radiusDp).toFloat()
        return shape
    }

    fun setTextViewDrawableTint(view: TextView, color: Int) {
        val drawables = view.compoundDrawables
        for (drawable in drawables) {
            drawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun setTextViewDrawable(
        view: TextView,
        drawable: Drawable?,
        heightDp: Int,
        widthDp: Int,
        color: Int,
        gravity: Int
    ) {
        val context = view.context
        setTextViewDrawable(view, heightDp, widthDp, color, gravity, context, drawable)
    }

    fun setTextViewDrawable(
        view: TextView,
        @DrawableRes drawable: Int,
        heightDp: Int,
        widthDp: Int,
        color: Int,
        gravity: Int
    ) {
        val context = view.context
        val textDrawable = ContextCompat.getDrawable(context, drawable)
        setTextViewDrawable(view, heightDp, widthDp, color, gravity, context, textDrawable)
    }

    fun setTextViewDrawable(
        view: TextView,
        heightDp: Int,
        widthDp: Int,
        color: Int?,
        gravity: Int,
        context: Context,
        textDrawable: Drawable?
    ) {
        if (color != null) {
            DrawableCompat.setTint(textDrawable!!, color)
        }
        if (heightDp != -1 && widthDp != -1) {
            textDrawable!!.setBounds(0, 0, dpToPx(context, widthDp), dpToPx(context, heightDp))
            if (gravity == Gravity.START) {
                view.setCompoundDrawables(textDrawable, null, null, null)
            } else if (gravity == Gravity.END) {
                view.setCompoundDrawables(null, null, textDrawable, null)
            }
        } else {
            if (gravity == Gravity.START) {
                view.setCompoundDrawablesWithIntrinsicBounds(textDrawable, null, null, null)
            } else if (gravity == Gravity.END) {
                view.setCompoundDrawablesWithIntrinsicBounds(null, null, textDrawable, null)
            }
        }
    }

    fun setTextViewDrawableStart(
        view: TextView,
        @DrawableRes drawable: Int,
        heightDp: Int = -1,
        widthDp: Int = -1,
        color: Int
    ) {
        val context = view.context
        val startDrawable = ContextCompat.getDrawable(context, drawable)
        setTextViewDrawableStart(view, startDrawable, heightDp, widthDp, color)
    }

    fun setTextViewDrawableStart(
        view: TextView,
        startDrawable: Drawable?,
        heightDp: Int,
        widthDp: Int,
        color: Int
    ) {
        val context = view.context
        DrawableCompat.setTint(startDrawable!!, color)
        if (heightDp != -1 && widthDp != -1) {
            startDrawable.setBounds(0, 0, dpToPx(context, widthDp), dpToPx(context, heightDp))
            view.setCompoundDrawables(startDrawable, null, null, null)
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, null, null)
        }
    }

    fun setBadgeStyleForTextView(textView: TextView, bgTint: Int) {
        val context = textView.context
        val bg = getRoundedRect(context, dpToPx(context, 2).toFloat())
        DrawableCompat.setTint(bg, bgTint)
        textView.background = bg
        val padding = dpToPx(context, 6)
        textView.setPadding(padding, 0, padding, 0)
        textView.isAllCaps = true
    }

    fun expand(view: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = view.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.layoutParams.height = 1
        view.isVisible = true
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        animation.duration = (targetHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }

    fun collapse(view: View) {
        val initialHeight = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    view.isVisible = false
                } else {
                    view.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // 1dp/ms
        animation.duration =
            ((initialHeight / view.context.resources.displayMetrics.density).toLong())
        view.startAnimation(animation)
    }
}