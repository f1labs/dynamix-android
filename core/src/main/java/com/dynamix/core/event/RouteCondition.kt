package com.dynamix.core.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RouteCondition(
    val key: String = "",
    val value: String = "",
    val routeCode: String? = ""
) : Parcelable