package com.dynamix.formbuilder.data

object DynamixFormFieldUtils {
    fun getDateTextFieldChildTag(field: DynamixFormField, childTag: String): String {
        return field.tag + childTag
    }
}