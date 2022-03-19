package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
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
import com.google.android.material.radiobutton.MaterialRadioButton
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixRadioFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return super.init(ctx, fieldRenderer)
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(
            labelView = DynamixLabelFieldView(
                ctx,
                fieldRenderer.formLabelList,
                fieldRenderer.formFieldViewMap
            ).render(field),
            formView = render(field)
        )
    }

    private fun onRadioChangedListener(
        formFieldViewMap: MutableMap<String, DynamixFormFieldView>,
        formFieldList: MutableList<DynamixFormFieldView>,
        formField: DynamixFormField,
        tag: String?
    ) {
        val formFieldView = formFieldViewMap[formField.tag]
        if (formFieldView?.radioButtons != null) {
            for (inputRadioButton in formFieldView.radioButtons!!) {
                for (formFieldView1 in formFieldList) {
                    if (formFieldView1.formField.visibilityParent != null &&
                        formFieldView1.formField.visibilityParent.equals(
                            tag,
                            ignoreCase = true
                        ) && formFieldView1.formField.visibilityValues != null &&
                        formFieldView1.formField.visibilityValues!!.isNotEmpty()
                    ) {
                        for (visibilityValue in formFieldView1.formField.visibilityValues!!) {
                            if (visibilityValue == inputRadioButton.tag) {
                                if (inputRadioButton.isChecked) {
                                    formFieldView1.view.isVisible = true
                                    formFieldView1.formField.isIgnoreField = false
                                    if (formFieldView1.formField.isRequired) {
                                        formFieldView1.formField.isValidateIgnoreField = true
                                    }
                                    val view =
                                        formFieldViewMap[formFieldView1.formField.tag + "__Label"]!!
                                            .view
                                    view.isVisible = true
                                } else {
                                    if (formFieldView1.formField.visibilityParent != null) {
                                        formFieldView1.view.isVisible = false
                                        formFieldView1.formField.isIgnoreField = true
                                        if (formFieldView1.formField.isRequired) {
                                            formFieldView1.formField.isValidateIgnoreField =
                                                false
                                        }
                                        val view =
                                            formFieldViewMap[formFieldView1.formField.tag + "__Label"]!!
                                                .view
                                        view.isVisible = false
                                    }
                                }
                            }
                        }
                    }
                }
                fieldRenderer.formDataProvider.dynamixManageFieldVisibility(
                    inputRadioButton,
                    formFieldList,
                    tag
                )
            }
        }
    }


    override fun render(field: DynamixFormField): View {
        if (field.defaultItemPosition < 1 || field.defaultItemPosition > field.options!!.size) {
            field.defaultItemPosition = 1
        }
        val radios = ArrayList<AppCompatRadioButton>()
        val radioGroup = RadioGroup(ctx)
        if (field.isOrientationHorizontal) {
            radioGroup.orientation =
                LinearLayout.HORIZONTAL
        } else {
            radioGroup.orientation = LinearLayout.VERTICAL
        }
        if (!(field.options == null || field.options!!.isEmpty())) {
            for ((key, value) in field.options!!) {
                val radioButton =
                    MaterialRadioButton(ctx, null, R.attr.geFmRadioStyle)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                radioButton.layoutParams = params
                radioButton.text = value
                radioButton.tag = key
                radioButton.setOnCheckedChangeListener { _, _ ->
                    onRadioChangedListener(
                        fieldRenderer.formFieldViewMap,
                        fieldRenderer.formFieldList,
                        field,
                        field.tag.toString()
                    )
                }
                radioGroup.addView(radioButton)
                radios.add(radioButton)
            }
        }
        if (field.defaultItemPosition > 0) {
            (radioGroup.getChildAt(field.defaultItemPosition - 1)!! as AppCompatRadioButton).isChecked =
                true
        }
        if (field.defaultValue != null && field.defaultValue!!.isNotEmpty()) {
            val keySet: List<String?> = ArrayList(field.options!!.keys)
            val defaultValuePosition = keySet.indexOf(field.defaultValue)
            (radioGroup.getChildAt(defaultValuePosition) as AppCompatRadioButton).isChecked = true
        }
        val fieldView = DynamixFormFieldView()
        fieldView.radioButtons = radios
        fieldView.formField = field
        fieldView.view = radioGroup
        fieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        fieldRenderer.formFieldList.add(fieldView)
        return radioGroup
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        val radioButtons = formFieldView.radioButtons
        try {
            if (formFieldView.formField.isRequired) {
                var isSelected = false
                for (radioButton in radioButtons!!) {
                    if (radioButton.isChecked) {
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
        } catch (e: Exception) {
            fieldRenderer.appLoggerProvider.info(e.localizedMessage)
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
        val radioButtons = formFieldView.radioButtons
        var radioText = ""
        for (radioButton in radioButtons!!) {
            if (radioButton.isChecked) {
                radioText = radioButton.text.toString()
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
                radioText
            )
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(formFieldView.formField.tag!!, radioText)
        }
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            listConfirmationData.add(
                DynamixConfirmationModel(
                    formFieldView.formField.label,
                    DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                        radioText
                    )
                )
            )
        } else {
            listConfirmationData.add(
                DynamixConfirmationModel(
                    formFieldView.formField.label, radioText
                )
            )
        }
    }
}