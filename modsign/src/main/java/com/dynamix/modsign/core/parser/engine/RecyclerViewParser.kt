package com.dynamix.modsign.core.parser.engine

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dynamix.core.cache.DynamixDataStorage
import com.dynamix.core.logger.LoggerProviderUtils
import com.dynamix.core.network.ApiProvider
import com.dynamix.modsign.ModSignKeyConfigs
import com.dynamix.modsign.core.components.recyclerview.RvAdapter
import com.dynamix.modsign.core.events.DynamixRvEvent
import com.dynamix.modsign.core.parser.BaseParser
import com.dynamix.modsign.model.LayoutWrapper
import com.dynamix.modsign.model.RootView
import com.dynamix.modsign.view.ModSignDataProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.java.KoinJavaComponent.inject

class RecyclerViewParser(
    context: Context,
    rootView: RootView
) : BaseParser(context, rootView) {

    @SuppressLint("WrongConstant")
    public override fun parse(): BaseParser {
        val recyclerView = RecyclerView(mContext!!)
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        if (mRootView.layoutType == "grid") {
            recyclerView.layoutManager = GridLayoutManager(mContext, mRootView.itemsPerGrid)
        } else if (mRootView.layoutType == "horizontal") {
            val layoutManager = LinearLayoutManager(mContext)
            layoutManager.orientation = HORIZONTAL
            recyclerView.layoutManager = layoutManager
        } else {
            recyclerView.layoutManager = LinearLayoutManager(mContext)
        }
        recyclerView.setHasFixedSize(true)

        setupLayout(recyclerView)
        recyclerView.tag = mRootView.id
        return this
    }


    companion object {
        private val modSignDataProvider: ModSignDataProvider by inject(ModSignDataProvider::class.java)
        private val apiProvider: ApiProvider by inject(ApiProvider::class.java)
        private val gson: Gson by inject(Gson::class.java)
        private val dataStorage: DynamixDataStorage by inject(DynamixDataStorage::class.java)

        fun postInflate(
            context: Context,
            callback: Any,
            recyclerView: RecyclerView,
            view: RootView,
            dataMap: Map<String, Any>
        ) {

            if (view.getDataUrl() == null) {
                setupLayoutFromUrl(view, dataMap, context, callback, recyclerView)
            } else {
                setupLayoutFromWrapper(view, context, callback, recyclerView)
            }
        }

        @SuppressLint("CheckResult")
        private fun setupLayoutFromWrapper(
            view: RootView,
            context: Context,
            callback: Any,
            recyclerView: RecyclerView
        ) {
            Observable.zip<Map<*, *>, LayoutWrapper, Map<String, Any>>(
                getLayoutData(view.getDataUrl()!!),
                modSignDataProvider.loadLayout(view.getLayoutUrl())
                    .onErrorReturn { LayoutWrapper() }
            ) { dataList: Map<*, *>, layoutWrapper: LayoutWrapper? ->
                val map: MutableMap<String, Any> = HashMap()
                map["layout"] = gson.toJson(layoutWrapper)
                map["data"] = dataList
                map
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ o: Map<String, Any> ->
                    setAdapter(
                        context,
                        view,
                        o["data"] as Map<String, Any>,
                        o["layout"] as String,
                        recyclerView,
                        callback
                    )
                }) { obj: Throwable -> obj.printStackTrace() }
        }

        @SuppressLint("CheckResult")
        private fun getLayoutData(dataUrl: String): Observable<Map<*, *>> {
            if (dataUrl.startsWith(ModSignKeyConfigs.ADAPT_STORAGE_DATA)) {
                LoggerProviderUtils.debug("Adapt local data")
                val storageKey = dataUrl.replace(ModSignKeyConfigs.ADAPT_STORAGE_DATA, "")
                return Observable.just(dataStorage.getStoredData(storageKey))
            } else {
                return modSignDataProvider.loadData(dataUrl)
                    .onErrorReturn { JsonObject() }
                    .map {
                        return@map gson.fromJson<Map<String, Any>?>(
                            it.toString(), object : TypeToken<HashMap<String, Any>>() {}.type
                        )
                    }
            }
        }

        @SuppressLint("CheckResult")
        private fun setupLayoutFromUrl(
            view: RootView,
            dataMap: Map<String, Any>,
            context: Context,
            callback: Any,
            recyclerView: RecyclerView
        ) {
            modSignDataProvider.loadLayout(view.getLayoutUrl())
                .onErrorReturn { LayoutWrapper() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ o: LayoutWrapper ->
                    val layout = gson.toJson(o)

                    setAdapter(context, view, dataMap, layout, recyclerView, callback)
                }) { obj: Throwable -> obj.printStackTrace() }
        }

        fun setAdapter(
            context: Context,
            view: RootView,
            data: Map<String, Any>,
            layout: String,
            recyclerView: RecyclerView,
            callback: Any
        ) {
            val rvAdapter = RvAdapter(
                context,
                getData(view.dataObject, data) as ArrayList<Map<String, Any>>,
                layout,
                callback
            )
            if (view.radioId != "") {
                rvAdapter.setSingleItemSelectionId(view.radioId!!)
            }

            if (view.maxItems != 0) {
                rvAdapter.setMaxItems(view.maxItems)
            }

            recyclerView.adapter = rvAdapter
            (callback as DynamixRvEvent).onAdapterSet(rvAdapter)
        }

        fun getData(dataObject: String, data: Map<String, Any>): Any? {
            val dataObjectList = dataObject.split(".").toTypedArray()

            var dataList: Any? = null
            for (d in dataObjectList) {

                if (data[d] is List<*>) {
                    return data[d]
                }

                dataList = if (dataList == null) {
                    data[d]
                } else {
                    (dataList as Map<*, *>)[d]
                }
            }

            return dataList
        }
    }
}