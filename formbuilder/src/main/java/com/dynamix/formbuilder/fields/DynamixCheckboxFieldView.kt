package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.isVisible
import com.dynamix.formbuilder.R
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import com.google.android.material.checkbox.MaterialCheckBox
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixCheckboxFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    fun checkChangeCheckListener(
        key: String,
        checked: Boolean
    ) {
        for (formFieldView in fieldRenderer.formFieldList) {
            if (formFieldView.formField.visibilityParent != null && formFieldView.formField.visibilityParent == key) {
                for (labelFormField in fieldRenderer.formLabelList) {
                    if (labelFormField.formField.visibilityParent != null && labelFormField.formField.visibilityParent == key) {
                        val label = labelFormField.view
                        if (checked) {
                            if (label != null) {
                                label.isVisible = true
                            }
                        } else {
                            if (label != null) {
                                label.isVisible = false
                            }
                        }
                    }
                }
                if (checked) {
                    formFieldView.view.isVisible = true
                    formFieldView.formField.isIgnoreField = false
                } else {
                    formFieldView.view.isVisible = false
                    formFieldView.formField.isIgnoreField = true
                }
            }
        }
    }

    override fun render(field: DynamixFormField): View {
        val checkBoxes = ArrayList<AppCompatCheckBox>()
        val linearLayout = LinearLayout(ctx)
        if (field.isOrientationHorizontal) {
            linearLayout.orientation =
                LinearLayout.HORIZONTAL
        } else {
            linearLayout.orientation = LinearLayout.VERTICAL
        }
        if (field.options != null && field.options!!.isNotEmpty()) {
            for ((key, value) in field.options!!) {
                val checkBox = MaterialCheckBox(ctx, null, R.attr.geFmCheckBoxStyle)
                val checkBoxParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                checkBox.layoutParams = checkBoxParams
                checkBox.text = value
                checkBox.tag = key
                linearLayout.addView(checkBox)
                checkBoxes.add(checkBox)
                checkBox.setOnCheckedChangeListener { _, b ->
                    checkChangeCheckListener(key!!, b)
                    fieldRenderer.formDataProvider.dynamixCheckChangeListener(key, b)
                }
            }
        }
        val fieldView = DynamixFormFieldView()
        fieldView.checkBoxes = checkBoxes
        fieldView.formField = field
        fieldView.view = linearLayout
        fieldView.fieldHandler = this
        fieldRenderer.formFieldList.add(fieldView)
        return linearLayout
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        val checkboxes = formFieldView.checkBoxes
        if (formFieldView.formField.isRequired) {
            var isSelected = false
            for (checkBox in checkboxes!!) {
                if (checkBox.isChecked) {
                    isSelected = true
                    break
                }
            }
            if (!isSelected) {
                DynamixNotificationUtils.showErrorInfo(
                    ctx,
                    ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
                )
                return false
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
        val checkBoxes = formFieldView.checkBoxes
        var checkboxKey = ctx.getString(R.string.dynamix_cr_n)
        for (checkBox in checkBoxes!!) {
            if (checkBox.isChecked) {
                checkboxKey = checkBox.tag.toString()
                break
            }
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
                checkboxKey
            )
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(formFieldView.formField.tag!!, checkboxKey)
        }
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            listConfirmationData.add(
                DynamixConfirmationModel(
                    formFieldView.formField.label,
                    DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                        checkboxKey
                    )
                )
            )
        } else {
            listConfirmationData.add(
                DynamixConfirmationModel(
                    formFieldView.formField.label, checkboxKey
                )
            )
        }
    }
}