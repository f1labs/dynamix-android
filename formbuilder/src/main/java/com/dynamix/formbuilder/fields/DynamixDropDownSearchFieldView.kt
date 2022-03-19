package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.dynamix.core.extensions.selectedItemPosition
import com.dynamix.core.extensions.toAutoCompleteTextView
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import com.dynamix.formbuilder.view.DynamixSpinnerSearchBottomSheet
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixDropDownSearchFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        val label: View? = DynamixLabelFieldView(
            ctx,
            fieldRenderer.formLabelList,
            fieldRenderer.formFieldViewMap
        ).render(field)
        val formView: View = render(field)
        return DynamixFormView(labelView = label, formView = formView)
    }

    private fun selectDataFromSpinner(tag: String?, array: Array<String>?) {
        val bottomSheet =
            DynamixSpinnerSearchBottomSheet(
                fieldRenderer.formFieldViewMap[tag]?.formField?.label!!,
                array!!
            ) {
                onDataSelectedFromSpinner(tag, it)
            }
        bottomSheet.showNow((ctx as AppCompatActivity).supportFragmentManager, "spinner")
    }

    private fun onDataSelectedFromSpinner(tag: String?, value: String?) {
        val formFieldView = fieldRenderer.formFieldViewMap[tag]
        if (formFieldView != null) {
            ((formFieldView.view as TextInputLayout).editText as AutoCompleteTextView).setText(value)
        }
    }

    override fun render(field: DynamixFormField): View {
        val textInputLayout = TextInputLayout(ctx, null, R.attr.geFmDropDownSearchStyle)
        val fieldView = DynamixFormFieldView()
        val textView = MaterialAutoCompleteTextView(textInputLayout.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            inputType = EditorInfo.TYPE_NULL
            textInputLayout.addView(this)
        }

        if (field.options == null || field.options!!.size == 1) {
            textInputLayout.endIconMode = TextInputLayout.END_ICON_NONE
        } else {
            textInputLayout.setEndIconOnClickListener {
                val values: Collection<String> = field.options!!.values
                val array = values.toTypedArray()
                if (array.size > 1) {
                    selectDataFromSpinner(field.tag, array)
                }
            }
        }
        if (field.isHidden) {
            textInputLayout.isVisible = false
        }
        textInputLayout.tag = field.tag
        if (field.defaultItemPosition > 0) {
            val values: Collection<String> = field.options!!.values
            val array = values.toTypedArray()
            textView.setText(array[field.defaultItemPosition - 1])
        }
        if (field.spinnerMultiItems == null || field.spinnerMultiItems!!.isEmpty()) {
            setDefaultValue(field, textView)
        }
        textView.setOnClickListener {
            val values: Collection<String> = field.options!!.values
            val array = values.toTypedArray()
            if (array.size > 1) {
                selectDataFromSpinner(field.tag, array)
            }
        }
        textView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                fieldRenderer.formDataProvider.dynamixSpinnerSearchTextChanged(
                    field.tag,
                    editable.toString()
                )
            }
        })
        fieldView.formField = field
        fieldView.view = textInputLayout
        fieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        fieldRenderer.formFieldList.add(fieldView)
        return textInputLayout
    }

    private fun setDefaultValue(field: DynamixFormField, textView: MaterialAutoCompleteTextView) {
        if (field.defaultValue != null && field.defaultValue!!.isNotEmpty()) {
            textView.setText(field.options!![field.defaultValue])
        } else {
            if (field.placeholder.isNotEmpty()) {
                textView.setText(field.placeholder)
            } else {
                if (!field.options.isNullOrEmpty()) {
                    if (field.options!!.containsKey(null)) {
                        textView.setText(field.options!![null])
                    } else {
                        val values: Collection<String> = field.options!!.values
                        textView.setText(values.first())
                    }
                }
            }
        }
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        val textView = (formFieldView.view as TextInputLayout).editText as AutoCompleteTextView
        var spinnerSearchKey = ""
        if (formFieldView.formField.parentSpinner == null) {
            spinnerSearchKey = DynamixCommonUtils.getKeyFromValueInHashMap(
                formFieldView.formField.options,
                textView.text.toString()
            )
            if (formFieldView.formField.isRequired && spinnerSearchKey.isEmpty()) {
                DynamixNotificationUtils.showErrorInfo(
                    ctx,
                    ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
                )
                return false
            }
        } else {
            if (formFieldView.formField.isRequired) {
                var parentPosition = 0
                for (formFieldView1 in fieldRenderer.formFieldList) {
                    if (formFieldView1.formField.tag.equals(
                            formFieldView.formField.parentSpinner,
                            ignoreCase = true
                        )
                    ) {
                        if (formFieldView1.formField.fieldType == DynamixFieldType.SPINNER.fieldType) {
                            parentPosition =
                                formFieldView1.view.toAutoCompleteTextView()
                                    .selectedItemPosition()
                        }
                        if (formFieldView1.formField.fieldType == DynamixFieldType.SPINNER_SEARCH.fieldType) {
                            val parentTextView =
                                formFieldView1.view.toAutoCompleteTextView()
                            val indexes: List<String> =
                                ArrayList(formFieldView1.formField.options!!.values)
                            parentPosition =
                                indexes.indexOf(parentTextView.text.toString())
                        }
                        break
                    }
                }
                spinnerSearchKey = DynamixCommonUtils.getKeyFromValueInHashMap(
                    formFieldView.formField.spinnerMultiItems!![parentPosition],
                    textView.text.toString()
                )
                if (spinnerSearchKey.isEmpty()) {
                    DynamixNotificationUtils.showErrorInfo(
                        ctx,
                        ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
                    )
                    return false
                }
            }
        }
        return true
    }

    override fun requestParams(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {
        val appCompatTextView =
            (formFieldView.view as TextInputLayout).editText as AutoCompleteTextView
        val spinnerSearchText: String?
        if (formFieldView.formField.parentSpinner == null) {
            spinnerSearchText = DynamixCommonUtils.getKeyFromValueInHashMap(
                formFieldView.formField.options,
                appCompatTextView.text.toString()
            )
        } else {
            var parentPosition = 0
            for (formFieldView1 in fieldRenderer.formFieldList) {
                if (formFieldView1.formField.tag.equals(
                        formFieldView.formField.parentSpinner,
                        ignoreCase = true
                    )
                ) {
                    if (formFieldView1.formField.fieldType == DynamixFieldType.SPINNER.fieldType) {
                        parentPosition =
                            formFieldView1.view.toAutoCompleteTextView()
                                .selectedItemPosition()
                    }
                    if (formFieldView1.formField.fieldType == DynamixFieldType.SPINNER_SEARCH.fieldType) {
                        val parentTextView =
                            formFieldView1.view as AppCompatTextView
                        val indexes: List<String> =
                            ArrayList(formFieldView1.formField.options!!.values)
                        parentPosition =
                            indexes.indexOf(parentTextView.text.toString())
                    }
                    break
                }
            }
            spinnerSearchText = DynamixCommonUtils.getKeyFromValueInHashMap(
                formFieldView.formField
                    .spinnerMultiItems!![parentPosition],
                appCompatTextView.text.toString()
            )
        }
        if (DynamixCommonUtils.isNumeric(formFieldView.formField.tag!!)) {
            merchantRequest.put(
                DynamixFormFieldConstants.PARAM_ORDER,
                formFieldView.formField.tag
            )
            merchantRequest.put(
                DynamixFormFieldConstants.LABEL,
                formFieldView.formField.label
            )
            merchantRequest.put(
                DynamixFormFieldConstants.PARAM_VALUE,
                spinnerSearchText
            )
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(
                formFieldView.formField.tag!!,
                spinnerSearchText
            )
        }
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            if (formFieldView.formField.confirmationLabel != null
                && formFieldView.formField.confirmationLabel!!.isNotEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            appCompatTextView.text.toString()
                        )
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            appCompatTextView.text.toString()
                        )
                    )
                )
            }
        } else {
            if (formFieldView.formField.confirmationLabel != null
                && formFieldView.formField.confirmationLabel!!.isNotEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        appCompatTextView.text.toString()
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        appCompatTextView.text.toString()
                    )
                )
            }
        }
    }
}