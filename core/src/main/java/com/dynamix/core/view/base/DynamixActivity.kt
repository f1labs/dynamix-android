package com.dynamix.core.view.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.dynamix.core.R
import com.dynamix.core.cache.DynamixDataStorage
import com.dynamix.core.helper.DynamixCustomProgressDialog
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.locale.DynamixLocaleContextWrapper
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.navigation.NavigationProvider
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

abstract class DynamixActivity<B : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    protected lateinit var mBinding: B
    protected lateinit var fragmentContainer: View
    protected open var fragment: Fragment? = null
    protected val disposables = CompositeDisposable()
    protected val navigationProvider: NavigationProvider by inject() { parametersOf(this) }
    protected val appLoggerProvider: AppLoggerProvider by inject()

    protected lateinit var progressDialog: DynamixCustomProgressDialog

    protected val dynamixDataStorage: DynamixDataStorage by inject()

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    override fun getLifecycle(): Lifecycle {
        return super.getLifecycle()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateLayout()
        setUp()
    }

    protected open fun inflateLayout() {
        mBinding = DataBindingUtil.setContentView(this, layoutId)
    }

    protected fun setUp() {
        makeDialog()
        setupViews()
        setupObservers()
        setupEventListeners()
    }

    protected open fun makeDialog() {
        progressDialog = DynamixCustomProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.dynamix_action_loading))
    }

    protected open fun showDialog() {
        progressDialog.show()
    }

    protected open fun dismissDialog() {
        progressDialog.dismiss()
    }

    abstract fun setupEventListeners()

    abstract fun setupObservers()

    abstract fun setupViews()

    protected fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private val isKeyboardOpened: Boolean
        get() {
            val view = currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                return imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            return false
        }

    protected fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputManager =
                view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputManager.isAcceptingText) {
                inputManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }

    override fun onBackPressed() {
        if (isKeyboardOpened) {
            hideKeyboard()
        }
        super.onBackPressed()
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            return
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun attachBaseContext(newBase: Context) {
        val newLocale = Locale(DynamixEnvironmentData.locale)
        val context = DynamixLocaleContextWrapper.wrap(newBase, newLocale)

        appLoggerProvider.info("Locale Activity::: " + context.resources.configuration.locale.language)
        super.attachBaseContext(newBase)
    }
}