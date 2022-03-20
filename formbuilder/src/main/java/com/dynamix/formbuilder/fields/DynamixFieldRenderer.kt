package com.dynamix.formbuilder.fields

import android.view.View
import com.dynamix.formbuilder.data.DynamixFormField

/**
 * Created by user on 16/12/2021.
 */
interface DynamixFieldRenderer {

    fun render(field: DynamixFormField): View?
}