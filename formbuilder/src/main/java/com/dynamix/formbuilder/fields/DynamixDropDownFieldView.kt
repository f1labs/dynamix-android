package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.dynamix.core.extensions.selectedItemPosition
import com.dynamix.core.extensions.toAutoCompleteTextView
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixDropDownFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

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

    override fun render(field: DynamixFormField): View {
        val textInputLayout = TextInputLayout(ctx, null, R.attr.geFmDropdownStyle)
        val autoCompleteView = MaterialAutoCompleteTextView(textInputLayout.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            inputType = EditorInfo.TYPE_NULL
            textInputLayout.addView(this)
        }

        if (field.isHidden) {
            textInputLayout.isVisible = false
        }
        textInputLayout.tag = field.tag
        val options: MutableList<String> = ArrayList()
        if (field.placeholder.isNotEmpty()) {
            options.add(field.placeholder)
        }
        if (field.options != null) {
            val values: Collection<String> = field.options!!.values
            options.addAll(values)
        }
        val arrayAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            ctx, R.layout.dynamix_spinner_item, options
        ) {
            override fun isEnabled(position: Int): Boolean {
                return if (field.placeholder.isNotEmpty()) {
                    position != 0
                } else {
                    true
                }
            }
        }
        arrayAdapter.setDropDownViewResource(R.layout.dynamix_spinner_dropdown_item)
        autoCompleteView.setAdapter(arrayAdapter)
        if (options.isNotEmpty()) {
            autoCompleteView.setText(options[field.defaultItemPosition], false)
        }
        if (field.placeholder.isNotEmpty()) {
            autoCompleteView.setText(options[0], false)
        }
        if (field.defaultValue != null && field.defaultValue!!.isNotEmpty()) {
            val indexes: List<String?> = if (field.shouldSendValue) {
                ArrayList(field.options!!.values)
            } else {
                ArrayList(field.options!!.keys)
            }
            val index = indexes.indexOf(field.defaultValue)
            val adjustedIndex = index + if (field.placeholder.isNotEmpty()) 1 else 0
            autoCompleteView.setText(options[adjustedIndex])
        }
        val fieldView = DynamixFormFieldView()
        fieldView.view = textInputLayout
        fieldView.formField = field
        fieldView.fieldHandler = this
        fieldRenderer.formFieldList.add(fieldView)
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        return textInputLayout
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        val autoCompleteView =
            (formFieldView.view as TextInputLayout).editText as MaterialAutoCompleteTextView
        var key = ""
        if (formFieldView.formField.parentSpinner == null) {
            if (autoCompleteView.visibility == View.GONE) {
                return true
            } else {
                if (formFieldView.formField.placeholder.isNotEmpty()) {
                    if (autoCompleteView.selectedItemPosition() == 0) {
                        DynamixNotificationUtils.showErrorInfo(
                            ctx,
                            ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
                        )
                        return false
                    }
                }
                if (autoCompleteView.text.toString().isEmpty()) {
                    DynamixNotificationUtils.showErrorInfo(
                        ctx,
                        ctx.getString(R.string.dynamix_cr_your_action_could_not_be_completed)
                    )
                    return false
                } else {
                    key = DynamixCommonUtils.getKeyFromValueInHashMap(
                        formFieldView.formField.options,
                        autoCompleteView.text.toString()
                    )
                    if (formFieldView.formField.isRequired && key.isEmpty()) {
                        DynamixNotificationUtils.showErrorInfo(
                            ctx,
                            ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
                        )
                        return false
                    }
                }
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
                        parentPosition =
                            formFieldView1.view.toAutoCompleteTextView()
                                .selectedItemPosition()
                        break
                    }
                }
                key = DynamixCommonUtils.getKeyFromValueInHashMap(
                    formFieldView.formField.spinnerMultiItems!![parentPosition],
                    autoCompleteView.text.toString()
                )
                if (key.isEmpty()) {
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
        val autoCompleteView =
            (formFieldView.view as TextInputLayout).editText as AutoCompleteTextView
        val spinnerText: String?
        val shouldSendValue = formFieldView.formField.shouldSendValue
        if (formFieldView.formField.parentSpinner == null) {
            spinnerText = if (shouldSendValue) {
                autoCompleteView.text.toString()
            } else {
                DynamixCommonUtils.getKeyFromValueInHashMap(
                    formFieldView.formField.options,
                    autoCompleteView.text.toString()
                )
            }
        } else {
            var parentPosition = 0
            for (formFieldView1 in fieldRenderer.formFieldList) {
                if (formFieldView1.formField.tag.equals(
                        formFieldView.formField.parentSpinner,
                        ignoreCase = true
                    )
                ) {
                    parentPosition =
                        formFieldView1.view.toAutoCompleteTextView()
                            .selectedItemPosition()
                    break
                }
            }
            spinnerText = DynamixCommonUtils.getKeyFromValueInHashMap(
                formFieldView.formField
                    .spinnerMultiItems!![parentPosition],
                autoCompleteView.text.toString()
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
                spinnerText
            )
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(
                formFieldView.formField.tag!!,
                spinnerText
            )
        }
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            if (formFieldView.formField.confirmationLabel != null
                && !formFieldView.formField.confirmationLabel!!.isEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            autoCompleteView.text.toString()
                        )
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            autoCompleteView.text.toString()
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
                        autoCompleteView.text.toString()
                    )
                )
            } else {
                if (autoCompleteView.text.isEmpty()) {
                    listConfirmationData.add(
                        DynamixConfirmationModel(
                            formFieldView.formField.label,
                            ""
                        )
                    )
                } else {
                    listConfirmationData.add(
                        DynamixConfirmationModel(
                            formFieldView.formField.label,
                            autoCompleteView.text.toString()
                        )
                    )
                }

            }
        }
    }
}