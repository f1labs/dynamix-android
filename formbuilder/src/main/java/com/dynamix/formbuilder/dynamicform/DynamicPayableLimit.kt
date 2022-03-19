package com.dynamix.formbuilder.dynamicform

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DynamicPayableLimit(
    val maxAmount: Double = 0.0,
    val minAmount: Double = 0.0,
    val fixedAmount: Double = 0.0,
) : Parcelable