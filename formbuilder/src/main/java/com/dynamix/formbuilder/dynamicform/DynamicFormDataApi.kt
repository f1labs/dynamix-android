package com.dynamix.formbuilder.dynamicform

import android.os.Parcelable
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.formbuilder.data.DynamixKeyValue
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DynamicFormDataApi(
    @SerializedName("success")
    val isSuccess: Boolean = false,
    val dynamicForm: Map<String, DynamicForm>? = null
)

data class DynamicForm(
    val displayAccountNumberField: Boolean = false,
    val displayPrimaryAccountNumberOnly: Boolean = false,
    val accountNumberLabel: String? = null,
    val displayConfirmation: Boolean = false,
    val askAuthentication: Boolean = false,
    val intermediateDataUrl: String = "",
    val url: String = "",
    val response: JsonObject? = JsonObject(),
    val fields: List<DynamicFormField> = arrayListOf(),
    val hasImageUpload: Boolean = false,
    val imageUploadPath: String = "",
    val termsAndConditionPath: String = "",
    val bookingUrl: String = "",
    val displayBookingDataInConfirmation: Boolean = false,
    val event: DynamixEvent? = null
)

@Parcelize
data class DynamicFormField(
    val paramOrder: Int = 0,
    val maxLength: Int = 0,
    val amountField: Boolean = false,
    @SerializedName("label")
    private val _label: String = "",
    @SerializedName("description")
    private val _description: String = "",
    val paramValue: String = "",
    val regex: String = "",
    val inputType: String = "",
    val required: String = "",
    val placeHolder: String = "",
    val options: List<DynamixKeyValue> = listOf(),
    val tag: String? = null,
    val payableLimit: DynamicPayableLimit? = null,
    val dataUrl: String = "",
    val minDate: Long = 0,
    val maxDate: Long = 0,
    val enableDaysAfter: Long = 0,
    val enableDaysBefore: Long = 0,
    val disabledDays: List<String> = emptyList(),
    val disablePastDates: Boolean = false,
    val disableFutureDates: Boolean = false,
    val allowMultipleImages: Boolean = false,
    val defaultValue: String = "",
    val ignoreField: Boolean = false,
    val nonEditable: Boolean = false,
    val webDataPath: String = "",
    val webDataValue: String = "",
    /*
    This is used to adapt url response with required data format
    If response is {"data" : "categories": [ { "id": 1, "name" : "Test" } ]}, then
    {@responseArrayKey} is categories
    {@responseKeyTag} is id, then mapped with key of KeyValue -> data for request
    {@responseValueTag} is name, then mapped with value of KeyValue -> data for display
     */
    val adaptData: Boolean = false,
    val sortData: Boolean = true,
    val responseArrayKey: String = "",
    val responseKeyTag: String = "",
    val responseValueTag: String = "",
    val appendInResponseValue: String = "",
    /*
    For Limit fields
     */
    val fieldLimit: Boolean = false,
    val fieldLimitKey: String = "",
    val fieldLimitValues: List<DynamixKeyValue> = emptyList(),
    val fieldDataLimitMap: Map<String, List<DynamixKeyValue>> = emptyMap(),
    val visibilityParent: String? = "",
    val visibilityValues: List<String>? = emptyList(),

    val hasChildField: Boolean = false,
    val addChildField: Boolean = false,
    val childField: DynamicChildFormField? = DynamicChildFormField(),
    val defaultValueFromParent: Boolean = false,
    val defaultValueParentTag: String = "",
    var locale: Map<String, Map<String, String>>? = null

) : Parcelable {

    val label: String
        get() {
            if (locale != null && locale?.containsKey("label")!!) {
                if (locale!!["label"]?.containsKey(DynamixEnvironmentData.locale)!!)
                    return locale!!["label"]?.get(DynamixEnvironmentData.locale)!!
            }
            return _label
        }

    val description: String
        get() {
            if (locale != null && locale?.containsKey("description")!!) {
                if (locale!!["description"]?.containsKey(DynamixEnvironmentData.locale)!!)
                    return locale!!["description"]?.get(DynamixEnvironmentData.locale)!!
            }
            return _description
        }
}

@Parcelize
data class DynamicChildFormField(
    val keyToAddChildFields: String = "",
    val valueToAddChildFields: String = "",
    val childVisibilityKey: String = "",
    val childVisibilityValues: List<String> = emptyList(),
    val allowEmptyChild: Boolean = true,
    val fields: List<DynamicFormField> = emptyList()
) : Parcelable

object DynamicFieldType {
    const val NUMBER = "NUMBER"
    const val LANDLINE = "LANDLINE"
    const val MOBILE = "MOBILE"
    const val DROPDOWN = "DROPDOWN"
    const val DROPDOWN_SEARCH = "DROPDOWN_SEARCH"
    const val ESEWAID = "ESEWAID"
    const val TEXTAREA = "TEXTAREA"
    const val DATE = "DATE"
    const val TIME = "TIME"
    const val IMAGE = "IMAGE"
    const val WEB_DATA = "WEB_DATA"
}