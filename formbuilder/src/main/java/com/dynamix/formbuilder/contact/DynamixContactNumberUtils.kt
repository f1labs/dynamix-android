package com.dynamix.formbuilder.contact

import com.dynamix.core.utils.DynamixCommonUtils.validateRegex
import com.dynamix.formbuilder.data.DynamixFormConfig

/**
 * Created by user on 11/4/20
 */
object DynamixContactNumberUtils {
    fun getStrippedPhoneNumber(phoneNumber: String): String {
        var strippedNumber = phoneNumber.replace("[^\\d]".toRegex(), "")
        if (strippedNumber.startsWith("977")) strippedNumber = strippedNumber.replace("977", "")
        if (strippedNumber.startsWith("0")) strippedNumber = strippedNumber.replace("0", "")
        return strippedNumber
    }

    fun isValidPhoneNumber(phoneNumber: String?): Boolean {
        return validateRegex(DynamixFormConfig.REGEX_MOBILE, phoneNumber!!) ||
                validateRegex(DynamixFormConfig.REGEX_LANDLINE, phoneNumber)
    }
}