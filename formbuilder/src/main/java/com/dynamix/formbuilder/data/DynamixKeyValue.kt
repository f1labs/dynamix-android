package com.dynamix.formbuilder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamixKeyValue(
    val key: String,
    val value: String
) : Parcelable