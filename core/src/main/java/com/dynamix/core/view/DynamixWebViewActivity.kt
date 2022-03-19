package com.dynamix.core.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import com.dynamix.core.R
import com.dynamix.core.databinding.DynamixActivityWebviewBinding
import com.dynamix.core.navigation.NavigationConstants
import com.dynamix.core.utils.JavaScriptInterfaceProvider
import com.dynamix.core.utils.DynamixNotificationUtils
import com.dynamix.core.utils.permission.PermissionConstants
import com.dynamix.core.utils.permission.PermissionUtils
import com.dynamix.core.view.base.DynamixActivity
import im.delight.android.webview.AdvancedWebView
import org.koin.android.ext.android.inject
import java.net.URISyntaxException

class DynamixWebViewActivity : DynamixActivity<DynamixActivityWebviewBinding>(),
    AdvancedWebView.Listener {

    private var url: String? = null
    private val javaScriptInterfaceProvider: JavaScriptInterfaceProvider by inject()

    override val layoutId: Int
        get() = R.layout.dynamix_activity_webview

    private lateinit var bundleData: Bundle

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bundleData = intent.getBundleExtra(NavigationConstants.NAV_DATA)!!

        mBinding.dynamixWebview.webViewClient = object : WebViewClient() {
            @SuppressLint("CheckResult")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                appLoggerProvider.debug("URL --- $url")
                if (!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                            finish()
                        }
                        return true
                    } catch (e: URISyntaxException) {
                        error(e)
                    }
                    return false
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
        if (bundleData.containsKey(NavigationConstants.WEBVIEW_URL)) {
            if (bundleData.getString(NavigationConstants.WEBVIEW_URL)!!
                    .contains("meroshare.cdsc.com.np")
            ) {
                mBinding.dynamixWebview.settings.userAgentString =
                    "Mozilla/5.0 (Windows NT 5.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36"
            }
            loadUrl(bundleData.getString(NavigationConstants.WEBVIEW_URL), "")
        }
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    protected fun loadUrl(url: String?, title: String?) {
        mBinding.dynamixWebview.loadUrl(url!!)
        mBinding.dynamixWebview.settings.javaScriptEnabled = true
        mBinding.dynamixWebview.addJavascriptInterface(
            javaScriptInterfaceProvider,
            "javaScriptInterface"
        )
        mBinding.dynamixWebview.setListener(this, this)
        if (bundleData.containsKey(NavigationConstants.PAGE_TITLE)) {
            mBinding.dynamixToolbar.pageTitle.text =
                bundleData.getString(NavigationConstants.PAGE_TITLE)
        } else {
            mBinding.dynamixToolbar.pageTitle.text = title
        }
        mBinding.dynamixToolbar.myToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && mBinding.dynamixWebview.canGoBack()) {
            mBinding.dynamixWebview.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        mBinding.dynamixWebview.onResume()
    }

    override fun onPause() {
        mBinding.dynamixWebview.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mBinding.dynamixWebview.onDestroy()
        super.onDestroy()
    }

    override fun onPageStarted(url: String, favicon: Bitmap?) {
        mBinding.dynamixToolbar.progressBar.isVisible = true
    }

    override fun onPageFinished(url: String?) {
        mBinding.dynamixToolbar.progressBar.isVisible = false
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        mBinding.dynamixToolbar.progressBar.isVisible = false
        mBinding.dynamixWebviewContainer.isVisible = false
        mBinding.tvNoConnection.isVisible = true
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
        this.url = url
        if (!PermissionUtils.hasExternalStorageWritePermission(this@DynamixWebViewActivity)) {
            PermissionUtils.requestExternalStoragePermission(this@DynamixWebViewActivity)
        } else {
            downloadFile()
        }
    }

    private fun downloadFile() {
        if (url!!.startsWith("blob")) {
            mBinding.dynamixWebview.loadUrl(
                javaScriptInterfaceProvider.getBase64StringFromBlobUrl(
                    url!!
                )
            )
        } else {
            DynamixNotificationUtils.showErrorInfo(
                this,
                getString(R.string.dynamix_error_permission_not_granted)
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionConstants.REQ_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadFile()
            } else {
                DynamixNotificationUtils.showErrorInfo(
                    this,
                    getString(R.string.dynamix_error_permission_not_granted)
                )
            }
        }
    }

    override fun onExternalPageRequest(url: String?) {

    }

    override fun setupEventListeners() {

    }

    override fun setupObservers() {

    }

    override fun setupViews() {

    }
}