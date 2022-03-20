package com.dynamix.core.deviceutils

import com.dynamix.core.init.DynamixEnvironmentData
import io.reactivex.Observable

class DynamixDeviceDetailDummyProviderImpl(private val dynamixDeviceHelper: DynamixDeviceHelper) :
    DynamixDeviceDetailProvider {

    override val model: Observable<String>
        get() = dynamixDeviceHelper.getModel()

    override val deviceDetails: Observable<String>
        get() = dynamixDeviceHelper.getDeviceDetails()

    override val screenHeightInPixels: Observable<Int>
        get() = dynamixDeviceHelper.screenHeightInPixels

    override val screenWidthInPixels: Observable<Int>
        get() = dynamixDeviceHelper.screenWidthInPixels

    override val deviceModel: Observable<String>
        get() = dynamixDeviceHelper.deviceModel

    override val emailAddress: Observable<String>
        get() = dynamixDeviceHelper.emailAddress

    override val deviceId: Observable<String>
        get() = Observable.just(DynamixEnvironmentData.deviceId)

    override val deviceIdAsString: String
        get() = DynamixEnvironmentData.deviceId
}