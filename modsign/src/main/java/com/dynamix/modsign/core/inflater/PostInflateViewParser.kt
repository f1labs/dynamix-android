package com.dynamix.modsign.core.inflater

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.core.navigation.NavigationType
import com.dynamix.core.navigation.Navigator
import com.dynamix.R
import com.dynamix.core.cache.AppEnvironment
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.event.DynamixEventAction
import com.dynamix.core.extensions.tintWithPrimary
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.network.ApiProvider
import com.dynamix.modsign.ModSignKeyConfigs
import com.dynamix.modsign.ModsignConfigurations
import com.dynamix.modsign.comparator.ModSignComparator
import com.dynamix.modsign.comparator.ModSignComparatorFactory
import com.dynamix.modsign.core.RootViewTypes
import com.dynamix.modsign.core.parser.engine.ButtonParser
import com.dynamix.modsign.core.parser.engine.RecyclerViewParser
import com.dynamix.modsign.core.parser.engine.ViewPagerParser
import com.dynamix.modsign.model.ModSignVisibility
import com.dynamix.modsign.model.RootView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import java.util.regex.Pattern
import kotlin.collections.HashMap

class PostInflateViewParser(
    val context: Context,
    private val renderedView: View,
    val callback: Any
) : KoinComponent {

    private val navigation: NavigationProvider by inject { parametersOf(context) }
    private val apiProvider: ApiProvider by inject()
    private val appLoggerProvider: AppLoggerProvider by inject()
    private val gson: Gson by inject()

//    private var lastSelectedCheckbox : CheckBox? = null

    @SuppressLint("CheckResult")
    fun viewInflated(
        inflatedView: View,
        rootView: RootView,
        data: Map<String, Any>
    ): HashMap<String, Any> {

        val viewData: HashMap<String, Any> = HashMap()

        for (view in rootView.children!!) {

            setupEventListeners(view, data)

            if (view.children != null && view.children.isNotEmpty()) {
                viewInflated(inflatedView, view, data)
            }
            parseViews(view, rootView, inflatedView, data, viewData)
        }

        return viewData
    }

    private fun parseViews(
        view: RootView,
        rootView: RootView,
        inflatedView: View,
        data: Map<String, Any>,
        viewData: HashMap<String, Any>
    ) {

        if (view.isRelative) {
            parseRelativeLayout(view)
        }

        if(view.visibility != null) {
            inflatedView.findViewWithTag<View>(view.id).isVisible = getVisibility(view.visibility, viewData)
        }

        when (view.type) {
            RootViewTypes.RECYCLER_VIEW -> {
                val recyclerView: RecyclerView = inflatedView.findViewWithTag(view.id)
                RecyclerViewParser.postInflate(context, callback, recyclerView, view, data)
            }

            RootViewTypes.VIEWPAGER -> {
                val viewPager2: ViewPager2 = inflatedView.findViewWithTag(view.id)
                ViewPagerParser.postInflate(context, viewPager2, rootView, viewData)
            }

            RootViewTypes.TEXTVIEW -> {
                if (data.isNotEmpty()) {
                    val textView = inflatedView.findViewWithTag<TextView>(view.id)
                    var text = textView.getTag(R.id.imageUrl) as String

                    val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
                    val matcher = pattern.matcher(text)
                    while (matcher.find()) {
                        text = text.replace(
                            "{{" + matcher.group(1) + "}}",
                            data[matcher.group(1)] as String,
                            true
                        )
                    }

                    if(view.stripHtmlTags != null && view.stripHtmlTags) {
                        text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                    }

                    textView.text = text
                }
            }

            RootViewTypes.IMAGE -> {
                val imageView = inflatedView.findViewWithTag<ImageView>(view.id)
                val imageUrlTag = imageView.getTag(R.id.imageUrl) as String
                if(data.containsKey("tint") && data["tint"] == true && view.tintMode != null) {
                    imageView.tintWithPrimary()
                }
                if (!imageUrlTag.startsWith("http")) {

                    var imageUrl: String = data[imageUrlTag] as String

                    val pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}")
                    val matcher = pattern.matcher(imageUrl)
                    while (matcher.find()) {
                        if(ModsignConfigurations.urlMap.containsKey(matcher.group(1))) {
                            imageUrl = imageUrl.replace(
                                "{{" + matcher.group(1) + "}}",
                                ModsignConfigurations.urlMap[matcher.group(1)] as String,
                                true
                            )
                        }
                    }


                    Glide.with(imageView.context)
                        .load(imageUrl)
                        .into(object : CustomTarget<Drawable?>() {
                            override fun onLoadCleared(placeholder: Drawable?) {
                                //clear image if needed
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                transition: Transition<in Drawable?>?
                            ) {
                                imageView.setImageDrawable(resource)
                            }
                        })
                }
            }
            RootViewTypes.BUTTON -> {
                val button: MaterialButton = inflatedView.findViewWithTag(view.id)
                if(view.onClick == null) {
                    ButtonParser.postInflate(callback, button, view)
                }
            }
        }
    }

    fun setupEventListeners(view: RootView, data: Map<String, Any>) {

        if (view.onClick == null) {
            return
        }

        val v = renderedView
            .findViewWithTag<View>(view.id)

        v.setOnClickListener {
            if (view.onClick.action == "toast") {
                view.onClick.message?.let {
                    Toast.makeText(context, view.onClick.message, Toast.LENGTH_SHORT).show()
                }

                view.onClick.data?.let {
                    Toast.makeText(context, data[it] as String, Toast.LENGTH_SHORT).show()
                }
            } else if (view.onClick.action == "apiCall") {
                view.onClick.api?.let {
                    apiProvider.postUrl(it, data, JsonObject::class.java)
                        .onErrorReturn { JsonObject() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            val responseData: Map<String, Any> = gson.fromJson(
                                it.toString(), object : TypeToken<HashMap<String, Any>>() {}.type
                            )
                            view.onClick.event?.let { it2 ->
                                val navigator = Navigator(
                                    "",
                                    it2.routeTitle,
                                    NavigationType.WEB_VIEW,
                                    getData(it2.getRouteUrl()!!, responseData) as String
                                )
                                navigation.navigate(navigator)
                            }
                        }, {
                            appLoggerProvider.error(it)
                        })
                }
            } else if (view.onClick.action == DynamixEventAction.ROUTE) {
                var event = gson.fromJson(
                    gson.toJson(data),
                    DynamixEvent::class.java
                )
                event = event.copy(
                    routeCode = event.code ?: event.routeCode,
                    routeTitle = event.name ?: event.routeTitle,
                    menuType = event.menuType
                )
                if (data.containsKey(ModSignKeyConfigs.GATE_TYPE)) {


                    navigation.navigate(event, data)
                    return@setOnClickListener
                }

                var event2 = view.onClick


                event2.menuType = event.menuType
                if(event.navLink.isNotEmpty()) {
                    event2.setRouteUrl(event.navLink)
                }
                if(event.name != null && event.name!!.isNotEmpty()) {
                    event2 = view.onClick.copy(
                        routeTitle = event.name!!
                    )
                }

                navigation.navigate(event2, data)
            } else if (view.onClick.action == DynamixEventAction.WEB_VIEW) {
                val navigator = Navigator(
                    code = "",
                    name = view.onClick.routeTitle,
                    type = NavigationType.WEB_VIEW,
                    navLink = view.onClick.getRouteUrl()!!
                )
                navigation.navigate(navigator, data)
            }
        }
    }

    fun parseRelativeLayout(view: RootView) {
        val layoutParams = renderedView
            .findViewWithTag<View>(view.id).layoutParams
        if (layoutParams is RelativeLayout.LayoutParams) {
            if (view.topOf != null) {
                layoutParams.addRule(
                    RelativeLayout.ABOVE,
                    renderedView.findViewWithTag<View>(view.topOf).id
                )
            }
            if (view.leftOf != null) {
                layoutParams.addRule(
                    RelativeLayout.LEFT_OF,
                    renderedView.findViewWithTag<View>(view.leftOf).id
                )
            }
            if (view.rightOf != null) {
                layoutParams.addRule(
                    RelativeLayout.RIGHT_OF,
                    renderedView.findViewWithTag<View>(view.rightOf).id
                )
            }
            if (view.bottomOf != null) {
                layoutParams.addRule(
                    RelativeLayout.BELOW,
                    renderedView.findViewWithTag<View>(view.bottomOf).id
                )
            }
        }
    }

    fun getData(dataObject: String, dataMap: Map<String, Any>): Any? {
        val dataObjectList = dataObject.split(".").toTypedArray()
        var dataList: Any? = null
        for (data in dataObjectList) {
            dataList = if (dataList == null) {
                dataMap[data]
            } else {
                (dataList as Map<*, *>)[data]
            }
        }

        return dataList
    }

    fun getVisibility(visibility: ModSignVisibility, data: HashMap<String, Any>): Boolean {
        val operator = visibility.operator
        var result = true
        if(operator == "OR") {
            result = false
        }

        visibility.conditions?.let {

            for (v in it) {
                result = if(v.operator != null) {
                    getVisibility(v, data)
                } else {
                    if(operator.equals("AND")) {
                        result && getVisibilityCondition(v, data)
                    } else {
                        result || getVisibilityCondition(v, data)
                    }
                }
            }
            return result
        }

        return false
    }

    fun getVisibilityCondition(visibility: ModSignVisibility, data: HashMap<String, Any>): Boolean {

        var key = visibility.key

        if(visibility.key!!.startsWith("env")) {
            key = AppEnvironment.instance.get(visibility.key.replace("env:", "")) as String
        } else if(visibility.key.startsWith("data")) {
            key = data[visibility.key]!! as String
        }

        if(visibility.value!!.startsWith("env")) {
            key = AppEnvironment.instance.get(visibility.value.replace("env:", "")) as String
        } else if(visibility.value.startsWith("data")) {
            key = data[visibility.value]!! as String
        }

        val comparator: ModSignComparator = when(visibility.compareType) {
            "boolean" -> {
                ModSignComparatorFactory<Boolean>().getComparator(Boolean::class.java)
            }
            "int" -> {
                ModSignComparatorFactory<Int>().getComparator(Int::class.java)
            }
            "double" -> {
                ModSignComparatorFactory<Double>().getComparator(Double::class.java)
            }
            else -> {
                ModSignComparatorFactory<String>().getComparator(String::class.java)
            }
        }

        when(visibility.condition) {
            "=" -> {
                return comparator.equalsIgnoreCase(key!!, visibility.value!!)
            }

            "==" -> {
                return comparator.equals(key!!, visibility.value!!)
            }

            "!=" -> {
                return comparator.notEqualsIgnoreCase(key!!, visibility.value!!)
            }

            "!==" -> {
                return comparator.notEquals(key!!, visibility.value!!)
            }

            ">" -> {
                return comparator.greaterThan(key!!, visibility.value!!)
            }

            "<" -> {
                return comparator.lessThan(key!!, visibility.value!!)
            }

            else -> {
                return false
            }
        }

    }
}