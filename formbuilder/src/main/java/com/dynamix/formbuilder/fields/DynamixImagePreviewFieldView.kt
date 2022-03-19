package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.core.utils.DynamixViewUtils.setTextViewDrawable
import com.dynamix.formbuilder.view.DynamixImagePreviewFragment
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.databinding.DynamixLayoutFormFieldImageUploadBinding
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixImagePreviewFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val uploadBinding =
            DynamixLayoutFormFieldImageUploadBinding.inflate(LayoutInflater.from(ctx), null, false)
        // set image icon if supplied
        if (field.iconRes != 0) {
            uploadBinding.ltFfIuUploadIcon.setImageResource(field.iconRes)
        }
        uploadBinding.ltFfKycImgDownload.setOnClickListener {
            fieldRenderer.formDataProvider.dynamixOnImagePreviewDownload(field)
        }
        setTextViewDrawable(
            uploadBinding.ltFfIuPreviewSample,
            R.drawable.dynamix_ic_help_outline, 18, 18,
            DynamixResourceUtils.getColorFromThemeAttributes(ctx, R.attr.colorPrimary), Gravity.END
        )
        uploadBinding.ltFfIuTitle.text = field.label
        uploadBinding.ltFfIuUploadInfo.text = field.displayValue
        uploadBinding.ltFfIuUploadLayout.setOnClickListener {
            fieldRenderer.formDataProvider.dynamixRequestImageSelection(field)
        }

        // set place holder
        uploadBinding.ltFfIuImage.setImageResource(R.drawable.dynamix_white_background)
        uploadBinding.ltFfIuPreviewSample.setOnClickListener {
            // show the fragment dialog
            val fragment = DynamixImagePreviewFragment {
                if (field.placeholder.isNotEmpty()) {
                    field.defaultValue = field.placeholder
                }
                fieldRenderer.formDataProvider.dynamixOnImagePreviewDownload(field)
            }
            if (field.placeholder.isNotEmpty()) {
                fragment.setImage(field.placeholder);
            } else {
                fragment.setImage(R.drawable.dynamix_image_sample_preview);
            }
            fragment.showNow((ctx as AppCompatActivity).supportFragmentManager, "")
        }
        if ((field.defaultValue != null && field.defaultValue!!.isNotEmpty())
            || (field.fieldDataValues != null && field.fieldDataValues!!.isNotEmpty())
        ) {
            fieldRenderer.formDataProvider.dynamixOnImagePickerFieldRestore(
                uploadBinding,
                field
            )
        }

//        if (field.defaultValue != null || field.fieldDataValues != null) {
//            fieldRenderer.formDataProvider.onImagePickerFieldRestore(
//                uploadBinding,
//                field
//            )
//        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = uploadBinding.root
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        fieldRenderer.formFieldList.add(formFieldView)
        fieldRenderer.formDataProvider.dynamixOnImagePickerFieldInflated(
            uploadBinding,
            field
        )
        return uploadBinding.root
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        val field = formFieldView.formField
        if (!field.isIgnoreField && !isImagePreviewFieldValid(field)) {
            DynamixNotificationUtils.showErrorInfo(
                ctx,
                if (field.validatorMessage != null) field.validatorMessage else ctx.getString(
                    R.string.dynamix_cr_please_upload_valid
                ) + formFieldView.formField.label
            )
            return false
        }
        return true
    }

    private fun isImagePreviewFieldValid(field: DynamixFormField): Boolean {
        return try {
            fieldRenderer.attachmentMap[field.tag]!!.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override fun requestParams(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {

    }
}