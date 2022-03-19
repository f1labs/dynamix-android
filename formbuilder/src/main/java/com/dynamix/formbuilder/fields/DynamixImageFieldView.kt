package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.databinding.DynamixViewImageUploadBinding
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 20/12/2021.
 */
class DynamixImageFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val clearImageLp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin = dpToPx(ctx, 8f)
        clearImageLp.setMargins(margin, margin, margin, margin)
        clearImageLp.gravity = Gravity.END
        val imageUploadBinding: DynamixViewImageUploadBinding = DataBindingUtil.inflate(
            LayoutInflater.from(ctx),
            R.layout.dynamix_view_image_upload, null, false
        )
        imageUploadBinding.label.text = field.label
        val clearImage = ImageView(ctx)
        clearImage.setImageResource(R.drawable.dynamic_ic_clear)
        clearImage.layoutParams = clearImageLp
        clearImage.isVisible = false
        val textView = TextView(ctx)
        textView.text = field.label
        textView.typeface = ResourcesCompat.getFont(ctx, fieldRenderer.font)
        textView.setTextColor(
            DynamixResourceUtils.getColorFromThemeAttributes(
                ctx,
                R.attr.textColor200
            )
        )
        val imageHolderWrap = LinearLayout(ctx)
        imageHolderWrap.orientation = LinearLayout.HORIZONTAL
        imageHolderWrap.layoutParams = layoutParams
        val textViewLp = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textViewLp.addRule(RelativeLayout.BELOW, field.id)
        clearImageLp.setMargins(0, margin, margin, 0)
        val linearLayout = LinearLayout(ctx)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(imageUploadBinding.root)
        linearLayout.addView(imageHolderWrap)
        imageUploadBinding.ivUpload.setOnClickListener {
            fieldRenderer.formDataProvider.dynamixOnImageTap(field, imageHolderWrap, clearImage)
        }
        clearImage.setOnClickListener {
            clearImage.isVisible = false
            imageHolderWrap.removeAllViews()
            fieldRenderer.fileMap[field.tag!!] = ArrayList()
            imageUploadBinding.root.isVisible = true
        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = imageUploadBinding.root
        formFieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        fieldRenderer.formFieldList.add(formFieldView)
        return linearLayout
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        if (formFieldView.formField.isRequired
            && (fieldRenderer.fileMap[formFieldView.formField.tag] == null
                    || fieldRenderer.fileMap[formFieldView.formField.tag]!!.isEmpty())
        ) {
            DynamixNotificationUtils.showErrorInfo(
                ctx,
                ctx.getString(R.string.dynamix_cr_please_select) + formFieldView.formField.label
            )
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

    }
}