package com.dynamix.modsign.view

import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.helper.DynamixBaseVm
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.utils.DynamixSingleEvent
import com.dynamix.core.utils.DynamixSingleLiveEvent
import com.dynamix.modsign.model.LayoutWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ModSignVm(
    private val modSignDataProvider: ModSignDataProvider,
    private val appLoggerProvider: AppLoggerProvider
) : DynamixBaseVm() {

    val layoutLoadingSuccess: DynamixSingleLiveEvent<DynamixSingleEvent<LayoutWrapper>> =
        DynamixSingleLiveEvent()
    val layoutLoadingFailure: DynamixSingleLiveEvent<DynamixSingleEvent<Boolean>> =
        DynamixSingleLiveEvent()

    fun loadStyles(styleDataLoaded: (Map<String, Any>) -> Unit) {
        disposables.add(
            modSignDataProvider.loadParsedStyles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    styleDataLoaded(it)
                }, {
                    appLoggerProvider.error(it)
                })
        )
    }

    fun loadLayout(layoutUrl: String, event: DynamixEvent = DynamixEvent()) {
        loading.value = true

        disposables.add(
            modSignDataProvider.loadLayout(layoutUrl, event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false

                    if (it.layout != null) {
                        layoutLoadingSuccess.value = DynamixSingleEvent(it)
                    } else {
                        layoutLoadingFailure.value = DynamixSingleEvent(true)
                    }
                }, {
                    appLoggerProvider.error(it)

                    loading.value = false
                    layoutLoadingFailure.value = DynamixSingleEvent(true)
                })
        )
    }


    fun loadLayoutWithLayoutCode(layoutCode: String, event: DynamixEvent = DynamixEvent()) {
        loading.value = true

        disposables.add(
            modSignDataProvider.loadLayoutWithLayoutCode(layoutCode, event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false

                    if (it.layout != null) {
                        layoutLoadingSuccess.value = DynamixSingleEvent(it)
                    } else {
                        layoutLoadingFailure.value = DynamixSingleEvent(true)
                    }
                }, {
                    appLoggerProvider.error(it)

                    loading.value = false
                    layoutLoadingFailure.value = DynamixSingleEvent(true)
                })
        )
    }
}