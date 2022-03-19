package com.dynamix.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import com.dynamix.core.logger.LoggerProviderUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Created by Kiran Gyawali on 10/28/2018.
 */
object DynamixCommonUtils {
    fun validateRegex(regex: String, input: String): Boolean {
        return try {
            val pattern = Pattern.compile(regex)
            pattern.matcher(input).matches()
        } catch (e: PatternSyntaxException) {
            LoggerProviderUtils.error(e)
            false
        }
    }

    fun isNumeric(str: String): Boolean {
        try {
            val d = str.toDouble()
        } catch (nfe: Exception) {
            return false
        }
        return true
    }

    fun getKeyFromValueInHashMap(hm: Map<String?, String>?, value: String): String {
        if (hm == null) {
            return ""
        }
        for (o in hm.keys) {
            if (hm[o].equals(value, ignoreCase = true)) {
                return o ?: ""
            }
        }
        return ""
    }

    fun getValueFromKeyInHashMap(hm: Map<String?, String>, key: String): String? {
        for (o in hm.keys) {
            if (o != null) {
                if (o.equals(key, ignoreCase = true)) {
                    return hm[o]
                }
            }
        }
        return ""
    }

    fun convertDateTimeToMilliseconds(givenDateString: String): Long {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        try {
            val date = sdf.parse(givenDateString)!!
            val timeInMilliseconds = date.time
            println("Date in milli :: $timeInMilliseconds")
            return timeInMilliseconds
        } catch (e: ParseException) {
            LoggerProviderUtils.error(e)
        }
        return cal.timeInMillis
    }

    fun convertDateTimeToMilliseconds(givenDateString: String, dateFormat: String): Long {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        try {
            val date = sdf.parse(givenDateString)!!
            val timeInMilliseconds = date.time
            println("Date in milli :: $timeInMilliseconds")
            return timeInMilliseconds
        } catch (e: ParseException) {
            LoggerProviderUtils.error(e)
        }
        return cal.timeInMillis
    }

    fun getColorFromAttribute(context: Context, attr: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    fun getDrawableFromAttribute(context: Context, attr: Int): Drawable {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        val attributeResourceId = a.getResourceId(0, 0)
        return ResourcesCompat.getDrawable(context.resources, attributeResourceId, context.theme)!!
    }

    fun getDimenFromAttribute(context: Context, attr: Int): Int {
        val array = context.theme.obtainStyledAttributes(intArrayOf(attr))
        val dimension = array.getDimensionPixelSize(0, 0)
        array.recycle()
        return dimension
    }


    fun isNullOrEmpty(content: String?): Boolean {
        return content == null || content.isEmpty()
    }

    fun capitalizeWord(str: String): String {
        val finalStr = str.trim().replace("\\s+".toRegex(), " ")
        val words = finalStr.split("\\s".toRegex()).toTypedArray()
        val capitalizeWord = StringBuilder()
        for (w in words) {
            val first = w.substring(0, 1)
            val afterFirst = w.substring(1).lowercase()
            capitalizeWord.append(first.uppercase()).append(afterFirst).append(" ")
        }
        return capitalizeWord.toString().trim { it <= ' ' }
    }

    fun decimalLimiter(string: String, MAX_DECIMAL: Int): String {

        var str = string
        if (str[0] == '.') str = "0$str"
        val max = str.length

        var rFinal = ""
        var after = false
        var i = 0
        var up = 0
        var decimal = 0
        var t: Char

        val decimalCount = str.count { ".".contains(it) }

        if (decimalCount > 1)
            return str.dropLast(1)

        while (i < max) {
            t = str[i]
            if (t != '.' && !after) {
                up++
            } else if (t == '.') {
                after = true
            } else {
                decimal++
                if (decimal > MAX_DECIMAL)
                    return rFinal
            }
            rFinal += t
            i++
        }
        return rFinal
    }
}