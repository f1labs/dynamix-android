package com.dynamix.formbuilder.data

import DynamixViewContainer
import android.os.Parcelable
import android.text.InputFilter
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.DayOfWeek

@Parcelize
data class DynamixFormField(
    var id: Int = 0,
    var label: String = "",
    var displayValue: String? = null,
    var labelValue: String? = null,
    var placeholder: String = "",
    var fieldType: String = DynamixFieldType.TEXT_FIELD.fieldType,
    var pattern: String? = null,
    var defaultValue: String? = null,
    var defaultItemPosition: Int = 0,
    var maxLength: Int = 0,
    var tag: String? = null,
    var isRequired: Boolean = false,
    var spinnerMultiItems: List<Map<String?, String>>? = null,
    var validatorMessage: String? = null,
    var requiredMessage: String? = null,
    var isDisablePastDates: Boolean = false,
    var isDisableFutureDates: Boolean = false,
    var parentSpinner: String? = null,
    var options: Map<String?, String>? = null,
    var isOrientationHorizontal: Boolean = false,
    var isOrientationVertical:Boolean = false,
    var inputFilters: Array<@RawValue InputFilter>? = emptyArray(),
    var inputType: Int = 0,
    var isIgnoreField: Boolean = false,
    var isValidateIgnoreField: Boolean = false,
    var minValue: Double = 0.0,
    var maxValue: Double= 0.0,
    var isHidden: Boolean = false,
    var visibilityParent: String? = null,
    var visibilityTabParent: String? = null,
    var visibilityValues: List<String>? = null,
    var dateRangeFields: List<DynamixFormField>? = null,
    var isEnabled: Boolean = false,
    var isNonEditable: Boolean = false,
    var isNepaliDate: Boolean = false,
    var isDisableCopyPaste: Boolean = false,
    var confirmationLabel: String? = null,
    var iconUrl: String? = null,
    var childFields: List<DynamixFormField>? = null,
    var isOptionSelected: Boolean = false,
    var fieldDataValues: MutableList<@RawValue Any>? = null,
    var isCategoryField: Boolean = false,
    var minDate: Long? = null,
    var maxDate: Long? = null,
    var serviceCode: String? = null,
    var isAllowMultipleImages: Boolean = false,
    var imeOptions: Int = -1,
    var isSelfPhoneNumberEntry: Boolean = false,
    var isDisableContactSearch: Boolean = false,
    var disabledDays: List<DayOfWeek>? = null,

    // notes
    var notes: List<String>? = null,
    var iconRes: Int = 0,
    var webUrl: String? = null,
    var webData: String? = null,
    var weight: Int = 0,
    var isChildManaged: Boolean = false,
    var textAppearance: Int = 0,
    var shouldSendValue: Boolean = false,
    var containerType: DynamixViewContainer = DynamixViewContainer.MAIN_CONTAINER,
    var textColor: Int? = null,
    var inputDigits: String? = null,
    var isFullWidth: Boolean = false,
    var fieldData: @RawValue Any? = null,
    var fieldDataLimitMap: Map<String, List<DynamixKeyValue>> = emptyMap(),
    var startTime: String = "",
    var endTime: String = "",
    /*
    * This @{timeValidationDateTag} is used to get date to which current date is to be compared
    * This @{disablePastTime} is used to disable past time
    * This @{disableFutureTime} is used to disable future time
    *
     */
    var timeValidationDateTag: String = "",
    var disablePastTime: Boolean = false,
    var disableFutureTime: Boolean = false,
    var maxDecimalDigits: Int? = null
): Parcelable