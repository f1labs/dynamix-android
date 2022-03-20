package com.dynamix.core.init

import com.dynamix.core.locale.DynamixLocaleStrings

object DynamixEnvironmentData {

    var FILE_PROVIDER = ""
    var token = ""
    var deviceId = ""
    var requestHeadersMap: Map<String, String> = emptyMap()
    var isFormAmountCardEnabled = false
    var isEnableTxnLimit = false
    var localeEnabled = false
    var locale: String = DynamixLocaleStrings.ENGLISH
    var loggedInUserName = ""
}