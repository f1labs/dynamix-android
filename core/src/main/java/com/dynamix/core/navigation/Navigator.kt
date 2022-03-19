package com.dynamix.core.navigation

import android.os.Parcelable
import com.dynamix.core.event.DynamixEvent
import kotlinx.parcelize.Parcelize

@Parcelize
data class Navigator(
    val code: String = "",
    val name: String = "",
    val type: String = "",
    val navLink: String = "",
    val requestCode: Int = -1,
    val event: DynamixEvent? = null,
    val storageId: String = "",

) : Parcelable

object NavigationType {

    const val WEB_VIEW = "WV"
    const val MODSIGN = "MODSIGN"
}