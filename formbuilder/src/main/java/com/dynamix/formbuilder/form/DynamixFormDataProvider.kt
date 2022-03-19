package com.dynamix.formbuilder.form

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatRadioButton
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.databinding.DynamixLayoutFormFieldImageUploadBinding
import org.json.JSONArray
import org.json.JSONObject

interface DynamixFormDataProvider {

    fun dynamixOnMainContainerFormFieldAdded()

    fun dynamixOnPostContainerFormFieldAdded()

    fun dynamixPrefixMainContainerFieldsAdded()

    fun dynamixSuffixMainContainerFieldsAdded()

    fun dynamixSpinnerSearchTextChanged(tag: String?, text: String?)

    fun dynamixTxnLimit(formCode: String)

    fun dynamixTxnLimit(formCode: String, tag: String?)

    fun dynamixSelectImage(multipleImages: Boolean)

    fun dynamixManageFieldVisibility(
        inputRadioButton: AppCompatRadioButton?,
        mFormFieldList: List<DynamixFormFieldView>?,
        tag: String?
    )

    fun dynamixHandleOptionsChanged(
        parentView: DynamixFormFieldView,
        childView: DynamixFormFieldView
    )

    fun dynamixHideKeyboardIfOpened()

    fun dynamixOnSubmitButtonClicked()

    fun dynamixOnCancelButtonClicked()

    fun dynamixConvertDateADBS(formField: DynamixFormField)

    fun dynamixValidateAppFormFields()

    fun dynamixOnFormFieldsValidated()

    fun dynamixOnRequestParameterReady(jsonObject: JSONObject)

    fun dynamixOnFormFieldRequestParameterManaged(listConfirmationData: List<DynamixConfirmationModel>)

    fun dynamixOnFormFieldRequestParameterManaged(
        pageTitle: String?,
        listConfirmationData: List<DynamixConfirmationModel>
    )

    fun dynamixKeyOfSpinnerItemSelected(tag: String?, key: String?)

    fun dynamixValueOfSpinnerItemSelected(formField: DynamixFormField, value: String)

    fun dynamixOnImagePickerFieldInflated(
        uploadBinding: DynamixLayoutFormFieldImageUploadBinding,
        field: DynamixFormField
    )

    fun dynamixOnImagePickerFieldRestore(
        binding: DynamixLayoutFormFieldImageUploadBinding,
        field: DynamixFormField
    )

    fun dynamixOnImagePreviewDownload(field: DynamixFormField)

    fun dynamixMakeAppRequestParams(
        field: DynamixFormField, formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    )

    fun dynamixManageAppRequestParams(requestData: Map<String, Any>): Map<String, Any>

    fun dynamixAuthenticate()

    fun dynamixOnAuthenticated(requestData: Map<String, Any>)

    fun dynamixOnButtonClick(buttonField: DynamixFormField, tag: String?)

    fun dynamixCheckChangeListener(key: String, checked: Boolean)

    //Need to call dynamic form method for change listener
    fun dynamixSetupOptionsChangedListener()

    fun dynamixOnImageTap(field: DynamixFormField, imageHolderWrap: LinearLayout, clearImage: ImageView)

    fun dynamixRequestImageSelection(field: DynamixFormField)
}