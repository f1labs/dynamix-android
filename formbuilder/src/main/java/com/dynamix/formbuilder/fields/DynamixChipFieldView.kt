package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixCommonUtils.getColorFromAttribute
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixChipFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    private val chipUnSelectedStrokeWidth = 1
    private val chipSelectedStrokeWidth = 0

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
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

    private fun onChipSelected(formField: DynamixFormField, chipTag: Any) {
        val selectedChipTag = chipTag.toString()
        for (formFieldView in fieldRenderer.formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.CHIP.fieldType &&
                formFieldView.formField.tag.equals(formField.tag, ignoreCase = true)
            ) {
                val chipList = formFieldView.chips
                for (chip in chipList!!) {
                    if (chip.tag == selectedChipTag) {
                        selectedChip(chip)
                        fieldRenderer.chipTexts[formField.tag!!] = chip.tag.toString()
                    } else {
                        unSelectedChip(chip)
                    }
                }
            }
        }
    }

    private fun selectedChip(chipShape: Chip) {
        chipShape.setTextColor(ContextCompat.getColor(ctx, R.color.color_ffffff))
        chipShape.chipBackgroundColor = ColorStateList.valueOf(
            DynamixCommonUtils.getColorFromAttribute(
                ctx,
                R.attr.formWidgetColor
            )
        )
        chipShape.chipStrokeWidth =
            dpToPx(ctx, chipSelectedStrokeWidth.toFloat()).toFloat()
        chipShape.chipStrokeColor = ColorStateList.valueOf(
            getColorFromAttribute(
                ctx,
                R.attr.formWidgetColor
            )
        )
    }

    private fun unSelectedChip(chipShape: Chip) {
        chipShape.setTextColor(getColorFromAttribute(ctx, R.attr.formWidgetColor))
        chipShape.chipBackgroundColor =
            ColorStateList.valueOf(ContextCompat.getColor(ctx, android.R.color.transparent))
        chipShape.chipStrokeWidth = dpToPx(ctx, chipUnSelectedStrokeWidth.toFloat()).toFloat()
        chipShape.chipStrokeColor = ColorStateList.valueOf(
            getColorFromAttribute(
                ctx,
                R.attr.formWidgetColor
            )
        )
    }

    override fun render(field: DynamixFormField): View {
        if (field.defaultItemPosition < 1 || field.defaultItemPosition > field.options!!.size) {
            field.defaultItemPosition = 1
        }
        val chipGroup = ChipGroup(ctx)
        val chipList: MutableList<Chip> = ArrayList()
        if (field.options != null && field.options!!.isNotEmpty()) {
            for ((key, value) in field.options!!) {
                val chipShape = Chip(ctx, null, R.attr.geFmChipStyle)
                chipShape.text = value
                chipShape.tag = key
                chipGroup.addView(chipShape)
                chipList.add(chipShape)
                chipShape.setOnClickListener {
                    onChipSelected(
                        field,
                        chipShape.tag
                    )
                }
            }
        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = chipGroup
        formFieldView.chips = chipList
        formFieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        fieldRenderer.formFieldList.add(formFieldView)
        val keySet: List<String?> = ArrayList(field.options!!.keys)
        val defaultTag = keySet[field.defaultItemPosition - 1]!!
        onChipSelected(field, defaultTag)
        return chipGroup
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        if (formFieldView.formField.isRequired) {
            if (!fieldRenderer.chipTexts.containsKey(formFieldView.formField.tag)) {
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
        if (fieldRenderer.chipTexts.containsKey(formFieldView.formField.tag)) {
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
                    fieldRenderer.chipTexts[formFieldView.formField.tag]
                )
                merchantRequestParams.put(merchantRequest)
            } else {
                requestParams.put(
                    formFieldView.formField.tag!!,
                    fieldRenderer.chipTexts[formFieldView.formField.tag]
                )
            }
            if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            fieldRenderer.chipTexts[formFieldView.formField.tag]
                        )
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        fieldRenderer.chipTexts[formFieldView.formField.tag]!!
                    )
                )
            }
        }
    }
}