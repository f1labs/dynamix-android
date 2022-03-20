package com.dynamix.core.view.terms_and_condition

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.dynamix.core.R
import com.dynamix.core.databinding.DynamixTermsAndConditionBinding
import com.dynamix.core.helper.DynamixCustomProgressDialog
import com.dynamix.core.navigation.NavigationConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import im.delight.android.webview.AdvancedWebView
import org.koin.android.ext.android.inject

class DynamixTermsAndConditionBottomSheetDialog(
    private val api: String,
    private val statusListener: StatusListener?
) : BottomSheetDialogFragment(),
    AdvancedWebView.Listener {

    private lateinit var binding: DynamixTermsAndConditionBinding
    private lateinit var progressDialog: DynamixCustomProgressDialog

    private val termsAndConditionVm: DynamixTermsAndConditionVm by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Core_BottomSheetDialog)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dynamix_terms_and_condition,
            container,
            false
        )
        val bundle = arguments
        if (bundle?.getString(NavigationConstants.POSITIVE_BUTTON_TEXT) != null && bundle.getString(
                NavigationConstants.POSITIVE_BUTTON_TEXT
            )!!.isNotEmpty()
        ) {
            binding.btnContinue.text = bundle.getString(NavigationConstants.POSITIVE_BUTTON_TEXT)
        }
        binding.ltBsBtmShClose.setOnClickListener { dismiss() }
        binding.webViewContainer.settings.javaScriptEnabled = true
        binding.webViewContainer.setListener(this.activity, this)
        binding.webViewContainer.isVisible = false
        binding.btnContinue.isVisible = false
        binding.btnCancel.isVisible = false
        binding.cvAgreeTerms.isVisible = false
        binding.btnContinue.setOnClickListener { onTermsAndConditionAccepted() }
        binding.btnCancel.setOnClickListener { onTermsAndConditionRejected() }
        binding.cvAgreeTerms.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                binding.btnContinue.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dynamix_primary_btn_bg
                )!!
                binding.btnContinue.isFocusable = true
                binding.btnContinue.isEnabled = true
                binding.btnContinue.isClickable = true
            } else {
                binding.btnContinue.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.dynamix_primary_btn_bg_disabled
                )!!
                binding.btnContinue.isFocusable = false
                binding.btnContinue.isEnabled = false
                binding.btnContinue.isClickable = false
            }
        }
        progressDialog = DynamixCustomProgressDialog(requireContext())
        progressDialog.setMessage(getString(R.string.dynamix_action_loading))
        progressDialog.setCancelable(false)
        return binding.root
    }

    protected open fun onTermsAndConditionAccepted() {
        dismiss()
        statusListener?.accept()
    }

    protected open fun onTermsAndConditionRejected() {
        dismiss()
        statusListener?.reject()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        termsAndConditionVm.loading.observe(viewLifecycleOwner) {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }
        termsAndConditionVm.termsAndConditionData.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { data ->
                binding.webViewContainer.loadHtml(data)
            }
        }
        loadData()
    }

    private fun loadData() {
        binding.webViewContainer.isVisible = true

        if (api.isNotEmpty()) {
            termsAndConditionVm.loadHtmlData(api)
        } else {
            dismiss()
        }
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {}
    override fun onPageFinished(url: String?) {
        binding.btnContinue.isVisible = true
        binding.btnCancel.isVisible = true
        binding.cvAgreeTerms.isVisible = true
    }

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {
        binding.btnContinue.isVisible = true
        binding.btnCancel.isVisible = true
        binding.cvAgreeTerms.isVisible = true
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {}

    interface StatusListener {
        fun accept()
        fun reject()
    }
}