package com.dynamix.modsign.view

import com.dynamix.core.event.DynamixEvent
import com.dynamix.modsign.model.LayoutWrapper
import io.reactivex.Observable

interface ModSignDataProvider {

    fun invalidateCacheIfRequired()

    fun loadParsedStyles(): Observable<Map<String, Any>>

    fun loadLayout(
        layoutUrl: String,
        event: DynamixEvent = DynamixEvent()
    ): Observable<LayoutWrapper>

    fun loadLayoutWithLayoutCode(
        layoutCode: String,
        event: DynamixEvent = DynamixEvent()
    ): Observable<LayoutWrapper>
}