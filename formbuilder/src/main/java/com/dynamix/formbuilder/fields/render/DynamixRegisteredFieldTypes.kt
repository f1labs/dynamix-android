package com.dynamix.formbuilder.fields.render

import androidx.collection.SimpleArrayMap
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.fields.*

class DynamixRegisteredFieldTypes {

    fun fieldTypes(): SimpleArrayMap<String, DynamixFormDataHandler> {
        val formFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()

        formFieldTypes.apply {
            put(DynamixFieldType.TEXT_FIELD.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.NUMBER.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.HIDDEN.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.NUMBER_PASSWORD.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.TEXT_PASSWORD.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.TEXTAREA.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.ACCOUNT.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.DATE.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.TIME.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.PHONE.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.PHONE_EMAIL.fieldType, DynamixTextInputFieldView())
            put(DynamixFieldType.SPINNER.fieldType, DynamixDropDownFieldView())
            put(DynamixFieldType.SPINNER_SEARCH.fieldType, DynamixDropDownSearchFieldView())
            put(DynamixFieldType.CHECKBOX.fieldType, DynamixCheckboxFieldView())
            put(DynamixFieldType.RADIO.fieldType, DynamixRadioFieldView())
            put(DynamixFieldType.LABEL_VALUE.fieldType, DynamixLabelValueFieldView())
            put(DynamixFieldType.TXN_LIMIT.fieldType, DynamixTxnLimitFieldView())
            put(DynamixFieldType.DATE_RANGE.fieldType, DynamixDateRangeFieldView())
            put(DynamixFieldType.TEXT_DATA.fieldType, DynamixTextDataFieldView())
            put(DynamixFieldType.OPTIONS.fieldType, DynamixOptionsFieldView())
            put(DynamixFieldType.CHIP.fieldType, DynamixChipFieldView())
            put(DynamixFieldType.IMAGE.fieldType, DynamixImageFieldView())
            put(DynamixFieldType.AMOUNT.fieldType, DynamixAmountFieldView())
            put(DynamixFieldType.AMOUNT_SPINNER.fieldType, DynamixAmountDropDownFieldView())
            put(DynamixFieldType.EMPTY.fieldType, DynamixEmptyFieldView())
            put(DynamixFieldType.BUTTON.fieldType, DynamixButtonFieldView())
            put(DynamixFieldType.FIELD_LIMIT.fieldType, DynamixFieldLimitFieldView())
            put(DynamixFieldType.DATE_TEXT_FIELD.fieldType, DynamixDateTextFieldView())
            put(DynamixFieldType.DIVIDER.fieldType, DynamixDividerFieldView())
            put(DynamixFieldType.LABEL.fieldType, DynamixCustomLabelFieldView())
            put(DynamixFieldType.IMAGE_PREVIEW.fieldType, DynamixImagePreviewFieldView())
            put(DynamixFieldType.LABEL_IMAGE.fieldType, DynamixLabelImageFieldView())
            put(DynamixFieldType.NOTES.fieldType, DynamixNotesFieldView())
            put(DynamixFieldType.ICON_LABEL.fieldType, DynamixIconWithTextFieldView())
            put(DynamixFieldType.WEB_DATA.fieldType, DynamixWebDataFieldView())
        }
        return formFieldTypes
    }

    fun prefixFieldTypes(): SimpleArrayMap<String, DynamixFormDataHandler> {
        val prefixFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()

        prefixFieldTypes.apply {

        }
        return prefixFieldTypes
    }

    fun postfixFieldTypes(): SimpleArrayMap<String, DynamixFormDataHandler> {
        val postFixFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()

        postFixFieldTypes.apply {
            put(DynamixFieldType.NOTES.fieldType, DynamixNotesFieldView())
            put(DynamixFieldType.ICON_LABEL.fieldType, DynamixIconWithTextFieldView())
            put(DynamixFieldType.WEB_DATA.fieldType, DynamixWebDataFieldView())
        }
        return postFixFieldTypes
    }
}