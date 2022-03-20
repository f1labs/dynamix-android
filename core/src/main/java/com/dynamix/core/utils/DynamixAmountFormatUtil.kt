package com.dynamix.core.utils

import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.locale.DynamixLocaleStrings
import timber.log.Timber
import java.text.DecimalFormat

/**
 * Created by Kiran Gyawali on 10/28/2018.
 */
object DynamixAmountFormatUtil {
    fun formatAmountWithCurrencyCode(amount: String?): String {
        var amt = amount
        if (amt == null) {
            amt = "N/A"
        }
        if (amt.contains(".")) {
            return if ("N/A" == amt) amt else "NPR $amt"
        }
        return if ("N/A" == amt) amt else "NPR " + formatAmount(amt)
    }

    fun formatAmountWithCurrencyCode(currencyCode: String?, amount: String?): String {
        val currencyCodeFormatted = currencyCode ?: "NPR"
        var amt = amount
        if (amt == null) {
            amt = "N/A"
        }
        if (amt.contains(".")) {
            return if ("N/A" == amt) amt else "$currencyCodeFormatted $amt"
        }
        return if ("N/A" == amt) amt else currencyCodeFormatted + " " + formatAmount(amt)
    }

    fun formatAmount(amount: String?): String {
        var amt = amount
        if (amt == null) {
            amt = "N/A"
        }
        if (amt.contains(".")) {
            return amt
        }
        return if (amt.isNotEmpty()) {
            if (DynamixEnvironmentData.localeEnabled &&
                DynamixEnvironmentData.locale.equals(DynamixLocaleStrings.NEPALI, true)
            ) {
                unicodeAmountConverter(getFormat(amt))
            } else {
                getFormat(amt)
            }
        } else amt
    }

    fun formatNumericAmount(amount: String): String {
        return getFormat(amount)
    }

    private fun getFormat(s: String): String {
        return try {
            //remove comma in amount
            var localValue = s.replace(",", "")
            val decimalFormat = DecimalFormat("####.##")
            localValue = decimalFormat.format(localValue.toDouble())
            val end: String
            val start: String
            if (localValue.contains(".")) {
                val splits = localValue.split("[.]".toRegex()).toTypedArray()
                if (splits.size > 2) {
                    return "invalid amount"
                } else {
                    val endTemp = splits[1]
                    val amountTemp = splits[0]
                    end = if (endTemp.length >= 2) {
                        endTemp.substring(0, 2)
                    } else if ("" == endTemp) {
                        "00"
                    } else {
                        endTemp + "0"
                    }
                    start = if ("" == amountTemp) {
                        "0"
                    } else {
                        getFirstPart(amountTemp)
                    }
                }
            } else {
                end = "00"
                start = getFirstPart(localValue)
            }
            "$start.$end"
        } catch (e: Exception) {
            Timber.tag("AMOUNT_FORMAT_UTIL").e(e.toString())
            s
        }
    }

    private fun getFirstPart(s: String): String {
        var start = ""
        var negative = ""
        var amountTemp = s
        if (amountTemp[0] == '-') {
            negative = "-"
            amountTemp = amountTemp.substring(1)
        }
        if (amountTemp.length > 5) {
            val hundreds = amountTemp.substring(amountTemp.length - 3)
            var remaining = amountTemp.substring(0, amountTemp.length - 3)
            var hasMore = true
            var tempS: String
            while (hasMore) {
                if (remaining.length > 1) {
                    tempS = remaining.substring(remaining.length - 2)
                    start = if ("" != start) {
                        "$tempS,$start"
                    } else {
                        tempS
                    }
                    remaining = remaining.substring(0, remaining.length - 2)
                } else {
                    if ("" != start) {
                        if (remaining.length > 0) {
                            start = "$remaining,$start"
                        }
                    } else {
                        start = remaining
                    }
                    hasMore = false
                }
            }
            start = "$start,$hundreds"
        } else {
            start = if (amountTemp.length > 3) {
                amountTemp.substring(
                    0,
                    amountTemp.length - 3
                ) + "," + amountTemp.substring(amountTemp.length - 3)
            } else {
                amountTemp
            }
        }
        return negative + start
    }

    private fun unicodeAmountConverter(amount: String): String {
        var amt = amount
        amt = amt.replace("0", "०")
        amt = amt.replace("1", "१")
        amt = amt.replace("2", "२")
        amt = amt.replace("3", "३")
        amt = amt.replace("4", "४")
        amt = amt.replace("5", "५")
        amt = amt.replace("6", "६")
        amt = amt.replace("7", "७")
        amt = amt.replace("8", "८")
        amt = amt.replace("9", "९")
        return amt
    }
}