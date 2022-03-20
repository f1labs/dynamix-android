package com.dynamix.core.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.dynamix.core.R
import com.dynamix.core.R.id.design_bottom_sheet
import com.dynamix.core.databinding.DynamixLayoutBaseBottomSheetBinding
import com.dynamix.core.logger.AppLoggerProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

abstract class DynamixBaseBottomSheet(private val title: String) : BottomSheetDialogFragment() {

    private var _layoutBinding: DynamixLayoutBaseBottomSheetBinding? = null
    protected val appLoggerProvider: AppLoggerProvider by inject()

    protected val layoutBinding: DynamixLayoutBaseBottomSheetBinding
        get() = _layoutBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_Core_BottomSheetDialog)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        initializeArguments()
        _layoutBinding = DynamixLayoutBaseBottomSheetBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        val layoutView = layoutView
        require(layoutView.id != -1) { "please set a valid id to the view" }
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
        params.weight = 1f
        layoutBinding.ltBsBtmShRoot.addView(layoutView, params)
        layoutBinding.ltBsBtmShTitle.text = title
        return layoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutBinding.ltBsBtmShClose.setOnClickListener { dismiss() }
        setupViews()
    }

    open fun setupViews() {}
    protected abstract val layoutView: View
    override fun onDestroyView() {
        super.onDestroyView()
        _layoutBinding = null
    }

    protected fun setupEditTextExpansion(editText: EditText) {
        editText.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                expandBottomSheet()
            }
        }
        editText.setOnClickListener { expandBottomSheet() }
    }

    protected fun expandBottomSheet() {
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog!!.findViewById<FrameLayout>(design_bottom_sheet)!!
        val behavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun initializeArguments() {
        try {
            onParseArgument(requireArguments())
        } catch (e: Exception) {
            // TODO(route to some generic error fragments)
            appLoggerProvider.info(e.toString())
        }
    }

    protected open fun onParseArgument(args: Bundle) {}
}