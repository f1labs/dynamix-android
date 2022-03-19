package com.dynamix.core.utils

import android.content.Context
import android.util.TypedValue

/**
 * Created by user on 1/19/21
 */
object DynamixConverter {

    fun dpToPx(context: Context, sizeDp: Int): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeDp.toFloat(), metrics)
            .toInt()
    }

    fun dpToPx(context: Context, sizeDp: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeDp, metrics).toInt()
    }

    fun getAppVersion(buildVersion: String): Int {
        val buildNumber = buildVersion.split("-".toRegex()).toTypedArray()[0]
        val appVersionSplit = buildNumber.split("\\.".toRegex()).toTypedArray()
        val appVersion = StringBuilder()
        for (s in appVersionSplit) {
            appVersion.append(s)
        }
        return appVersion.toString().toInt()
    }
}