package com.dynamix.modsign.core.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.dynamix.R
import com.dynamix.core.event.DynamixEvent
import com.dynamix.core.helper.DynamixCustomProgressDialog
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.modsign.ModSignKeyConfigs
import com.dynamix.modsign.core.components.recyclerview.DynamixRvAdapterEvent
import com.dynamix.modsign.core.events.DynamixLayoutEvent
import com.dynamix.modsign.core.components.recyclerview.RvAdapter
import com.dynamix.modsign.core.events.DynamixButtonEvent
import com.dynamix.modsign.core.events.DynamixRvEvent
import com.dynamix.modsign.core.inflater.DynamixLayoutInflater
import com.dynamix.modsign.model.RootView
import com.dynamix.modsign.view.ModSignVm
import com.google.gson.Gson
import org.koin.android.ext.android.inject

open class DynamixFragment : Fragment(), LifecycleOwner, DynamixLayoutEvent, DynamixRvEvent,
    DynamixButtonEvent, DynamixRvAdapterEvent {
    lateinit var linearLayout: LinearLayout
    lateinit var mRootView: RootView
    lateinit var renderedView: View

    private lateinit var progressDialog: DynamixCustomProgressDialog
    private val modSignVm: ModSignVm by inject()
    private val navigation: NavigationProvider by inject()
    private val gson: Gson by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dynamix_fragment, container, false)

        progressDialog = DynamixCustomProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.modsign_label_loading))

        return view
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearLayout = view.findViewById(R.id.container)
        var layoutUrl = requireArguments().getString("layoutUrl")

        setupObservers()

        if (layoutUrl == null) {
            layoutUrl = getLayoutUrl()
        }

        modSignVm.loadLayout(layoutUrl)
    }

    open fun getLayoutUrl(): String {
        return ""
    }

    private fun setupObservers() {
        modSignVm.loading.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }

        modSignVm.layoutLoadingSuccess.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let {
                val dynamixLayoutInflater = DynamixLayoutInflater(this)
                dynamixLayoutInflater.inflateAndPostInflate(requireContext(), it, linearLayout)
            }
        }

        modSignVm.layoutLoadingFailure.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let {
            }
        }
    }


    companion object {
        fun getInstance(layoutUrl: String?): DynamixFragment {
            val bundle = Bundle()
            bundle.putString("layoutUrl", layoutUrl)
            val dynamixFragment = DynamixFragment()
            dynamixFragment.arguments = bundle
            return dynamixFragment
        }
    }

    override fun onViewInflated(viewData: HashMap<String, Any>) {
        println("View data size is " + viewData.size)
    }

    override fun onAdapterSet(adapter: RvAdapter) {
        println("Adapter is set wohoooo")
    }

    override fun getLifecycle(): Lifecycle {
        return super.getLifecycle()
    }

    override fun onButtonClicked(view: RootView) {

    }

    override fun onBindViewHolder(itemView: View, data: Any) {

    }
}