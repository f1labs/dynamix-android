package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.text.*
import android.text.method.DigitsKeyListener
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dynamix.core.extensions.colorFromTheme
import com.dynamix.core.extensions.drawableFor
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.formbuilder.utils.DynamixFormHelper
import com.dynamix.formbuilder.utils.DynamixFormHelper.setColorForDrawable
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.core.utils.date.DynamixDateUtils
import com.dynamix.core.view.DynamixNoChangingBackgroundTextInputLayout
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.contact.DynamixContactPickerBottomSheet
import com.dynamix.formbuilder.contact.DynamixContactPickerType
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixDisabledDateValidator
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import com.google.android.material.datepicker.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hornet.dateconverter.DateConverter
import com.hornet.dateconverter.DatePicker.DatePickerDialog
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Created by user on 16/12/2021.
 */
class DynamixTextInputFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        var label: View? = null
        val formView: View?

        when (field.fieldType) {
            DynamixFieldType.TEXT_FIELD.fieldType -> {
                when {
                    field.childFields != null && field.childFields!!.isNotEmpty() -> {
                        formView = addTextFieldWithChildren(field)
                    }
                    field.isSelfPhoneNumberEntry -> {
                        label = DynamixLabelFieldView(
                            ctx,
                            fieldRenderer.formLabelList,
                            fieldRenderer.formFieldViewMap
                        ).render(field)
                        val textInputLayout = addTextField(field)
                        formView = getPhoneNumberInputLayout(textInputLayout)
                    }
                    else -> {
                        label = DynamixLabelFieldView(
                            ctx,
                            fieldRenderer.formLabelList,
                            fieldRenderer.formFieldViewMap
                        ).render(field)
                        formView = addTextField(field)
                    }
                }
            }
            DynamixFieldType.PHONE_EMAIL.fieldType -> {
                label = DynamixLabelFieldView(
                    ctx,
                    fieldRenderer.formLabelList,
                    fieldRenderer.formFieldViewMap
                ).render(field)
                formView = addTextField(field)
            }
            DynamixFieldType.HIDDEN.fieldType -> {
                formView = addTextField(field)
            }
            else -> {
                if (field.isSelfPhoneNumberEntry) {
                    label = DynamixLabelFieldView(
                        ctx,
                        fieldRenderer.formLabelList,
                        fieldRenderer.formFieldViewMap
                    ).render(field)
                    val textInputLayout = addTextField(field)
                    formView = getPhoneNumberInputLayout(textInputLayout)
                } else {
                    label = DynamixLabelFieldView(
                        ctx,
                        fieldRenderer.formLabelList,
                        fieldRenderer.formFieldViewMap
                    ).render(field)
                    formView = addTextField(field)
                }
            }
        }
        return DynamixFormView(labelView = label, formView = formView)
    }

    private fun setDateStyle(textInputLayout: TextInputLayout) {
        textInputLayout.editText!!.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.dynamix_ic_calendar,
            0
        )
    }

    @SuppressLint("SetTextI18n")
    private fun addNepaliDateFieldListeners(formField: DynamixFormField, editText: EditText) {
        val currentDate = DateConverter().todayNepaliDate
        val date =
            DatePickerDialog.OnDateSetListener { view: DatePickerDialog?, year: Int, month: Int, day: Int ->
                editText.setText(year.toString() + "-" + (month + 1).toString() + "-" + day.toString())
            }
        editText.setOnClickListener {
            val dialog = DatePickerDialog.newInstance(date, currentDate)
            dialog.version = DatePickerDialog.Version.VERSION_2
            if (formField.isDisableFutureDates) {
                dialog.setMaxDate(currentDate)
            }
            if (formField.isDisablePastDates) {
                dialog.setMinDate(currentDate)
            }
            dialog.show((ctx as AppCompatActivity).supportFragmentManager, "DATE")
            val imm =
                ctx.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    private fun addDateFieldListeners(formField: DynamixFormField, editText: EditText) {
        val myCalendar = Calendar.getInstance()
        val positiveButtonClickListener =
            MaterialPickerOnPositiveButtonClickListener { selection: Long? ->
                myCalendar.timeInMillis = selection!!
                val myFormat = "yyyy-MM-dd" //In which you need put here
                val sdf = SimpleDateFormat(myFormat, Locale.UK)
                editText.setText(sdf.format(myCalendar.time))
            }
        editText.setOnClickListener {
            if (editText.text.toString().isNotEmpty()) {
                try {
                    val selectedDate = DynamixDateUtils.getDateFormat(DynamixDateUtils.dateFormat3)
                        .parse(editText.text.toString())!!
                    myCalendar.time = selectedDate
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            val constraintBuilder = CalendarConstraints.Builder()
            val validators: MutableList<CalendarConstraints.DateValidator> = ArrayList()
            if (formField.isDisableFutureDates) {
                validators.add(DateValidatorPointBackward.now())
            }
            if (formField.isDisablePastDates) {
                // subtract by 86400000 to make current day inclusive
                validators.add(DateValidatorPointForward.from(System.currentTimeMillis() - 86400000))
            }
            if (formField.minDate != null) {
                validators.add(DateValidatorPointForward.from(formField.minDate!! - 86400000))
            }
            if (formField.maxDate != null) {
                validators.add(DateValidatorPointBackward.before(formField.maxDate!!))
            }
            if (formField.disabledDays != null) {
                val disabledDateValidator = DynamixDisabledDateValidator(formField.disabledDays!!)
                validators.add(disabledDateValidator)
            }
            constraintBuilder.setValidator(CompositeDateValidator.allOf(validators))
            val myFormat = "yyyy-MM-dd" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.UK)
            val selectedDate = sdf.format(myCalendar.time)
            val selectedDateEpoch = LocalDate.parse(
                selectedDate, DateTimeFormatter.ofPattern(
                    DynamixDateUtils.dateFormat3
                )
            ).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            datePickerBuilder.setSelection(selectedDateEpoch)
            constraintBuilder.setOpenAt(selectedDateEpoch)
            datePickerBuilder.setCalendarConstraints(constraintBuilder.build())
            val datePicker = datePickerBuilder.build()
            datePicker.addOnPositiveButtonClickListener(positiveButtonClickListener)
            datePicker.showNow(
                (ctx as AppCompatActivity).supportFragmentManager,
                "material_date_picker"
            )
            val imm =
                ctx.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    private fun setTimeStyle(textInputLayout: TextInputLayout) {
        textInputLayout.editText!!.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.dynamix_ic_time,
            0
        )
    }

    private fun setPhoneStyle(textInputLayout: TextInputLayout) {
        textInputLayout.endIconDrawable = ctx.drawableFor(R.drawable.dynamix_ic_contact)
        textInputLayout.setEndIconTintList(ColorStateList.valueOf(ctx.colorFromTheme(R.attr.inputIconTint)))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addPhoneFieldListener(
        contactPickerType: Int,
        textInputLayout: DynamixNoChangingBackgroundTextInputLayout
    ) {
        textInputLayout.setEndIconOnClickListener {
            selectContactNumber(contactPickerType)
        }
    }

    private fun selectContactNumber(contactPickerType: Int) {
        val bottomSheet = DynamixContactPickerBottomSheet(fieldRenderer.contactFetcher) {
            if (contactPickerType == DynamixContactPickerType.PHONE) {
                onContactNumberSelected(it.number)
            } else {
                onEsewaContactNumberSelected(it.number)
            }
        }
        bottomSheet.showNow((ctx as AppCompatActivity).supportFragmentManager, null)
    }

    private fun onEsewaContactNumberSelected(number: String?) {
        for (formFieldView in fieldRenderer.formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.PHONE_EMAIL.fieldType &&
                formFieldView.view.visibility == View.VISIBLE
            ) {
                (formFieldView.view as TextInputLayout).editText!!.setText(number)
            }
        }
    }

    private fun onContactNumberSelected(number: String?) {
        if (fieldRenderer.formFieldViewMap["phone"] != null) {
            val formFieldView = fieldRenderer.formFieldViewMap["phone"]
            (formFieldView!!.view as TextInputLayout).editText!!.setText(number)
        } else {
            for (formFieldView in fieldRenderer.formFieldList) {
                if (formFieldView.formField.fieldType == DynamixFieldType.PHONE.fieldType &&
                    formFieldView.view.visibility == View.VISIBLE
                ) {
                    (formFieldView.view as TextInputLayout).editText!!.setText(number)
                }
            }
        }
    }

    private fun addTimeFieldListeners(editText: EditText) {
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.dynamix_ic_time, 0)
        editText.setOnClickListener {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val timePicker = TimePickerDialog(
                ctx,
                { _: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    editText.setText(selectedTime)
                },
                hour,
                minute,
                true
            ) //Yes 24 hour time
            timePicker.setTitle("Select Time")
            timePicker.show()
            val imm =
                ctx.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }

    private fun isAutoCompleteTextField(field: DynamixFormField): Boolean =
        field.fieldType == DynamixFieldType.PHONE.fieldType || field.fieldType == DynamixFieldType.PHONE_EMAIL.fieldType

    override fun render(field: DynamixFormField): View {
        //Setup NoChangingBackgroundTextInputLayout
        val textInputLayout =
            DynamixNoChangingBackgroundTextInputLayout(
                ctx,
                null,
                if (isAutoCompleteTextField(field)) {
                    R.attr.geFmAutoCompleteInputStyle
                } else {
                    R.attr.geFmTextInputStyle
                },
            )
        textInputLayout.id = R.id.dynamix_text_input_layout
        val editText: EditText =
            if (isAutoCompleteTextField(field)) {
                MaterialAutoCompleteTextView(textInputLayout.context)
            } else {
                TextInputEditText(textInputLayout.context)
            }
        editText.id = R.id.dynamix_text_input_edit_text
        textInputLayout.addView(editText)
        if (field.label.isNotEmpty() && field.label.equals(
                ctx.getString(R.string.dynamix_label_amount),
                ignoreCase = true
            )
        ) {
            textInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (field.label.isNotEmpty() && field.label.equals(
                        ctx.getString(R.string.dynamix_label_amount),
                        ignoreCase = true
                    )
                ) {
                    if (s.isNotEmpty()) {
                        textInputLayout.setEndIconDrawable(R.drawable.dynamix_ic_cross_circle)
                    } else {
                        textInputLayout.endIconDrawable = null
                    }
                }
                if (s.isNotEmpty()) {
                    if (field.tag != null && field.tag!!.lowercase().contains("date")) {
                        fieldRenderer.formDataProvider.dynamixConvertDateADBS(field)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        textInputLayout.setEndIconOnClickListener {
            editText.setText("")
        }

        //Add dynamic params
        textInputLayout.editText!!.tag = field.tag
        if (field.isHidden) {
            textInputLayout.isVisible = false
        }
        if (field.defaultValue != null &&
            !TextUtils.isEmpty(field.defaultValue)
        ) {
            editText.setText(field.defaultValue)
        }
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.hint = field.placeholder
            } else {
                editText.hint = ""
            }
        }

        if (field.isNonEditable) {
            editText.isFocusable = false
            editText.isEnabled = false
        }
        if (field.isDisableCopyPaste) {
            editText.isLongClickable = false
            editText.setTextIsSelectable(false)
            editText.setOnLongClickListener { true }
        }
        if (field.imeOptions > -1) {
            editText.imeOptions = field.imeOptions
        }
        when (field.fieldType) {
            DynamixFieldType.AMOUNT.fieldType, DynamixFieldType.NUMBER.fieldType -> editText.inputType =
                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
            DynamixFieldType.HIDDEN.fieldType -> textInputLayout.isVisible = false
            DynamixFieldType.TEXTAREA.fieldType -> {
                editText.isSingleLine = false
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                editText.setLines(5)
                editText.maxLines = 10
                editText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                editText.gravity = Gravity.TOP or Gravity.START
            }
            DynamixFieldType.NUMBER_PASSWORD.fieldType -> {
                editText.inputType = InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_VARIATION_PASSWORD
                textInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                textInputLayout.setEndIconTintList(ColorStateList.valueOf(ctx.colorFromTheme(R.attr.inputIconTint)))
            }
            DynamixFieldType.TEXT_PASSWORD.fieldType -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
                textInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                textInputLayout.setEndIconTintList(ColorStateList.valueOf(ctx.colorFromTheme(R.attr.inputIconTint)))
            }
            DynamixFieldType.ACCOUNT.fieldType -> {
            }
            DynamixFieldType.DATE.fieldType -> {
                editText.isFocusable = false
                setDateStyle(textInputLayout)
                if (field.isNepaliDate) {
                    addNepaliDateFieldListeners(field, editText)
                } else {
                    addDateFieldListeners(field, editText)
                }
            }
            DynamixFieldType.TIME.fieldType -> {
                editText.isFocusable = false
                setTimeStyle(textInputLayout)
                addTimeFieldListeners(editText)
            }
            DynamixFieldType.PHONE.fieldType -> {
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                setPhoneStyle(textInputLayout)
                addPhoneFieldListener(DynamixContactPickerType.PHONE, textInputLayout)
            }
            DynamixFieldType.PHONE_EMAIL.fieldType -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT
                setPhoneStyle(textInputLayout)
                addPhoneFieldListener(DynamixContactPickerType.PHONE_EMAIL, textInputLayout)
            }
            DynamixFieldType.TEXT_DATA.fieldType -> editText.inputType = InputType.TYPE_CLASS_TEXT
            else -> editText.inputType = InputType.TYPE_CLASS_TEXT
        }
        if (field.maxLength > 0) {
            editText.filters = arrayOf<InputFilter>(
                InputFilter.LengthFilter(field.maxLength)
            )
        }
        if (field.inputFilters != null) {
            editText.filters = field.inputFilters
        }
        if (field.inputType > 0) {
            editText.inputType = field.inputType
        }
        if (field.inputDigits != null && field.inputDigits!!.isNotEmpty()) {
            editText.keyListener = DigitsKeyListener.getInstance(field.inputDigits!!)
            editText.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
        }
        val fieldView = DynamixFormFieldView()
        fieldView.formField = field
        fieldView.view = textInputLayout
        fieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        fieldRenderer.formFieldList.add(fieldView)
        if (!field.isCategoryField) {
            DynamixFormBuilderValidationUtils.realTimeValidateField(fieldView)
        }
        setColorForDrawable(
            textInputLayout.findViewById(R.id.dynamix_text_input_edit_text), false
        )
        return textInputLayout
    }

    private fun addTextFieldWithChildren(field: DynamixFormField): View {
        return DynamixTextFieldChildrenFieldView().init(ctx, fieldRenderer).render(field)!!
    }

    private fun addTextField(field: DynamixFormField): DynamixNoChangingBackgroundTextInputLayout {
        return render(field) as DynamixNoChangingBackgroundTextInputLayout
    }

    private fun getPhoneNumberInputLayout(textInputLayout: TextInputLayout): LinearLayout {
        val layout = DynamixFormHelper.addIconToEnd(
            textInputLayout,
            fieldRenderer.viewsNotInFormFieldList,
            R.drawable.dynamix_ic_phone_android
        )
        layout.getChildAt(1).setOnClickListener {
            textInputLayout.editText?.setText(DynamixEnvironmentData.loggedInUserName)
        }
        return layout
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        when (formFieldView.formField.fieldType) {
            DynamixFieldType.ACCOUNT.fieldType, DynamixFieldType.TEXT_FIELD.fieldType -> run {
                return validateAccountAndNormalTextField(formFieldView)
            }
            DynamixFieldType.TIME.fieldType -> {
                return validateTimeField(formFieldView)
            }
            else -> {
                return validateCategorizedTextField(formFieldView)
            }
        }
    }

    private fun validateAccountAndNormalTextField(formFieldView: DynamixFormFieldView): Boolean {
        if (formFieldView.formField.childFields != null
            && formFieldView.formField.childFields!!.isNotEmpty()
        ) {
            // validate the child fields
            for (child in formFieldView.formField.childFields!!) {
                if (child.isIgnoreField) {
                    continue
                }
                val childView = fieldRenderer.formFieldViewMap[child.tag]
                val isValid =
                    DynamixFormBuilderValidationUtils.isFormFieldValid(
                        childView!!,
                        true
                    )
                if (!isValid) {
                    return false
                }
            }
        }
        if (formFieldView.formField.isIgnoreField &&
            !formFieldView.formField.isValidateIgnoreField
        ) {
            return true
        }
        if (!DynamixFormBuilderValidationUtils.isFormFieldValid(
                formFieldView,
                true
            )
        ) {
            return false
        }
        return true
    }

    private fun validateTimeField(formFieldView: DynamixFormFieldView): Boolean {
        val field = formFieldView.formField

        if (field.isIgnoreField &&
            !field.isValidateIgnoreField
        ) {
            return true
        }
        if (!DynamixFormBuilderValidationUtils.isFormFieldValid(
                formFieldView,
                true
            )
        ) return false

        if (field.timeValidationDateTag.isNotEmpty() && fieldRenderer.formFieldViewMap.containsKey(
                field.timeValidationDateTag
            )
        ) {
            val selectedDateTextInputLayout =
                fieldRenderer.formFieldViewMap[field.timeValidationDateTag]?.view as TextInputLayout
            val selectedDate = selectedDateTextInputLayout.editText?.text.toString()

            if (selectedDate.isNotEmpty()) {
                val currentDate = DynamixDateUtils.getFormattedDate(
                    DynamixDateUtils.dateFormat3,
                    Date()
                )

                if (selectedDate.equals(
                        currentDate,
                        ignoreCase = true
                    ) && (field.disablePastTime || field.disableFutureTime)
                ) {
                    val textInputLayout = formFieldView.view as TextInputLayout
                    val selectedTime = textInputLayout.editText?.text.toString()
                    val selectedTimeParts = selectedTime.split(":")
                    selectedTimeParts.map { time -> time.trim() }

                    val selectedTimeInDate = LocalTime.of(
                        selectedTimeParts.first().toInt(),
                        selectedTimeParts.last().toInt()
                    )
                    val currentTimeInDate = LocalTime.now()

                    if (field.disablePastTime) {
                        if (selectedTimeInDate.isBefore(currentTimeInDate)) {
                            DynamixNotificationUtils.errorDialog(
                                ctx,
                                "You cannot select past time."
                            )
                            return false
                        }
                    }
                    if (field.disableFutureTime) {
                        if (selectedTimeInDate.isAfter(currentTimeInDate)) {
                            DynamixNotificationUtils.errorDialog(
                                ctx,
                                "You cannot select future time."
                            )
                            return false
                        }
                    }
                }
            }
        }
        if (field.startTime.isNotEmpty() && field.endTime.isNotEmpty()) {
            val textInputLayout = formFieldView.view as TextInputLayout
            val selectedTime = textInputLayout.editText?.text.toString()
            val selectedTimeParts = selectedTime.split(":")
            selectedTimeParts.map { time -> time.trim() }
            val selectedTimeInDate = LocalTime.of(
                selectedTimeParts.first().toInt(),
                selectedTimeParts.last().toInt()
            )

            val startTime = field.startTime
            val startTimeParts = startTime.split(":")
            startTimeParts.map { time -> time.trim() }
            val startTimeInDate = LocalTime.of(
                startTimeParts.first().toInt(),
                startTimeParts.last().toInt()
            )

            val endTime = field.endTime
            val endTimeParts = endTime.split(":")
            endTimeParts.map { time -> time.trim() }
            val endTimeInDate = LocalTime.of(
                endTimeParts.first().toInt(),
                endTimeParts.last().toInt()
            )

            if (selectedTimeInDate.isBefore(startTimeInDate) || selectedTimeInDate.isAfter(
                    endTimeInDate
                )
            ) {
                DynamixNotificationUtils.errorDialog(
                    ctx,
                    "${field.label} must be between ${field.startTime} and ${field.endTime}"
                )
                return false
            }
        }
        return true
    }

    private fun validateCategorizedTextField(formFieldView: DynamixFormFieldView): Boolean {
        if (formFieldView.formField.isIgnoreField &&
            !formFieldView.formField.isValidateIgnoreField
        ) {
            return true
        }
        if (!DynamixFormBuilderValidationUtils.isFormFieldValid(
                formFieldView,
                true
            )
        ) {
            return false
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
        when (formFieldView.formField.fieldType) {
            DynamixFieldType.ACCOUNT.fieldType, DynamixFieldType.TEXT_FIELD.fieldType -> run {
                makeRequestParamsForAccountAndNormalTextField(
                    formFieldView,
                    requestParams,
                    merchantRequest,
                    merchantRequestParams,
                    listConfirmationData
                )
            }
            DynamixFieldType.HIDDEN.fieldType -> {
                makeRequestParamsForHiddenField(
                    formFieldView,
                    requestParams,
                    merchantRequest,
                    merchantRequestParams
                )
            }
            else -> {
                makeRequestParamsForCategorizedTextField(
                    formFieldView,
                    requestParams,
                    merchantRequest,
                    merchantRequestParams,
                    listConfirmationData
                )
            }
        }
    }

    private fun makeRequestParamsForAccountAndNormalTextField(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {
        if (formFieldView.formField.childFields != null
            && formFieldView.formField.childFields!!.isNotEmpty()
        ) {
            if (formFieldView.formField.isChildManaged) {
                // as the child needs to be managed, we need to handle the params ourselves
                for (child in formFieldView.formField.childFields!!) {
                    if (child.isIgnoreField) {
                        continue
                    }
                    val childView = fieldRenderer.formFieldViewMap[child.tag]
                    makeTextInputRequestParams(
                        childView!!,
                        requestParams,
                        merchantRequest,
                        merchantRequestParams,
                        listConfirmationData
                    )
                }
            }
            return
        }
        makeTextInputRequestParams(
            formFieldView,
            requestParams,
            merchantRequest,
            merchantRequestParams,
            listConfirmationData
        )
    }

    private fun makeRequestParamsForHiddenField(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray
    ) {
        val editText1 = (formFieldView.view as TextInputLayout).editText
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
                editText1!!.text.toString().trim { it <= ' ' })
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(
                formFieldView.formField.tag!!,
                editText1!!.text.toString().trim { it <= ' ' })
        }
    }

    private fun makeRequestParamsForCategorizedTextField(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {
        makeTextInputRequestParams(
            formFieldView,
            requestParams,
            merchantRequest,
            merchantRequestParams,
            listConfirmationData
        )
    }

    private fun makeTextInputRequestParams(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {
        val editText = (formFieldView.view as TextInputLayout).editText!!

        // check if the field is from merchant, if so put the data in merchant format
        if (DynamixCommonUtils.isNumeric(formFieldView.formField.tag!!)) {
            merchantRequest.put(DynamixFormFieldConstants.PARAM_ORDER, formFieldView.formField.tag)
            merchantRequest.put(DynamixFormFieldConstants.LABEL, formFieldView.formField.label)
            merchantRequest.put(
                DynamixFormFieldConstants.PARAM_VALUE,
                editText.text.toString().trim { it <= ' ' })
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(
                formFieldView.formField.tag!!,
                editText.text.toString().trim { it <= ' ' })
        }
        fieldRenderer.appLoggerProvider.debug("GETYPE : " + formFieldView.formField.fieldType)
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            if (formFieldView.formField.confirmationLabel != null
                && !formFieldView.formField.confirmationLabel!!.isEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            editText.text.toString().trim { it <= ' ' })
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            editText.text.toString().trim { it <= ' ' })
                    )
                )
            }
        } else {
            if (!editText.text.toString().isEmpty()) {
                if (formFieldView.formField.confirmationLabel != null
                    && formFieldView.formField.confirmationLabel!!.isNotEmpty()
                ) {
                    listConfirmationData.add(
                        DynamixConfirmationModel(
                            formFieldView.formField.confirmationLabel!!,
                            editText.text.toString().trim { it <= ' ' })
                    )
                } else {
                    listConfirmationData.add(
                        DynamixConfirmationModel(
                            formFieldView.formField.label,
                            editText.text.toString().trim { it <= ' ' })
                    )
                }
            }
        }
    }
}