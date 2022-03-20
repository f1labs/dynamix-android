package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatSpinner
import com.dynamix.core.extensions.dp
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.core.utils.date.DynamixDateUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldUtils.getDateTextFieldChildTag
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.google.android.material.textfield.TextInputLayout
import com.hornet.dateconverter.DateConverter
import com.hornet.dateconverter.Model
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.*

/**
 * Created by user on 16/12/2021.
 */
class DynamixDateTextFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

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

    private fun getDateTextFieldView(dateDay: DynamixFormField): LinearLayout {
        val layout = LinearLayout(ctx)
        layout.orientation = LinearLayout.VERTICAL
        val view: View = DynamixTextInputFieldView().init(ctx, fieldRenderer).render(dateDay)!!
        val label = DynamixLabelFieldView(
            ctx,
            fieldRenderer.formLabelList,
            fieldRenderer.formFieldViewMap
        ).render(dateDay)
        layout.addView(label)
        layout.addView(view)
        val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        layoutParams.setMargins(DynamixConverter.dpToPx(ctx, 8), 0, 0, 0)
        layout.layoutParams = layoutParams
        return layout
    }

    override fun render(field: DynamixFormField): View {
        val rootLayout = LinearLayout(ctx)
        rootLayout.gravity = Gravity.BOTTOM
        rootLayout.orientation = LinearLayout.HORIZONTAL
        var localDate: LocalDate? = null
        if (field.defaultValue != null) {
            localDate = LocalDate.parse(
                field.defaultValue, DateTimeFormatter.ofPattern(
                    DynamixDateUtils.dateFormat3
                )
            )
        }
        val dateDay = DynamixFormField()
        dateDay.label = ctx.getString(R.string.dynamix_label_day)
        dateDay.placeholder = ctx.getString(R.string.dynamix_label_dd)
        dateDay.maxLength = 2
        dateDay.visibilityTabParent = field.visibilityTabParent
        dateDay.tag =
            getDateTextFieldChildTag(field, DynamixFormFieldConstants.FORM_FIELD_DATE_DAY)
        if (localDate != null) {
            dateDay.defaultValue = localDate.dayOfMonth.toString()
        }
        dateDay.isIgnoreField = true
        val dateMonth = DynamixFormField()
        dateMonth.label = ctx.getString(R.string.dynamix_label_month)
        dateMonth.placeholder = ctx.getString(R.string.dynamix_label_mm)
        dateMonth.maxLength = 2
        dateMonth.visibilityTabParent = field.visibilityTabParent
        dateMonth.tag =
            getDateTextFieldChildTag(
                field,
                DynamixFormFieldConstants.FORM_FIELD_DATE_MONTH
            )
        if (localDate != null) {
            dateMonth.defaultValue = localDate.month.value.toString()
        }
        dateMonth.isIgnoreField = true
        val dateYear = DynamixFormField()
        dateYear.label = ctx.getString(R.string.dynamix_label_year)
        dateYear.visibilityTabParent = field.visibilityTabParent
        dateYear.placeholder = ctx.getString(R.string.dyamix_label_yyyy)
        dateYear.maxLength = 4
        dateYear.tag =
            getDateTextFieldChildTag(field, DynamixFormFieldConstants.FORM_FIELD_DATE_YEAR)
        if (localDate != null) {
            dateYear.defaultValue = localDate.year.toString()
        }
        dateYear.isIgnoreField = true
        val dateType = DynamixFormField()
        dateType.isIgnoreField = true
        dateType.fieldType = DynamixFieldType.SPINNER.fieldType
        dateType.visibilityTabParent = field.visibilityTabParent
        dateType.tag =
            getDateTextFieldChildTag(field, DynamixFormFieldConstants.FORM_FIELD_DATE_TYPE)
        val dateTypeOptions: MutableMap<String, String> = LinkedHashMap()
        dateTypeOptions["AD"] = ctx.getString(R.string.dynamix_label_ad)
        dateTypeOptions["BS"] = ctx.getString(R.string.dynamix_label_bs)
        dateType.options = dateTypeOptions.toMap()
        dateType.shouldSendValue = true
        // TODO(This is causing some issues)
//        dateType.defaultValue = field.containerType

        // inflate the fields
        val dateLinearLayout = getDateTextFieldView(dateDay)
        val dateLayoutParams = dateLinearLayout.layoutParams as LinearLayout.LayoutParams
        dateLayoutParams.setMargins(0, 0, 0, 0)
        dateLinearLayout.layoutParams = dateLayoutParams
        rootLayout.addView(dateLinearLayout)
        rootLayout.addView(getDateTextFieldView(dateMonth))
        rootLayout.addView(getDateTextFieldView(dateYear))
        val spinnerView =
            DynamixDropDownFieldView().init(ctx, fieldRenderer).render(dateType) as TextInputLayout
        val spinnerLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        spinnerLayoutParams.width = 0
        spinnerLayoutParams.weight = 1f
        spinnerLayoutParams.setMargins(8.dp(ctx), 0, 0, 0)
        rootLayout.addView(spinnerView, spinnerLayoutParams)
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = rootLayout
        formFieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        fieldRenderer.formFieldList.add(formFieldView)
        return rootLayout
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        if (!formFieldView.formField.isIgnoreField) {
            // validate the conversion
            val field = formFieldView.formField
            try {
                //  get all the fields and validate them
                val day =
                    (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                        field,
                        DynamixFormFieldConstants.FORM_FIELD_DATE_DAY
                    )]!!
                        .view as TextInputLayout).editText!!.text.toString().toInt()
                val month =
                    (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                        field,
                        DynamixFormFieldConstants.FORM_FIELD_DATE_MONTH
                    )]!!
                        .view as TextInputLayout).editText!!.text.toString().toInt()
                val year =
                    (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                        field,
                        DynamixFormFieldConstants.FORM_FIELD_DATE_YEAR
                    )]!!
                        .view as TextInputLayout).editText!!.text.toString().toInt()
                val type =
                    (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                        field,
                        DynamixFormFieldConstants.FORM_FIELD_DATE_TYPE
                    )]!!
                        .view as TextInputLayout).editText.toString()

                val localDate: LocalDate
                val converter = DateConverter()
                val now = LocalDate.now()
                if (type.equals("bs", ignoreCase = true)) {
                    DateConverter().getEnglishDate(year, month, day)
                    val dateModel: Model = converter.getEnglishDate(year, month, day)
                    localDate = LocalDate.of(
                        dateModel.year,
                        dateModel.month + 1,
                        dateModel.day
                    )
                } else {
                    // validate if the input is proper english date
                    localDate = LocalDate.of(year, month, day)
                }
                if (field.isDisableFutureDates && localDate.isAfter(now)) {
                    DynamixNotificationUtils.showErrorInfo(
                        ctx,
                        ctx.getString(R.string.dynamix_cr_please_enter_valid) + formFieldView.formField.label
                    )
                    return false
                }
                LocalDate.parse(
                    localDate.toString(),
                    DateTimeFormatter.ofPattern("uuuu-M-d")
                        .withResolverStyle(ResolverStyle.STRICT)
                )
            } catch (e: Exception) {
                DynamixNotificationUtils.showErrorInfo(
                    ctx,
                    ctx.getString(R.string.dynamix_cr_please_enter_valid) + formFieldView.formField.label
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
        val field = formFieldView.formField
        val day =
            (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                formFieldView.formField,
                DynamixFormFieldConstants.FORM_FIELD_DATE_DAY
            )]!!
                .view as TextInputLayout).editText!!.text.toString().toInt()
        val month =
            (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                formFieldView.formField,
                DynamixFormFieldConstants.FORM_FIELD_DATE_MONTH
            )]!!
                .view as TextInputLayout).editText!!.text.toString().toInt()
        val year =
            (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                field,
                DynamixFormFieldConstants.FORM_FIELD_DATE_YEAR
            )]!!
                .view as TextInputLayout).editText!!.text.toString().toInt()
        val type =
            (fieldRenderer.formFieldViewMap[getDateTextFieldChildTag(
                field,
                DynamixFormFieldConstants.FORM_FIELD_DATE_TYPE
            )]!!
                .view as AppCompatSpinner).selectedItem as String
        makeDateTextFieldRequestParameter(
            requestParams,
            merchantRequest,
            merchantRequestParams,
            listConfirmationData,
            field,
            day,
            month,
            year,
            type
        )
    }

    private fun makeDateTextFieldRequestParameter(
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>,
        field: DynamixFormField,
        day: Int,
        month: Int,
        year: Int,
        type: String
    ) {
        val localDate: LocalDate
        var confirmDate: String? = null
        if (type.equals("bs", ignoreCase = true)) {
            val converter = DateConverter()
            val dateModel = converter.getEnglishDate(year, month, day)
            localDate = LocalDate.of(dateModel.year, dateModel.month, dateModel.day)
            val formatter = SimpleDateFormat(DynamixDateUtils.dateFormat3, Locale.getDefault())
            confirmDate = formatter.format(converter.getEnglishDate(dateModel).time)
        } else {
            // validate if the input is proper english date
            localDate = LocalDate.of(year, month, day)
        }
        val formatter = DateTimeFormatter.ofPattern(DynamixDateUtils.dateFormat3)
        val parsedDate = formatter.format(localDate)
        if (confirmDate == null) {
            confirmDate = parsedDate
        }
        if (DynamixCommonUtils.isNumeric(field.tag!!)) {
            merchantRequest.put(DynamixFormFieldConstants.PARAM_ORDER, field.tag)
            merchantRequest.put(DynamixFormFieldConstants.LABEL, field.label)
            merchantRequest.put(DynamixFormFieldConstants.PARAM_VALUE, parsedDate)
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(field.tag!!, parsedDate)
        }
        listConfirmationData.add(DynamixConfirmationModel(field.label, confirmDate!!))
    }
}