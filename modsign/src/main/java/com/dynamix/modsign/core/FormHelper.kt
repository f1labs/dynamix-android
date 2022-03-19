package com.dynamix.modsign.core

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.widget.LinearLayout

object FormHelper {
    fun dpToPx(context: Context?, dp: Int): Int {
        if (dp < 1) {
            return dp
        }
        val r = context!!.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }

    fun dpToPx(context: Context, dp: Float): Int {
        val r = context.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.displayMetrics
            )
        )
    }

    fun getDimension(context: Context?, width: String?): Int {
        return when {
            TextUtils.isEmpty(width) -> {
                0
            }
            width.equals("match_parent", ignoreCase = true) -> {
                LinearLayout.LayoutParams.MATCH_PARENT
            }
            width.equals("wrap_content", ignoreCase = true) -> {
                LinearLayout.LayoutParams.WRAP_CONTENT
            }
            width!!.endsWith("dp") -> {
                dpToPx(context, width.replace("dp", "").toInt())
            }
            else -> {
                width.replace("[^\\d]".toRegex(), "").toInt()
            }
        }
    }
}