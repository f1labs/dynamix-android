package com.dynamix.formbuilder.validations

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import com.dynamix.core.locale.DynamixLocaleStrings
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixCommonUtils.decimalLimiter
import com.dynamix.core.view.DynamixNoChangingBackgroundTextInputLayout
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.google.android.material.textfield.TextInputLayout
import org.apache.commons.lang3.StringUtils

object DynamixFormBuilderValidationUtils {

    fun isAmountField(fieldView: DynamixFormFieldView): Boolean {
        return fieldView.formField.tag.equals("amount", ignoreCase = true) ||
                fieldView.formField.tag.equals("0", ignoreCase = true)
    }

    fun realTimeValidateField(fieldView: DynamixFormFieldView) {
        val textInputLayout = fieldView.view as DynamixNoChangingBackgroundTextInputLayout
        val editText = textInputLayout.editText!!
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateRequiredField(fieldView, false)
                if (fieldView.formField.maxDecimalDigits != null) {
                    if (fieldView.formField.tag.equals("amount", ignoreCase = true) ||
                        fieldView.formField.tag.equals("0", ignoreCase = true)
                    ) {
                        decimalDigitsValidation(editText, fieldView)
                    }
                }
            }
        })
        editText.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                isFormFieldValid(fieldView, false)
            } else {
                if (fieldView.formField.placeholder.isNotEmpty()
                ) {
                    editText.hint = fieldView.formField.placeholder
                } else {
                    editText.hint = ""
                }
            }
        }
    }

    fun isFormFieldValid(fieldView: DynamixFormFieldView, focusable: Boolean): Boolean {
        return validateRequiredField(fieldView, focusable) &&
                validateRange(fieldView, focusable) &&
                validateRegex(fieldView, focusable)
    }

    private fun validateRequiredField(
        fieldView: DynamixFormFieldView,
        focusable: Boolean
    ): Boolean {
        val textInputLayout = fieldView.view as TextInputLayout
        val editText = textInputLayout.editText ?: return false
        if (fieldView.formField.isRequired) {
            return if (editText.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                true
            } else {
                if (DynamixEnvironmentData.localeEnabled &&
                    DynamixEnvironmentData.locale.equals(DynamixLocaleStrings.NEPALI, true)
                ) {
                    textInputLayout.error = fieldView.formField.label +
                            " आवश्यक छ"
                } else {
                    textInputLayout.error =
                        StringUtils.capitalize(fieldView.formField.label.lowercase()) +
                                " is required"
                }
                if (focusable) textInputLayout.requestFocus()
                false
            }
        }
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
        return true
    }

    private fun validateRegex(fieldView: DynamixFormFieldView, focusable: Boolean): Boolean {
        val textInputLayout = fieldView.view as TextInputLayout
        val editText = textInputLayout.editText ?: return false
        if (fieldView.formField.pattern != null &&
            !fieldView.formField.pattern.equals("", ignoreCase = true)
        ) {
            return if (DynamixCommonUtils.validateRegex(
                    fieldView.formField.pattern!!,
                    editText.text.toString()
                )
            ) {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                true
            } else {
                if (fieldView.formField.validatorMessage == null ||
                    fieldView.formField.validatorMessage.equals("", ignoreCase = true)
                ) {
                    if (DynamixEnvironmentData.localeEnabled &&
                        DynamixEnvironmentData.locale.equals(DynamixLocaleStrings.NEPALI, true)
                    ) {
                        textInputLayout.error = fieldView.formField.label + " मिलेन"
                    } else {
                        textInputLayout.error = "Invalid " +
                                StringUtils.capitalize(fieldView.formField.label!!.lowercase())
                    }
                } else {
                    textInputLayout.error = fieldView.formField.validatorMessage
                }
                if (focusable) textInputLayout.requestFocus()
                false
            }
        }
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
        return true
    }

    private fun validateRange(fieldView: DynamixFormFieldView, focusable: Boolean): Boolean {
        if (fieldView.formField.fieldType != DynamixFieldType.NUMBER.fieldType) return true
        val textInputLayout = fieldView.view as TextInputLayout
        val editText = textInputLayout.editText ?: return false
        if (fieldView.formField.minValue > 0) {
            if (editText.text.toString().toDouble() <
                fieldView.formField.minValue
            ) {
                if (fieldView.formField.validatorMessage != null &&
                    !TextUtils.isEmpty(fieldView.formField.validatorMessage)
                ) {
                    textInputLayout.error = fieldView.formField.validatorMessage
                } else {
                    textInputLayout.error =
                        StringUtils.capitalize(fieldView.formField.label.lowercase()) +
                                " cannot be less than " + fieldView.formField.minValue
                }
                if (focusable) textInputLayout.requestFocus()
                return false
            } else {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
            }
        }
        if (fieldView.formField.maxValue > 0) {
            return if (editText.text.toString().toDouble() >
                fieldView.formField.maxValue
            ) {
                if (fieldView.formField.validatorMessage != null &&
                    !TextUtils.isEmpty(fieldView.formField.validatorMessage)
                ) {
                    textInputLayout.error = fieldView.formField.validatorMessage
                } else {
                    textInputLayout.error = fieldView.formField.label +
                            " cannot be greater than " + fieldView.formField.maxValue
                }
                if (focusable) textInputLayout.requestFocus()
                false
            } else {
                textInputLayout.error = null
                textInputLayout.isErrorEnabled = false
                true
            }
        }
        textInputLayout.error = null
        textInputLayout.isErrorEnabled = false
        return true
    }

    private fun decimalDigitsValidation(editText: EditText, fieldView: DynamixFormFieldView) {
        val str = editText.text!!.toString()
        if (str.isEmpty()) return
        val str2 = decimalLimiter(str, fieldView.formField.maxDecimalDigits!!)
        if (str2 != str) {
            editText.setText(str2)
            val pos = editText.text!!.length
            editText.setSelection(pos)
        }
    }
}