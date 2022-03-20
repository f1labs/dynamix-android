package com.dynamix.modsign.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dynamix.R
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.core.view.base.DynamixActivity
import com.dynamix.databinding.DynamixModsignActivityBinding
import com.dynamix.modsign.ModSignController
import com.dynamix.modsign.core.components.recyclerview.DynamixRvAdapterEvent
import com.dynamix.modsign.core.components.recyclerview.RvAdapter
import com.dynamix.modsign.core.events.DynamixButtonEvent
import com.dynamix.modsign.core.events.DynamixLayoutEvent
import com.dynamix.modsign.core.events.DynamixRvEvent
import com.dynamix.modsign.core.inflater.DynamixLayoutInflater
import com.dynamix.modsign.model.LayoutWrapper
import com.dynamix.modsign.model.RootView
import org.koin.android.ext.android.inject

open class ModSignActivity : DynamixActivity<DynamixModsignActivityBinding>(),
    DynamixLayoutEvent,
    DynamixRvEvent,
    DynamixRvAdapterEvent,
    DynamixButtonEvent {

    protected val modSignVm: ModSignVm by inject()
    protected lateinit var intentEvent: DynamixEvent
    protected lateinit var layoutWrapper: LayoutWrapper

    protected var mDataMap: Map<String, Any> = HashMap()

    override val layoutId: Int
        get() = R.layout.dynamix_modsign_activity

    private var requestCode: Int = -1

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var layoutCode = ""
        var layoutUrl = ""
        if (intent.hasExtra(NavigationConstants.NAV_DATA)) {
            val bundle = intent.getBundleExtra(NavigationConstants.NAV_DATA)
            intentEvent = bundle?.getParcelable(NavigationConstants.NAVIGATION_EVENT)!!
            requestCode = intentEvent.requestCode

            mBinding.toolbar.pageTitle.text = bundle.getString(NavigationConstants.PAGE_TITLE)

            layoutCode = intentEvent.layoutCode!!
            layoutUrl = intentEvent.layoutUrl
        }

        if(intent.hasExtra("dataMap")) {
            mDataMap = intent.getSerializableExtra("dataMap")!! as Map<String, Any>
        }

        //TODO load layout when  layoutCode is achieved.,
        if (layoutCode.isNotEmpty()) {
            modSignVm.loadLayoutWithLayoutCode(layoutCode, intentEvent)
            return
        }
        if (layoutUrl.isNotEmpty()) {
            modSignVm.loadLayout(layoutUrl, intentEvent)
            return
        }
    }

    override fun setupEventListeners() {
        mBinding.toolbar.myToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setupViews() {

    }

    override fun setupObservers() {
        modSignVm.loading.observe(this) {
            if (it) {
                showDialog()
            } else {
                dismissDialog()
            }
        }

        modSignVm.layoutLoadingSuccess.observe(this) {
            it?.getContentIfNotHandled()?.let {
                layoutWrapper = it
                val dynamixLayoutInflater = DynamixLayoutInflater(this)
                dynamixLayoutInflater.inflateAndPostInflate(this, it, mBinding.llFormContainer, mDataMap)
            }
        }

        modSignVm.layoutLoadingFailure.observe(this) {
            it?.getContentIfNotHandled()?.let {
//                DynamixNotificationUtils.errorDialog(this, "Form not generated...")
            }
        }
    }

    override fun onViewInflated(viewData: HashMap<String, Any>) {
//        val vp = linearLayout.findViewWithTag<ViewPager2>("vp")
//        vp.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
//        vp.setOnClickListener{
//            Toast.makeText(this, "I am clicked", Toast.LENGTH_LONG).show()
//        }
    }

    override fun onAdapterSet(adapter: RvAdapter) {
//        println("Adapter item count is " + adapter.itemCount)
    }

    override fun onBindViewHolder(itemView: View, data: Any) {
        println("On Bind view holder")
    }

    override fun onButtonClicked(view: RootView) {

    }

    protected open fun updateEvent() {
        if (requestCode != -1) {
            val intent = Intent()
            intent.putExtra(NavigationConstants.NAVIGATION_EVENT, intentEvent)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}