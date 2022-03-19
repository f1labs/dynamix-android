package com.dynamix.formbuilder.form

import DynamixFormFieldConstants
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

class DynamixRequestParamsGenerator(
    private val formFieldList: MutableList<DynamixFormFieldView>,
    private val formDataProvider: DynamixFormDataProvider,
    private val appLoggerProvider: AppLoggerProvider,
    private val finalRequestParams: (Map<String, Any>) -> Unit,
) {

    fun makeRequestParameters() {
        try {
            val requestParams = JSONObject()
            val merchantRequestParams = JSONArray()
            val listConfirmationData: MutableList<DynamixConfirmationModel> = ArrayList()
            for (formFieldView in formFieldList) {
                val merchantRequest = JSONObject()
                if (!formFieldView.formField.isIgnoreField) {
                    if (formFieldView.fieldHandler != null) {
                        formFieldView.fieldHandler!!.requestParams(
                            formFieldView,
                            requestParams,
                            merchantRequest,
                            merchantRequestParams,
                            listConfirmationData
                        )
                    } else {
                        formDataProvider.dynamixMakeAppRequestParams(
                            formFieldView.formField,
                            formFieldView,
                            requestParams,
                            merchantRequest,
                            merchantRequestParams,
                            listConfirmationData
                        )
                    }
                }
            }
            if (merchantRequestParams.length() > 0) {
                requestParams.put(DynamixFormFieldConstants.FIELDS, merchantRequestParams)
            }
            formDataProvider.dynamixOnRequestParameterReady(requestParams)
            var requestData: Map<String, Any> = Gson().fromJson(
                requestParams.toString(), object : TypeToken<HashMap<String?, Any?>?>() {}.type
            )
            requestData = formDataProvider.dynamixManageAppRequestParams(requestData)
            finalRequestParams(requestData)

            appLoggerProvider.debug("Raw Request: \n \t $requestParams")
            formDataProvider.dynamixOnFormFieldRequestParameterManaged(listConfirmationData)
        } catch (e: Exception) {
            e.printStackTrace()
            appLoggerProvider.info(e.localizedMessage)
        }
    }
}