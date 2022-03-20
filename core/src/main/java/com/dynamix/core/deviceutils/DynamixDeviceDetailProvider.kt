package com.dynamix.core.deviceutils

import io.reactivex.Observable

interface DynamixDeviceDetailProvider {

    val model: Observable<String>

    val deviceDetails: Observable<String>

    val screenHeightInPixels: Observable<Int>

    val screenWidthInPixels: Observable<Int>

    val deviceModel: Observable<String>

    val emailAddress: Observable<String>

    val deviceId: Observable<String>

    val deviceIdAsString: String
}