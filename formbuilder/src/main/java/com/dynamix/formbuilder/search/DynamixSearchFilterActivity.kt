package com.dynamix.formbuilder.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dynamix.core.extensions.themeTintedDrawableFromTheme
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.formbuilder.utils.DynamixMyDividerItemDecoration
import com.dynamix.core.view.base.DynamixActivity
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.databinding.DynamixActivitySearchBinding
import java.util.*
import java.util.regex.Pattern

class DynamixSearchFilterActivity : DynamixActivity<DynamixActivitySearchBinding>(),
    DynamixSearchFilterAdapter.FilterAdapterListener {

    private var bundleData = Bundle()
    private var stringList = mutableListOf<String>()
    private var tag: String? = null

    private lateinit var filterAdapter: DynamixSearchFilterAdapter

    override val layoutId: Int
        get() = R.layout.dynamix_activity_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundleData = intent.getBundleExtra(NavigationConstants.NAV_DATA)!!

        stringList = ArrayList()
        filterAdapter = DynamixSearchFilterAdapter(stringList, this)
        val mLayoutManager = LinearLayoutManager(this)
        mBinding.rvSearch.layoutManager = mLayoutManager
        mBinding.rvSearch.itemAnimator = DefaultItemAnimator()
        mBinding.rvSearch.addItemDecoration(
            DynamixMyDividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL,
                36
            )
        )
        mBinding.rvSearch.setIndexBarTransparentValue(0.4.toFloat())
        mBinding.rvSearch.adapter = filterAdapter
        tag = bundleData.getString(DynamixFormFieldConstants.TAG)
        addListData()
    }

    override fun setupEventListeners() {
        mBinding.actSchEditText.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            val query = text ?: ""
            filterAdapter.filter.filter(query)
            if (query.isNotEmpty()) {
                mBinding.rvSearch.setIndexBarVisibility(false)
            } else {
                mBinding.rvSearch.setIndexBarVisibility(true)
            }
        })
        mBinding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun setupObservers() {}

    override fun setupViews() {
        mBinding.toolbar.navigationIcon =
            themeTintedDrawableFromTheme(R.attr.homeAsUpIndicator, R.attr.colorOnSurface)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addListData() {
        stringList.clear()
        val list = mutableListOf<String>()
        if (bundleData.getStringArray(DynamixFormFieldConstants.MY_LIST) != null) {
            val stringArray = bundleData.getStringArray(DynamixFormFieldConstants.MY_LIST)!!

            stringArray.forEach {
                if (!Pattern.compile(Pattern.quote("select"), Pattern.CASE_INSENSITIVE).matcher(it)
                        .find()
                ) {
                    list.add(it)
                }
            }
            list.sort()
        }
        stringList.addAll(list)
        filterAdapter.notifyDataSetChanged()
    }

    override fun onDataSelected(value: String) {
        val intent = Intent()
        intent.putExtra(DynamixFormFieldConstants.VALUE, value)
        intent.putExtra(DynamixFormFieldConstants.TAG, tag)
        setResult(RESULT_OK, intent)
        finish()
    }
}