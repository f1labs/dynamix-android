package com.dynamix.formbuilder.fields

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixResourceUtils.getColorFromThemeAttributes
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixOptionsFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    private val selectedOptionBg = R.drawable.dynamix_bg_selected_option
    private val selectedOptionBgLeft = R.drawable.dynamix_bg_selected_option_left
    private val selectedOptionBgRight = R.drawable.dynamix_bg_selected_option_right
    private val unSelectedOptionBg = R.drawable.dynamix_bg_unselected_option
    private val unSelectedOptionBgLeft = R.drawable.dynamix_bg_unselected_option_left
    private val unSelectedOptionBgRight = R.drawable.dynamix_bg_unselected_option_right
    private val selectedOptionTextColor = R.attr.textColorWhite
    private val unSelectedOptionTextColor = R.attr.textColor400
    private val optionTopMargin = 0f
    private val optionBottomMargin = 0f

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    private fun optionSelectedItem(
        parentTag: String?,
        childField: DynamixFormField?,
        i: Int,
        size: Int
    ) {
        if (parentTag == null || childField == null) {
            return
        }
        for (formFieldOptionsView in fieldRenderer.formFieldOptionCollectionList) {
            if (formFieldOptionsView.formField.tag == parentTag) {
                for (field in fieldRenderer.formFieldOptionList) {
                    val formFieldView = fieldRenderer.formFieldViewMap[field.formField.tag]!!
                    val container = formFieldView.view as LinearLayout
                    val menuNameContainer =
                        container.findViewById<LinearLayout>(R.id.dynamix_menu_name_container)
                    val menuName: AppCompatTextView =
                        menuNameContainer.findViewById(R.id.dynamix_menu_name)
                    val index = fieldRenderer.formFieldOptionList.indexOf(field)
                    if (i == index) {
                        if (index == 0) {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, selectedOptionBgLeft)
                        } else if (index == size - 1) {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, selectedOptionBgRight)
                        } else {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, selectedOptionBg)
                        }
                    } else {
                        if (index == 0) {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, unSelectedOptionBgLeft)
                        } else if (index == size - 1) {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, unSelectedOptionBgRight)
                        } else {
                            menuNameContainer.background =
                                ContextCompat.getDrawable(ctx, unSelectedOptionBg)
                        }
                    }
                    if (formFieldView.formField.tag.equals(childField.tag, ignoreCase = true)) {
                        menuName.setTextColor(
                            getColorFromThemeAttributes(
                                ctx,
                                selectedOptionTextColor
                            )
                        )
                        field.formField.isOptionSelected = true
                        fieldRenderer = fieldRenderer.copy(
                            optionsType = field.formField.tag!!
                        )
                    } else {
                        menuName.setTextColor(
                            getColorFromThemeAttributes(ctx, unSelectedOptionTextColor)
                        )
                        field.formField.isOptionSelected = false
                    }
                }
            }
        }
        fieldRenderer.formDataProvider.dynamixSetupOptionsChangedListener()
    }

    private fun addOptionField(
        parentField: DynamixFormField,
        childField: DynamixFormField,
        i: Int,
        size: Int
    ): View {
        val container = LinearLayout(ctx)
        container.orientation = LinearLayout.VERTICAL
        val containerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        containerParams.setMargins(
            0, dpToPx(ctx, optionTopMargin),
            0, dpToPx(ctx, optionBottomMargin)
        )
        container.layoutParams = containerParams
        val menuNameContainer = LinearLayout(ctx)
        menuNameContainer.id = R.id.dynamix_menu_name_container
        menuNameContainer.orientation = LinearLayout.VERTICAL
        if (i == 0) {
            if (childField.isOptionSelected) {
                menuNameContainer.background = ContextCompat.getDrawable(ctx, selectedOptionBgLeft)
            } else {
                menuNameContainer.background =
                    ContextCompat.getDrawable(ctx, unSelectedOptionBgLeft)
            }
        } else if (i == size - 1) {
            if (childField.isOptionSelected) {
                menuNameContainer.background =
                    ContextCompat.getDrawable(ctx, selectedOptionBgRight)
            } else {
                menuNameContainer.background =
                    ContextCompat.getDrawable(ctx, unSelectedOptionBgRight)
            }
        } else {
            if (childField.isOptionSelected) {
                menuNameContainer.background = ContextCompat.getDrawable(ctx, selectedOptionBg)
            } else {
                menuNameContainer.background = ContextCompat.getDrawable(ctx, unSelectedOptionBg)
            }
        }
        val imageContainerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageContainerParams.setMargins(0, 0, 0, 0)
        menuNameContainer.layoutParams = imageContainerParams
        val menuName =
            AppCompatTextView(
                ContextThemeWrapper(
                    ctx,
                    R.style.ThemeOverlay_Dynamix_GenericForm_Chip
                )
            )
        val menuNameParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        menuNameParams.topMargin = dpToPx(ctx, 2f)
        menuName.setPadding(
            dpToPx(ctx, 2f),
            dpToPx(ctx, 2f),
            dpToPx(ctx, 2f),
            dpToPx(ctx, 2f)
        )
        menuName.layoutParams = menuNameParams
        menuName.id = R.id.dynamix_menu_name
        menuName.text = childField.label
        menuName.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        menuName.gravity = Gravity.CENTER
        if (childField.isOptionSelected) {
            menuName.setTextColor(
                getColorFromThemeAttributes(
                    ctx,
                    selectedOptionTextColor
                )
            )
        } else {
            menuName.setTextColor(
                getColorFromThemeAttributes(
                    ctx,
                    unSelectedOptionTextColor
                )
            )
        }
        menuNameContainer.addView(menuName)
        container.addView(menuNameContainer)
        container.setOnClickListener {
            optionSelectedItem(
                parentField.tag,
                childField,
                i,
                size
            )
        }
        if (childField.isOptionSelected) {
            fieldRenderer = fieldRenderer.copy(
                optionsType = childField.tag!!
            )
        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = childField
        formFieldView.view = container
        childField.tag?.let {
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        fieldRenderer.formFieldOptionList.add(formFieldView)
        return container
    }

    override fun render(field: DynamixFormField): View {
        val parentContainer = LinearLayout(ctx)
        parentContainer.orientation = LinearLayout.VERTICAL
        val optionsContainer = LinearLayout(ctx)
        if (field.isOrientationHorizontal) {
            optionsContainer.orientation = LinearLayout.HORIZONTAL
        } else {
            optionsContainer.orientation = LinearLayout.VERTICAL
        }
        if (field.childFields != null && field.childFields!!.isNotEmpty()) {
            if (field.defaultItemPosition < 1 || field.defaultItemPosition > field.childFields!!.size) {
                field.defaultItemPosition = 1
            }
            field.childFields!![field.defaultItemPosition - 1].isOptionSelected = true

            for (i in field.childFields!!.indices) {
                if (field.childFields!![i].fieldType == DynamixFieldType.OPTION.fieldType) {
                    val optionView = addOptionField(
                        field,
                        field.childFields!![i],
                        i,
                        field.childFields!!.size
                    )
                    (optionView.layoutParams as LinearLayout.LayoutParams).weight = 1f
                    optionsContainer.addView(optionView)
                }
            }
        }
        parentContainer.addView(optionsContainer)
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = parentContainer
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it + "__optionContainer"] = formFieldView
        }
        fieldRenderer.formFieldOptionCollectionList.add(formFieldView)
        return parentContainer
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
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