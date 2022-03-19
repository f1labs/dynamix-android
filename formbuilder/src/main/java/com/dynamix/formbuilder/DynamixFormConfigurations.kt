package com.dynamix.formbuilder

import androidx.appcompat.app.AppCompatActivity
import androidx.collection.SimpleArrayMap
import com.dynamix.formbuilder.constants.DynamixFormNavigation
import com.dynamix.formbuilder.fields.DynamixFormDataHandler
import com.dynamix.formbuilder.fields.render.DynamixRegisteredFieldTypes
import com.dynamix.formbuilder.search.DynamixSearchFilterActivity

object DynamixFormConfigurations {

    var registeredFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()
    var prefixFormFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()
    var postFixFormFieldTypes = SimpleArrayMap<String, DynamixFormDataHandler>()

    fun addFieldTypes(appFieldTypes: SimpleArrayMap<String, DynamixFormDataHandler>): DynamixFormConfigurations {
        val formFieldTypes = DynamixRegisteredFieldTypes().fieldTypes()
        if (!appFieldTypes.isEmpty) {
            formFieldTypes.putAll(appFieldTypes)
        }
        registeredFieldTypes.putAll(formFieldTypes)
        return this
    }

    fun addPrefixFieldTypes(appFieldTypes: SimpleArrayMap<String, DynamixFormDataHandler>): DynamixFormConfigurations {
        val prefixFieldTypes = DynamixRegisteredFieldTypes().prefixFieldTypes()
        if (!appFieldTypes.isEmpty) {
            prefixFieldTypes.putAll(appFieldTypes)
        }
        prefixFormFieldTypes.putAll(prefixFieldTypes)
        return this
    }

    fun addPostFixFieldTypes(appFieldTypes: SimpleArrayMap<String, DynamixFormDataHandler>): DynamixFormConfigurations {
        val postfixFieldTypes = DynamixRegisteredFieldTypes().postfixFieldTypes()
        if (!appFieldTypes.isEmpty) {
            postfixFieldTypes.putAll(appFieldTypes)
        }
        postFixFormFieldTypes.putAll(postfixFieldTypes)
        return this
    }

    fun addActivities(activityMap: MutableMap<String, Class<out AppCompatActivity>>): DynamixFormConfigurations {
        activityMap.apply {
            put(DynamixFormNavigation.SEARCH_ACTIVITY, DynamixSearchFilterActivity::class.java)
        }
        return this
    }
}