package com.dynamix.core.deviceutils

data class DynamixDeviceDetails(
    val isDeviceRooted: Boolean = false,
    val isDeveloperOptionsEnabled: Boolean = false,
    val isDontKeepActivitiesEnabled: Boolean = false,
    val manufacturer: String = "",
    val model: String = "",
    val sdkVersion: Int = 0,
    val androidVersion: String = "",
    val appVersionName: String = "",
    val appVersionCode: String = "",
)