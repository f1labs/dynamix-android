package com.dynamix.modsign.model

import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.locale.DynamixLocaleStrings
import com.dynamix.modsign.ModSignController
import com.dynamix.modsign.ModsignConfigurations
import com.google.gson.annotations.SerializedName
import java.util.regex.Pattern

data class RootView(
    val type: String = "",
    @SerializedName("equalWidth")
    val isEqualWidth: Boolean = false,
    val tag: String? = "",
    val width: String = "",
    val height: String = "",
    val padding: String = "",
    val paddingLeft: String = "",
    val paddingTop: String = "",
    val paddingRight: String = "",
    val paddingBottom: String = "",
    val margin: String = "",
    val marginLeft: String = "",
    val marginTop: String = "",
    val marginRight: String = "",
    val marginBottom: String = "",
    @SerializedName("hidden")
    val isHidden: Boolean = false,
    val backgroundColor: String? = null,
    val background: Background? = null,
    val backgroundImage: String? = null,
    val position //center, bottom, top
    : String = "",
    val layoutGravity: String = "",
    val gravity: String = "",

    //Relative
    @SerializedName("centerInParent")
    val isCenterInParent: Boolean = false,
    @SerializedName("centerVertical")
    val isCenterVertical: Boolean = false,
    @SerializedName("centerHorizontal")
    val isCenterHorizontal: Boolean = false,
    @SerializedName("alignParentBottom")
    val isAlignParentBottom: Boolean = false,
    @SerializedName("alignParentEnd")
    val isAlignParentEnd: Boolean = false,
    val leftOf: String? = "",
    val topOf: String? = "",
    val bottomOf: String? = "",
    val rightOf: String? = "",
    @SerializedName("relative")
    val isRelative: Boolean = false,
    private val text: String = "",
    val unicodeText: String? = null,
    val id: String? = "",
    val children: List<RootView>? = emptyList(),
    val style: String? = null,
    val tintMode: String? = null,

    //TextView
    val maxLines: Int = 1,
    val ellipsize: Boolean? = null,
    val textAlignment: String? = "",
    val stripHtmlTags: Boolean? = null,

    //ImageView
    @SerializedName("imageUrl")
    private var _imageUrl: String = "",

    //CardView
    @SerializedName("cardUseCompatPadding")
    val isCardUseCompatPadding: Boolean = false,
    val cardElevation: String = "",
    val cardRadius: String = "",

    //Recycler View
    @SerializedName("dataUrl")
    private var _dataUrl: String? = null,
    @SerializedName("layoutUrl")
    private var _layoutUrl: String = "",
    val dataObject: String = "",
    val layoutType: String = "",
    val itemsPerGrid: Int = 1,
    val maxItems: Int = 0,
    val radioId: String? = "",
    val radioRequired: Boolean = false,

    val visibility: ModSignVisibility? = null,

    //Viewpager
    val viewPagerItems: List<ViewPagerItem> = emptyList(),

    //Events
    val onClick: DynamixEvent? = null
) {

    fun setDataUrl(dataUrl: String) {
        _dataUrl = dataUrl
    }

    fun getDataUrl(): String? {
        if (_dataUrl == null) {
            return null
        }

        val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
        val matcher = pattern.matcher(_dataUrl)
        while (matcher.find()) {
            _dataUrl = _dataUrl!!.replace(
                "{{" + matcher.group(1) + "}}",
                ModsignConfigurations.urlMap[matcher.group(1)] as String,
                true
            )
        }

        return _dataUrl

    }

    fun setLayoutUrl(layoutUrl: String) {
        _layoutUrl = layoutUrl
    }

    fun getLayoutUrl(): String {
        val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
        val matcher = pattern.matcher(_layoutUrl)
        while (matcher.find()) {
            _layoutUrl = _layoutUrl.replace(
                "{{" + matcher.group(1) + "}}",
                ModsignConfigurations.urlMap[matcher.group(1)] as String,
                true
            )
        }

        return _layoutUrl
    }

    fun setImageUrl(imageUrl: String) {
        _imageUrl = imageUrl
    }

    fun getImageUrl(): String {
        val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
        val matcher = pattern.matcher(_imageUrl)
        while (matcher.find()) {
            _imageUrl = _imageUrl.replace(
                "{{" + matcher.group(1) + "}}",
                ModsignConfigurations.urlMap[matcher.group(1)] as String,
                true
            )
        }

        return _imageUrl
    }

    // Get text based on localization value
    fun getText(): String {
        if (ModsignConfigurations.localizationEnabled &&
            DynamixEnvironmentData.locale.equals(DynamixLocaleStrings.NEPALI, true)
        ) {
            return unicodeText ?: text
        }
        return text
    }
}