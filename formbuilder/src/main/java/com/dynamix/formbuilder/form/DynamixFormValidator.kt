package com.dynamix.formbuilder.form

import com.dynamix.formbuilder.data.DynamixFormFieldView

class DynamixFormValidator(
    private val formFieldList: MutableList<DynamixFormFieldView>,
    private val formDataProvider: DynamixFormDataProvider
) {

    fun validateFields() {
        formFieldList.forEach {
            if (it.fieldHandler != null) {
                if (!it.fieldHandler!!.validate(it)) {
                    return
                }
            }
        }
        formDataProvider.dynamixOnFormFieldsValidated()
    }
}