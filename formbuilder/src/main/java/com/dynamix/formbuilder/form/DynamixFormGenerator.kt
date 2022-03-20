package com.dynamix.formbuilder.form

import DynamixFormFieldConstants
import DynamixViewContainer
import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dynamix.formbuilder.R
import com.dynamix.core.extensions.dp
import com.dynamix.core.extensions.selectedItemPosition
import com.dynamix.core.extensions.setSelectedIndex
import com.dynamix.core.extensions.toAutoCompleteTextView
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.core.utils.DynamixViewUtils
import com.dynamix.core.utils.permission.PermissionConstants
import com.dynamix.formbuilder.DynamixFormConfigurations
import com.dynamix.formbuilder.contact.DynamixContactSuggestionAdapter
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.DynamixCancelButtonFieldView
import com.dynamix.formbuilder.fields.DynamixCustomButtonFieldView
import com.dynamix.formbuilder.fields.DynamixSubmitButtonFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.image.DynamixAttachment
import com.dynamix.formbuilder.image.DynamixImagePreviewBottomSheet
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yalantis.ucrop.UCrop
import imageview.avatar.com.avatarimageview.GlideApp
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DynamixFormGenerator(
    private val ctx: Context,
    private val formCode: String,
    private val font: Int,
    private val formDataProvider: DynamixFormDataProvider,
    private val navigationProvider: NavigationProvider,
    private val appLoggerProvider: AppLoggerProvider,
    var formFieldViewMap: MutableMap<String, DynamixFormFieldView>,
    private var formFieldList: MutableList<DynamixFormFieldView>,
    private var formLabelList: MutableList<DynamixFormFieldView>,
    private var formFieldOptionCollectionList: MutableList<DynamixFormFieldView>,
    private var formFieldOptionList: MutableList<DynamixFormFieldView>,
    private var viewsNotInFormFieldList: MutableList<View>?,
    private var enableAutoFocus: Boolean = true
) {
    private var viewContainer: LinearLayout

    private lateinit var prefixMainContainer: ViewGroup
    private lateinit var mainContainer: ViewGroup
    private lateinit var suffixMainContainer: ViewGroup

    var fieldMap = listOf<DynamixFormField>()

    private var imageWrapToSetImageAfterSelection: ViewGroup? = null
    private var clearImageView: ImageView? = null
    var allowMultipleImages = false
    private var attachmentMap: MutableMap<String, MutableList<DynamixAttachment>> = HashMap()
    private var fileName: String? = null
    private var recyclerViewDisabler: DynamixRecyclerViewDisabler? = null

    private val contactFetcher: com.dynamix.formbuilder.contact.DynamixContactFetcher =
        com.dynamix.formbuilder.contact.DynamixContactFetcher()

    @Deprecated("use attachmentMap if possible")
    val fileMap: HashMap<String, MutableList<File>> = HashMap()
    private var imageUploadTag: String? = null

    private var optionsType = ""
    private val mOptionsImage: AppCompatImageView? = null
    val chipTexts: MutableMap<String, String> = HashMap()

    var requestData: MutableMap<String, Any> = HashMap()

    private val disposeBag = CompositeDisposable()

    private lateinit var fieldDataHolder: DynamixFieldDataHolder

    init {
        viewContainer = LinearLayout(ctx)
        viewContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        viewContainer.orientation = LinearLayout.VERTICAL

        fieldMap = emptyList()
        recyclerViewDisabler = DynamixRecyclerViewDisabler()
    }

    fun registerViews(
        prefixMainContainer: ViewGroup,
        mainContainer: ViewGroup,
        suffixMainContainer: ViewGroup
    ) {
        this.prefixMainContainer = prefixMainContainer
        this.mainContainer = mainContainer
        this.suffixMainContainer = suffixMainContainer
    }

    fun registerFields(fieldMap: List<DynamixFormField>) {
        this.fieldMap = fieldMap
        registerFieldDataHolder()
    }

    fun processFormBuild() {
        mainContainer.isVisible = true
        val formView = getFormView()
        mainContainer.addView(formView)
    }

    private fun registerFieldDataHolder() {
        //This is a container for all data needed to render a view...
        fieldDataHolder = DynamixFieldDataHolder(
            ctx = ctx,
            appLoggerProvider = appLoggerProvider,
            formDataProvider = formDataProvider,
            navigationProvider = navigationProvider,
            formFieldList = formFieldList,
            formLabelList = formLabelList,
            formFieldViewMap = formFieldViewMap,
            contactFetcher = contactFetcher,
            viewsNotInFormFieldList = viewsNotInFormFieldList!!,
            formCode = formCode,
            formFieldOptionCollectionList = formFieldOptionCollectionList,
            formFieldOptionList = formFieldOptionList,
            font = font,
            fileMap = fileMap,
            attachmentMap = attachmentMap,
            chipTexts = chipTexts
        )
    }

    fun fieldDataHolder(): DynamixFieldDataHolder {
        return fieldDataHolder
    }

    private fun getFormView(): View {
        addFormFields()
        addButton(null)
        addCancelButton(null)
        addPostFormFields()
        return setViewContainerLayoutParams(viewContainer)
    }

    fun setupFieldListeners() {
        setupSpinnerListeners()
        checkChangedListener()
        setupOptionsChangedListener()
        radioChangedListener()
        initializeContactFetching()
    }

    private fun addFormFields() {
        for (field in fieldMap) {
            var label: View? = null
            var formView: View? = null

            if (DynamixFormConfigurations.registeredFieldTypes.containsKey(field.fieldType)
            ) {
                val dynamicFormView =
                    DynamixFormConfigurations.registeredFieldTypes[field.fieldType]?.init(ctx, fieldDataHolder)!!.renderField(
                        field
                    )
                label = dynamicFormView.labelView
                formView = dynamicFormView.formView
            }
            // add the fields
            label?.let {
                it.layoutParams = it.layoutParams ?: LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    marginStart = 16.dp(ctx)
                    marginEnd = 16.dp(ctx)
                    bottomMargin = 12.dp(ctx)
                }
                viewContainer.addView(it)
            }
            formView?.let {
                applyFieldViewMargin(it)
                when (field.containerType) {
                    DynamixViewContainer.PREFIX_MAIN_CONTAINER -> {
                        formDataProvider.dynamixPrefixMainContainerFieldsAdded()
                        prefixMainContainer.isVisible = true
                        prefixMainContainer.addView(it)
                    }
                    DynamixViewContainer.SUFFIX_MAIN_CONTAINER -> {
                        formDataProvider.dynamixSuffixMainContainerFieldsAdded()
                        suffixMainContainer.isVisible = true
                        suffixMainContainer.addView(it)
                    }
                    else -> {
                        viewContainer.addView(it)
                    }
                }
            }
        }
        if (enableAutoFocus) {
            for (formFieldView in formFieldList) {
                if (formFieldView.view is TextInputLayout || formFieldView.view is TextInputEditText) {
                    formFieldView.view.requestFocus()
                    break
                }
            }
        }
        formDataProvider.dynamixOnMainContainerFormFieldAdded()
    }

    private fun addPostFormFields() {
        for (field in fieldMap) {
            if (DynamixFormConfigurations.postFixFormFieldTypes.containsKey(field.fieldType)
            ) {
                val dynamicFormView =
                    DynamixFormConfigurations.postFixFormFieldTypes[field.fieldType]?.init(ctx, fieldDataHolder)!!.renderField(
                        field
                    )
                val fieldView = dynamicFormView.formView!!
                applyFieldViewMargin(fieldView)
                viewContainer.addView(fieldView)
            }
        }
        formDataProvider.dynamixOnPostContainerFormFieldAdded()
    }

    private fun setImageOfOptionsField(imageUrl: String?) {
        GlideApp.with(ctx)
            .load(imageUrl)
            .into(mOptionsImage!!)
    }

    private fun setParams(params: LinearLayout.LayoutParams, formField: DynamixFormField) {
        params.setMargins(0, 0, 0, DynamixConverter.dpToPx(ctx, 17))
    }

    private fun isAutoCompleteTextField(field: DynamixFormField): Boolean =
        field.fieldType == DynamixFieldType.PHONE.fieldType || field.fieldType == DynamixFieldType.PHONE_EMAIL.fieldType

    private fun addButton() {
        val buttonView = DynamixCustomButtonFieldView.button(
            ctx
        ) { onSubmitButtonClick() }
        applyFieldViewMargin(buttonView, -1, -1, 10)
        viewContainer.addView(buttonView)
    }

    private fun addButton(submitButton: String?) {
        val buttonView = DynamixSubmitButtonFieldView(
            ctx,
            submitButton,
        ) { onSubmitButtonClick() }.render(DynamixFormField())
        applyFieldViewMargin(buttonView, -1, -1, 10)
        viewContainer.addView(buttonView)
    }

    private fun addCancelButton() {
        val buttonView = DynamixCustomButtonFieldView.cancelButton(
            ctx
        ) { onCancelButtonClick() }
        applyFieldViewMargin(buttonView)
        viewContainer.addView(buttonView)
    }

    private fun addCancelButton(label: String?) {
        val buttonView = DynamixCancelButtonFieldView(
            ctx,
            label,
        ) { onCancelButtonClick() }.render(DynamixFormField())
        applyFieldViewMargin(buttonView)
        viewContainer.addView(buttonView)
    }

    private fun onSubmitButtonClick() {
        formDataProvider.dynamixHideKeyboardIfOpened()
        formDataProvider.dynamixOnSubmitButtonClicked()
    }

    private fun onCancelButtonClick() {
        formDataProvider.dynamixHideKeyboardIfOpened()
        formDataProvider.dynamixOnCancelButtonClicked()
    }

    private fun applyFieldViewMargin(
        formView: View,
        viewStartMargin: Int = -1,
        viewEndMargin: Int = -1,
        viewBottomMargin: Int = -1
    ) {
        val startMarginDp: Int = if (viewStartMargin != -1) {
            viewStartMargin.dp(ctx)
        } else {
            16.dp(ctx)
        }
        val endMarginDp: Int = if (viewEndMargin != -1) {
            viewEndMargin.dp(ctx)
        } else {
            16.dp(ctx)
        }
        val bottomMarginDp: Int = if (viewBottomMargin != -1) {
            viewBottomMargin.dp(ctx)
        } else {
            24.dp(ctx)
        }
        formView.let {
            it.layoutParams = it.layoutParams ?: LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                marginStart = startMarginDp
                marginEnd = endMarginDp
                bottomMargin = bottomMarginDp
            }
            if (it.layoutParams !is ViewGroup.MarginLayoutParams) {
                it.layoutParams =
                    ViewGroup.MarginLayoutParams(it.layoutParams.height, it.layoutParams.width)
                        .apply {
                            marginStart = startMarginDp
                            marginEnd = endMarginDp
                            bottomMargin = bottomMarginDp
                        }
            }
        }
    }

    private fun setViewContainerLayoutParams(view: View): View {
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
            topMargin = 24.dp(ctx)
        }
        return view
    }

    fun requestImageSelection(field: DynamixFormField) {
        fileMap[field.tag!!] = ArrayList()
        imageUploadTag = field.tag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ctx as AppCompatActivity).requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), PermissionConstants.REQ_CODE_SELECT_IMAGE
            )
        } else {
            selectImage()
        }
    }

    fun onImageTap(field: DynamixFormField, imageHolderWrap: LinearLayout, clearImage: ImageView) {
        imageUploadTag = field.tag
        imageWrapToSetImageAfterSelection = imageHolderWrap
        clearImageView = clearImage
        allowMultipleImages = field.isAllowMultipleImages
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ctx as AppCompatActivity).requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ), PermissionConstants.REQ_CODE_SELECT_IMAGE
            )
        } else {
            selectImage()
        }
    }

    fun selectImage() {
        formDataProvider.dynamixSelectImage(allowMultipleImages)
    }

    fun getFiles(tag: String?): List<File> {
        return if (fileMap[tag] == null) ArrayList() else fileMap[tag]!!
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val fileName = UUID.randomUUID().toString()
        val storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            fileName,
            ".jpg",
            storageDir
        )
    }

    private fun hasPhoneFieldRecursive(
        fields: List<DynamixFormField>,
        hasPhoneField: Boolean
    ): Boolean {
        var checkHasPhoneField = hasPhoneField
        if (checkHasPhoneField) {
            return true
        }
        for (field in fields) {
            checkHasPhoneField =
                field.fieldType == DynamixFieldType.PHONE.fieldType || field.fieldType == DynamixFieldType.PHONE_EMAIL.fieldType
            if (checkHasPhoneField) {
                break
            }
        }
        return checkHasPhoneField
    }

    private fun hasPhoneField(): Boolean {
        return hasPhoneFieldRecursive(fieldMap, false)
    }

    private fun initializeContactFetching() {
        if (hasPhoneField()) {
            val disposable = contactFetcher.queryContacts(ctx as AppCompatActivity)
            if (disposable != null) {
                disposeBag.add(disposable)
            }
            disposeBag.add(
                contactFetcher.contactList
                    .subscribe({
                        // initialize suggestions to all phone fields
                        setupContactSuggestionFieldRecursive(fieldMap, it)
                    }) { appLoggerProvider.error(it) })
        }
    }

    private fun setupContactSuggestionFieldRecursive(
        fields: List<DynamixFormField>,
        contacts: List<com.dynamix.formbuilder.contact.DynamixContact>
    ) {
        for (field in fields) {
            setupContactSuggestionForField(field, contacts)
        }
    }

    private fun setupContactSuggestionForField(
        field: DynamixFormField,
        contacts: List<com.dynamix.formbuilder.contact.DynamixContact>
    ) {
        if ((field.fieldType == DynamixFieldType.PHONE.fieldType || field.fieldType == DynamixFieldType.PHONE_EMAIL.fieldType) && !field.isDisableContactSearch) {
            val editText = (formFieldViewMap[field.tag]!!.view as TextInputLayout).editText
            addContactSuggestion(editText, contacts)
        }
    }

    private fun addContactSuggestion(
        editText: EditText?,
        contacts: List<com.dynamix.formbuilder.contact.DynamixContact>
    ) {
        val autoCompleteTextView = editText as AppCompatAutoCompleteTextView?
        val adapter =
            DynamixContactSuggestionAdapter(
                ctx,
                R.layout.dynamix_contact_suggestion_list_item,
                contacts.toMutableList()
            )
        autoCompleteTextView!!.threshold = 1
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun setupSpinnerListeners() {
        for (formFieldView in formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.SPINNER.fieldType) {
                val spinner =
                    ((formFieldView.view as TextInputLayout).editText as MaterialAutoCompleteTextView)
                spinner.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    spinnerItemSelected(formFieldView, spinner.text.toString())
                    spinnerVisibilityControl(formFieldView, position)
                }
                // auto trigger listeners when we do our first layout
                // This is done as a backward compatible way to Spinner, in future need to find a better
                // solution
                formFieldView.view.doOnLayout {
                    spinnerItemSelected(formFieldView, spinner.text.toString())
                    spinnerVisibilityControl(formFieldView, spinner.selectedItemPosition())
                }
            }
            if (formFieldView.formField.fieldType == DynamixFieldType.SPINNER_SEARCH.fieldType) {
                val spinnerSearchTextView =
                    (formFieldView.view as TextInputLayout).editText as AutoCompleteTextView
                val indexes: List<String> = ArrayList(formFieldView.formField.options!!.values)
                val index = indexes.indexOf(spinnerSearchTextView.text.toString())
                spinnerSearchVisibilityControl(formFieldView, index)
            }
        }
    }

    private fun checkChangedListener() {
        for (formFieldView in formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.CHECKBOX.fieldType) {
                for (input_checkbox in formFieldView.checkBoxes!!) {
                    for (formFieldView1 in formFieldList) {
                        if (formFieldView1.formField.visibilityParent != null && formFieldView1.formField.visibilityParent == input_checkbox.tag) {
                            for (labelFormField in formLabelList) {
                                if (labelFormField.formField.visibilityParent != null && labelFormField.formField.visibilityParent == input_checkbox.tag) {
                                    val label = labelFormField.view
                                    if (input_checkbox.isChecked) {
                                        if (label != null) {
                                            label.isVisible = true
                                        }
                                    } else {
                                        if (label != null) {
                                            label.isVisible = false
                                        }
                                    }
                                }
                                if (input_checkbox.isChecked) {
                                    formFieldView1.view.isVisible = true
                                    formFieldView1.formField.isIgnoreField = false
                                    if (formFieldView1.formField.isRequired) {
                                        formFieldView1.formField.isValidateIgnoreField = true
                                    }
                                } else {
                                    if (formFieldView1.formField.visibilityParent != null) {
                                        formFieldView1.view.isVisible = false
                                        formFieldView1.formField.isIgnoreField = true
                                        if (formFieldView1.formField.isRequired) {
                                            formFieldView1.formField.isValidateIgnoreField = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun setupOptionsChangedListener() {
        if (formFieldOptionCollectionList.isEmpty() || formFieldOptionList.isEmpty()) {
            return
        }
        for (formFieldView in formFieldOptionList) {
            for (formFieldView1 in formFieldList) {
                if (formFieldView1.formField.visibilityParent != null && formFieldView1.formField.visibilityParent == formFieldView.formField.tag) {
                    if (formFieldView.formField.isOptionSelected) {
                        formFieldView1.view.isVisible = true
                        formFieldView1.formField.isIgnoreField = false
                        if (formFieldView1.formField.isRequired) {
                            formFieldView1.formField.isValidateIgnoreField = true
                        }
                    } else {
                        if (formFieldView1.formField.visibilityParent != null) {
                            formFieldView1.view.isVisible = false
                            formFieldView1.formField.isIgnoreField = true
                            if (formFieldView1.formField.isRequired) {
                                formFieldView1.formField.isValidateIgnoreField = false
                            }
                        }
                    }
                }
                handleOptionsChanged(formFieldView, formFieldView1)
            }
            for (labelFormField in formLabelList) {
                if (labelFormField.formField.visibilityParent != null &&
                    formFieldView.formField.tag.equals(
                        labelFormField.formField.visibilityParent,
                        ignoreCase = true
                    )
                ) {
                    if (formFieldView.formField.isOptionSelected) {
                        labelFormField.view.isVisible = true
                    } else {
                        if (labelFormField.formField.visibilityParent != null) {
                            labelFormField.view.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun handleOptionsChanged(
        parentView: DynamixFormFieldView,
        childView: DynamixFormFieldView
    ) {
        formDataProvider.dynamixHandleOptionsChanged(parentView, childView)
    }

    private fun radioChangedListener() {
        for (formFieldView in formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.RADIO.fieldType) {
                for (inputRadioButton in formFieldView.radioButtons!!) {
                    for (formFieldView1 in formFieldList) {
                        if (formFieldView1.formField.visibilityParent != null &&
                            formFieldView1.formField.visibilityParent.equals(
                                formFieldView.formField.tag,
                                ignoreCase = true
                            ) && formFieldView1.formField.visibilityValues != null &&
                            formFieldView1.formField.visibilityValues!!.isNotEmpty()
                        ) {
                            for (visibilityValue in formFieldView1.formField.visibilityValues!!) {
                                if (visibilityValue == inputRadioButton.tag) {
                                    if (inputRadioButton.isChecked) {
                                        formFieldView1.view.isVisible = true
                                        formFieldView1.formField.isIgnoreField = false
                                        if (formFieldView1.formField.isRequired) {
                                            formFieldView1.formField.isValidateIgnoreField = true
                                        }
                                    } else {
                                        if (formFieldView1.formField.visibilityParent != null) {
                                            formFieldView1.view.isVisible = false
                                            formFieldView1.formField.isIgnoreField = true
                                            if (formFieldView1.formField.isRequired) {
                                                formFieldView1.formField.isValidateIgnoreField =
                                                    false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (labelFormField in formLabelList) {
                        if (labelFormField.formField.visibilityParent != null &&
                            labelFormField.formField.visibilityParent.equals(
                                formFieldView.formField.tag,
                                ignoreCase = true
                            ) && labelFormField.formField.visibilityValues != null &&
                            labelFormField.formField.visibilityValues!!.isNotEmpty()
                        ) {
                            for (visibilityValue in labelFormField.formField.visibilityValues!!) {
                                if (visibilityValue == inputRadioButton.tag) {
                                    val label = labelFormField.view
                                    if (inputRadioButton.isChecked) {
                                        if (label != null) {
                                            label.isVisible = true
                                        }
                                    } else {
                                        if (label != null) {
                                            label.isVisible = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun spinnerItemSelected(formFieldView: DynamixFormFieldView, selectedItem: String) {
        val key =
            DynamixCommonUtils.getKeyFromValueInHashMap(
                formFieldView.formField.options,
                selectedItem
            )
        formDataProvider.dynamixKeyOfSpinnerItemSelected(formFieldView.formField.tag, key)
        formDataProvider.dynamixValueOfSpinnerItemSelected(formFieldView.formField, selectedItem)
    }

    private fun spinnerVisibilityControl(fieldView: DynamixFormFieldView, position: Int) {
        for (formFieldView in formFieldList) {
            if (formFieldView.formField.fieldType == DynamixFieldType.RADIO.fieldType && formFieldView.formField.tag.equals(
                    DynamixFormFieldConstants.PAY_TO,
                    ignoreCase = true
                )
            ) {
                continue
            }
            val formField1 = formFieldView.formField
            if (formField1.visibilityParent != null &&
                fieldView.formField.tag.equals(formField1.visibilityParent, ignoreCase = true)
            ) {
                val selectedKey = DynamixCommonUtils.getKeyFromValueInHashMap(
                    fieldView.formField.options,
                    fieldView.view.toAutoCompleteTextView().text.toString()
                )
                if (formField1.visibilityValues != null && formField1.visibilityValues!!.contains(
                        selectedKey
                    )
                ) {
                    formFieldView.view.isVisible = true
                    formField1.isIgnoreField = false
                    for (labelFormField in formLabelList) {
                        if (labelFormField.formField.visibilityParent != null && formField1.fieldType == DynamixFieldType.RADIO.fieldType &&
                            labelFormField.formField.visibilityParent.equals(
                                DynamixFormFieldConstants.PAY_TO,
                                ignoreCase = true
                            )
                        ) {
                            continue
                        }
                        if (labelFormField.formField.visibilityParent != null &&
                            labelFormField.formField.visibilityParent.equals(
                                fieldView.formField.tag,
                                ignoreCase = true
                            ) &&
                            labelFormField.formField.visibilityValues!!.contains(selectedKey)
                        ) {
                            labelFormField.view.isVisible = true
                        } else {
                            if (labelFormField.formField.visibilityParent != null) {
                                labelFormField.view.isVisible = false
                            }
                        }
                    }
                } else {
                    formFieldView.view.isVisible = false
                    if (formFieldView.formField.fieldType == DynamixFieldType.CHECKBOX.fieldType) {
                        for (checkBox in formFieldView.checkBoxes!!) {
                            checkBox.isSelected = false
                        }
                    }
                    formField1.isIgnoreField = true
                    for (labelFormField in formLabelList) {
                        if (labelFormField.formField.visibilityParent != null && labelFormField.formField.visibilityParent.equals(
                                DynamixFormFieldConstants.PAY_TO,
                                ignoreCase = true
                            )
                        ) {
                            continue
                        }
                        if (labelFormField.formField.visibilityParent != null &&
                            labelFormField.formField.visibilityParent.equals(
                                fieldView.formField.tag,
                                ignoreCase = true
                            ) && labelFormField.formField.visibilityValues != null &&
                            labelFormField.formField.visibilityValues!!.contains(selectedKey)
                        ) {
                            labelFormField.view.isVisible = true
                        } else {
                            if (labelFormField.formField.visibilityParent != null) {
                                labelFormField.view.isVisible = false
                            }
                        }
                    }
                }
            }
            if (formField1.parentSpinner != null &&
                fieldView.formField.tag.equals(formField1.parentSpinner, ignoreCase = true)
            ) {
                //get the child spinner values based on parent spinner
                val values: Collection<String> = formField1.spinnerMultiItems!![position].values
                val array = values.toTypedArray()
                val arrayAdapter = ArrayAdapter(
                    ctx,
                    R.layout.dynamix_spinner_item, array
                )
                arrayAdapter.setDropDownViewResource(R.layout.dynamix_spinner_dropdown_item)
                formFieldView.view.toAutoCompleteTextView().setAdapter(arrayAdapter)
                if (array.isNotEmpty()) {
                    formFieldView.view.toAutoCompleteTextView().setSelectedIndex(0)
                }
            }
        }
    }

    private fun spinnerSearchVisibilityControl(fieldView: DynamixFormFieldView, position: Int) {
        var formField1: DynamixFormField
        for (formFieldView in formFieldList) {
            formField1 = formFieldView.formField
            if (formField1.visibilityParent != null &&
                fieldView.formField.tag.equals(
                    formField1.visibilityParent,
                    ignoreCase = true
                ) && fieldView.formField.fieldType != DynamixFieldType.HIDDEN.fieldType
            ) {
                val selectedKey = DynamixCommonUtils.getKeyFromValueInHashMap(
                    fieldView.formField.options,
                    (fieldView.view as AppCompatTextView).text.toString()
                )
                if (formField1.visibilityValues!!.contains(selectedKey)) {
                    formFieldView.view.isVisible = true
                    formField1.isIgnoreField = false
                    for (labelFormField in formLabelList) {
                        if (labelFormField.formField.visibilityParent != null &&
                            fieldView.formField.tag.equals(
                                labelFormField.formField.visibilityParent, ignoreCase = true
                            ) &&
                            labelFormField.formField.visibilityValues!!.contains(selectedKey)
                        ) {
                            labelFormField.view.isVisible = true
                        } else {
                            if (labelFormField.formField.visibilityParent != null) {
                                labelFormField.view.isVisible = false
                            }
                        }
                    }
                } else {
                    formFieldView.view.isVisible = false
                    formField1.isIgnoreField = true
                }
            }
            if (formField1.parentSpinner != null &&
                fieldView.formField.tag.equals(formField1.parentSpinner, ignoreCase = true)
            ) {
                //get the child spinner values based on parent spinner
                val values: Collection<String> = formField1.spinnerMultiItems!![position].values
                val array = values.toTypedArray()
                (formFieldView.view as AppCompatTextView).text = array[0]
                formField1.options = formField1.spinnerMultiItems!![position]
            }
        }
    }

    private fun enableFormFields(enabled: Boolean) {
        for (formFieldView in formFieldList) {
            val view = formFieldView.view
            view.isEnabled = enabled
            view.isClickable = enabled
            view.isFocusable = enabled
            if (formFieldView.formField.fieldType == DynamixFieldType.AMOUNT.fieldType) {
                val amountView = view as ConstraintLayout
                for (childPosition in 0 until amountView.childCount) {
                    val childView = amountView.getChildAt(childPosition)
                    childView.isEnabled = enabled
                    childView.isClickable = enabled
                    childView.isFocusable = enabled
                    if (childView is RecyclerView) {
                        enableFormFieldViews(childView as ViewGroup, enabled)
                        if (enabled) {
                            childView.removeOnItemTouchListener(
                                recyclerViewDisabler!!
                            )
                        } else {
                            childView.addOnItemTouchListener(recyclerViewDisabler!!)
                        }
                    }
                }
            }
        }
        for (formFieldView in formFieldOptionCollectionList) {
            val view = formFieldView.view
            view.isEnabled = enabled
            view.isClickable = enabled
            view.isFocusable = enabled
        }
        for (formFieldView in formFieldOptionList) {
            val view = formFieldView.view
            view.isEnabled = enabled
            view.isClickable = enabled
            view.isFocusable = enabled
        }
        for (view in viewsNotInFormFieldList!!) {
            view.isEnabled = enabled
            view.isClickable = enabled
            view.isFocusable = enabled
        }
    }

    private fun enableFormFieldViews(layout: ViewGroup, enabled: Boolean) {
        layout.isClickable = enabled
        layout.isFocusable = enabled
        layout.isEnabled = enabled
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is ViewGroup) {
                enableFormFieldViews(child, enabled)
            } else {
                child.isClickable = enabled
                child.isFocusable = enabled
                child.isEnabled = enabled
            }
        }
    }

    fun onDataSelectedFromSpinner(tag: String?, value: String?) {
        val formFieldView = formFieldViewMap[tag]
        if (formFieldView != null) {
            ((formFieldView.view as TextInputLayout).editText as AutoCompleteTextView).setText(value)
            val indexes: List<String?> =
                java.util.ArrayList(formFieldView.formField.options!!.values)
            val index = indexes.indexOf(value)
            spinnerSearchVisibilityControl(formFieldView, index)
        }
    }

    class DynamixRecyclerViewDisabler : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    fun parseImage(intent: Intent) {
        val imageUri = UCrop.getOutput(intent)
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
        val input =
            ctx.contentResolver.openInputStream(imageUri!!)
        val out = FileOutputStream(file)
        BitmapFactory.decodeStream(input)
            .compress(Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        val selectedImageUri = Uri.fromFile(file)
        manageImage(selectedImageUri)
    }

    private fun manageImage(fileUri: Uri?) {
        if (imageUploadTag != null) {
            val fieldView = formFieldViewMap[imageUploadTag]
            if (fieldView != null && fieldView.formField.fieldType == DynamixFieldType.IMAGE_PREVIEW.fieldType) {
                setImageToImagePickerField(fieldView, fileUri!!)
            } else {
                val filePath = getRealPathFromUri(fileUri)
                val file = File(filePath!!)
                if (filePath.isNotEmpty()) {
                    setImageToImageField(fileUri, file)
                }
            }
        }
    }

    private fun setImageToImagePickerField(
        fieldView: DynamixFormFieldView,
        fileUri: Uri,
    ) {
        val imagePicker = fieldView.view
        val imageView = imagePicker.findViewById<ImageView>(R.id.lt_ff_iu_image)
        val infoLayout = imagePicker.findViewById<LinearLayout>(R.id.lt_ff_iu_upload_layout)
        if (imageView != null) {
            imageView.setImageURI(fileUri)
            // remove the default value from the field
            fieldView.formField.defaultValue = null
            DynamixViewUtils.setVisible(infoLayout, false)
            DynamixViewUtils.setVisible(imageView, true)
            imageView.setOnClickListener {
                val bottomSheet = DynamixImagePreviewBottomSheet(
                    fieldView.formField.label,
                    fileUri
                ) { requestImageSelection(fieldView.formField) }
                bottomSheet.showNow((ctx as AppCompatActivity).supportFragmentManager, "")
            }
            addFile(fieldView, fileUri, null)
        }
    }

    fun addFile(
        fieldView: DynamixFormFieldView,
        fileUri: Uri?,
        index: Int?
    ) {
        try {
            fileName = fileUri?.lastPathSegment
            val attachment = DynamixAttachment(
                fileName!!, ctx.contentResolver.openInputStream(
                    fileUri!!
                )!!, fileUri
            )
            attachment.tag = fieldView.formField.tag
            val tag = fieldView.formField.tag
            if (attachmentMap.containsKey(tag)) {
                if (!attachment.tag!!.equals(
                        DynamixFormFieldConstants.DOCUMENT_IMAGE_LIST,
                        ignoreCase = true
                    )
                ) {
                    attachmentMap[tag]!!.clear()
                    attachmentMap[tag]!!.add(attachment)
                } else {
                    if (index != null) {
                        attachmentMap[tag]!![index] = attachment
                    } else {
                        attachmentMap[tag]!!.add(attachment)
                    }
                }
            } else {
                val attachments: MutableList<DynamixAttachment> = java.util.ArrayList()
                attachments.add(attachment)
                attachmentMap[imageUploadTag!!] = attachments
            }
        } catch (e: Exception) {
            appLoggerProvider.error(e)
        }
    }

    protected fun getFileNameFromUri(uri: Uri?): String {
        val cursor = ctx.contentResolver.query(uri!!, null, null, null, null)
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val name = cursor.getString(nameIndex)
        cursor.close()
        return name
    }

    private fun setImageToImageField(fileUri: Uri?, file: File) {
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            DynamixConverter.dpToPx(ctx, 100f)
        )
        // root image layout to host both card and clear button
        val imageLayout = FrameLayout(ctx)
        val imageLayoutLp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val imageView = ImageView(ctx)
        imageView.layoutParams = lp
        imageView.adjustViewBounds = true
        imageView.setImageURI(fileUri)
        val removeButtonSize = 24

        // image cardView layout
        val cardLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // add margin to give space for the remove button
        cardLayoutParams.setMargins(
            0,
            DynamixConverter.dpToPx(ctx, removeButtonSize / 2f + 8),
            DynamixConverter.dpToPx(ctx, removeButtonSize / 2f + 8),
            DynamixConverter.dpToPx(ctx, 8f)
        )
        val cardView = CardView(ctx)
        cardView.radius = DynamixConverter.dpToPx(ctx, 4f).toFloat()
        val imageLp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            DynamixConverter.dpToPx(ctx, 100f)
        )
        // store the reference to the current selected field so that it can be accessed in callbacks
        val rootView = imageWrapToSetImageAfterSelection
        val currentImageTag = imageUploadTag
        cardView.addView(imageView, imageLp)
        val removeButton = ImageView(ctx)
        removeButton.setBackgroundResource(R.drawable.dynamix_cr_bg_round_primary)
        removeButton.backgroundTintList = ColorStateList.valueOf(
            DynamixResourceUtils.getColorFromThemeAttributes(
                ctx,
                R.attr.colorSurface
            )
        )
        removeButton.setImageDrawable(
            ContextCompat.getDrawable(
                ctx,
                R.drawable.dynamix_ic_cancel_outline,
            )
        )
        removeButton.imageTintList =
            ColorStateList.valueOf(DynamixResourceUtils.getColor(ctx, R.color.color_e53135))
        // same or more elevation than the cardView
        removeButton.elevation = cardView.elevation
        val removePadding = DynamixConverter.dpToPx(ctx, 2)
        removeButton.setPadding(removePadding, removePadding, removePadding, removePadding)
        // set the button margin taking cardView margin into account
        val removeLp = FrameLayout.LayoutParams(
            DynamixConverter.dpToPx(ctx, removeButtonSize.toFloat()),
            DynamixConverter.dpToPx(ctx, removeButtonSize.toFloat()),
            Gravity.TOP or Gravity.END
        )
        removeLp.setMargins(
            0,
            DynamixConverter.dpToPx(ctx, 8f),
            DynamixConverter.dpToPx(ctx, 8f),
            0
        )
        removeButton.setOnClickListener {
            rootView!!.removeView(imageLayout)
            fileMap[currentImageTag]!!.remove(file)
            // check if this is the last image
            if (fileMap.isEmpty()) {
                clearImageView!!.isVisible = false
                rootView.removeAllViews()
            }
        }
        // add the views to root image layout
        imageLayout.addView(cardView, cardLayoutParams)
        imageLayout.addView(removeButton, removeLp)
        imageWrapToSetImageAfterSelection!!.addView(imageLayout, imageLayoutLp)
        clearImageView!!.isVisible = true
        var files = fileMap[imageUploadTag]
        if (files == null) {
            files = java.util.ArrayList()
        }
        files.add(file)
        fileMap[imageUploadTag!!] = files
    }

    fun getRealPathFromUri(uri: Uri?): String? {
        if ("file".equals(uri!!.scheme, ignoreCase = true)) {
            // handle file uri first
            return uri.path
        } else if (DocumentsContract.isDocumentUri(ctx, uri)) {
            // DocumentProvider
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // MediaStore (and general)

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                uri,
                null,
                null
            )
        }
        return null
    }

    private fun getDataColumn(
        uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = ctx.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri?): Boolean {
        return "com.android.externalstorage.documents" == uri!!.authority
    }

    private fun isDownloadsDocument(uri: Uri?): Boolean {
        return "com.android.providers.downloads.documents" == uri!!.authority
    }

    private fun isMediaDocument(uri: Uri?): Boolean {
        return "com.android.providers.media.documents" == uri!!.authority
    }

    private fun isGooglePhotosUri(uri: Uri?): Boolean {
        return "com.google.android.apps.photos.content" == uri!!.authority
    }

    fun fetchContacts(
        requestCode: Int,
        grantResults: IntArray
    ) {
        contactFetcher.onRequestPermissionResult(
            ctx as AppCompatActivity,
            requestCode,
            grantResults
        )
    }

    fun validateFields() {
        DynamixFormValidator(
            formFieldList,
            formDataProvider,
        ).validateFields()
    }

    fun makeRequestParameters() {
        DynamixRequestParamsGenerator(
            formFieldList,
            formDataProvider,
            appLoggerProvider
        ) {
            requestData = it.toMutableMap()
        }.makeRequestParameters()
    }

    fun authenticated(txnPassword: String, txnPasswordStatusCode: String) {
        requestData[DynamixFormFieldConstants.TXN_PASSWORD] =
            txnPassword
        if (txnPasswordStatusCode.isNotEmpty()) {
            requestData[DynamixFormFieldConstants.PASSWORD_STATUS_CODE] =
                txnPasswordStatusCode
        }
        formDataProvider.dynamixOnAuthenticated(requestData)
    }

    fun onDestroy() {
        disposeBag.clear()
        viewsNotInFormFieldList!!.clear()
    }
}