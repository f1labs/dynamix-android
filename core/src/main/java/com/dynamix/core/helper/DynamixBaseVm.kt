package com.dynamix.core.helper

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class DynamixBaseVm : ViewModel(), LifecycleObserver {

    val loading = MutableLiveData<Boolean>()

    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}