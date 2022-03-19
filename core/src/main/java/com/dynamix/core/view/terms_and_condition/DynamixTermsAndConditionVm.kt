package com.dynamix.core.view.terms_and_condition

import com.dynamix.core.helper.DynamixBaseVm
import com.dynamix.core.logger.LoggerProviderUtils
import com.dynamix.core.network.ApiProvider
import com.dynamix.core.utils.DynamixSingleEvent
import com.dynamix.core.utils.DynamixSingleLiveEvent
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DynamixTermsAndConditionVm(private val apiProvider: ApiProvider) : DynamixBaseVm() {

    val termsAndConditionData: DynamixSingleLiveEvent<DynamixSingleEvent<String>> =
        DynamixSingleLiveEvent()
    val termsAndConditionFailure: DynamixSingleLiveEvent<DynamixSingleEvent<Boolean>> =
        DynamixSingleLiveEvent()

    fun loadHtmlData(api: String) {
        loading.value = true

        disposables.add(
            apiProvider.getUrl(api, JsonObject::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false

                    if (it.has("content") && it.get("content").asString.isNotEmpty()) {
                        termsAndConditionData.value = DynamixSingleEvent(it.get("content").asString)
                    } else {
                        termsAndConditionFailure.value = DynamixSingleEvent(true)
                    }
                }, {
                    LoggerProviderUtils.error(it)
                    loading.value = false
                    termsAndConditionFailure.value = DynamixSingleEvent(true)
                })
        )
    }
}