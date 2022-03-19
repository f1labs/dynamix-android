package com.dynamix.formbuilder.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by user-pc-suman
 */
@Parcelize
data class DynamixConfirmationModel(
    val title: String = "",
    val description: String = "",
) : Parcelable