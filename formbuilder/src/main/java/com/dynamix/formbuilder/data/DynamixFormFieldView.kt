package com.dynamix.formbuilder.data

import android.view.View
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import com.dynamix.formbuilder.fields.DynamixFormDataHandler
import com.google.android.material.chip.Chip

// TODO(migrate to data class)
class DynamixFormFieldView {
    lateinit var formField: DynamixFormField
    lateinit var view: View
    var radioButtons: List<AppCompatRadioButton>? = null
    var checkBoxes: List<AppCompatCheckBox>? = null
    var chips: List<Chip>? = null
    var fieldHandler: DynamixFormDataHandler? = null
}