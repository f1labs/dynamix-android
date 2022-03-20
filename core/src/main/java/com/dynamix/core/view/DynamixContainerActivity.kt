package com.dynamix.core.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dynamix.core.R
import com.dynamix.core.databinding.DynamixActivityContainerBinding
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.core.view.base.DynamixActivity
import com.dynamix.core.view.base.DynamixActivityManager
import com.dynamix.core.view.base.DynamixAppUtils

open class DynamixContainerActivity : DynamixActivity<DynamixActivityContainerBinding>(),
    DynamixActivityManager {

    override var fragment: Fragment? = null

    override val layoutId: Int
        get() = R.layout.dynamix_activity_container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(NavigationConstants.NAV_DATA)) {
            initializeFragment()
        }
    }

    protected open fun initializeFragment() {
        val bundle = intent.getBundleExtra(NavigationConstants.NAV_DATA)!!
        val title = bundle.getString(NavigationConstants.TITLE)
        val fragmentCode = bundle.getString(NavigationConstants.FRAGMENT_CODE)!!
        mBinding.dynamixToolbar.pageTitle.text = title
        fragment = DynamixAppUtils.getFragment(this, supportFragmentManager, fragmentCode)
        fragment!!.arguments = bundle // pass the bundle passed to the parent
        supportFragmentManager.beginTransaction().replace(
            mBinding.dynamixFragmentContainer.id,
            fragment!!
        ).commitAllowingStateLoss()
    }

    override fun setPageTitle(pageTitle: String) {
        mBinding.dynamixToolbar.pageTitle.text = pageTitle
    }

    override fun setupEventListeners() {
    }

    override fun setupObservers() {
    }

    override fun setupViews() {
    }
}