package com.dynamix.formbuilder.fields

import android.content.Context
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder

abstract class DynamixBaseFieldView {

    lateinit var ctx: Context
    lateinit var fieldRenderer: DynamixFieldDataHolder
}