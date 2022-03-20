package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

interface DynamixFormDataHandler {

    fun render(field: DynamixFormField): View?

    fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        return this
    }

    fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView()
    }

    fun validate(formFieldView: DynamixFormFieldView): Boolean

    fun requestParams(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    )
}