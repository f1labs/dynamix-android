package com.dynamix.formbuilder.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dynamix.core.R
import com.dynamix.core.utils.DynamixResourceUtils.getColorFromThemeAttributes
import com.dynamix.formbuilder.databinding.DynamixFragmentImagePreviewBinding
import imageview.avatar.com.avatarimageview.GlideApp

class DynamixImagePreviewFragment(private val listener: PreviewListener) : DialogFragment() {

    private var _binding: DynamixFragmentImagePreviewBinding? = null
    private val binding: DynamixFragmentImagePreviewBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DynamixFragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.ThemeOverlay_Dynamix_App_Dialog_FullScreen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fgIgPwClose.setOnClickListener { dismiss() }
        binding.fgIgPwDownload.setOnClickListener { listener.downloadImage() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setImage(drawable: Int) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun setImage() {
                lifecycle.removeObserver(this)
                binding.fgIgPwImage.setImageResource(drawable)
            }
        })
    }

    fun setImage(sampleImageUrl: String?) {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun setImage() {
                lifecycle.removeObserver(this)
                val circularProgressDrawable = CircularProgressDrawable(requireContext())
                circularProgressDrawable.strokeWidth = 10f
                circularProgressDrawable.centerRadius = 40f
                circularProgressDrawable.setColorFilter(
                    getColorFromThemeAttributes(
                        requireContext(),
                        R.attr.colorPrimary
                    ), PorterDuff.Mode.SRC_IN
                )
                circularProgressDrawable.start()
                GlideApp.with(requireContext())
                    .load(sampleImageUrl)
                    .placeholder(circularProgressDrawable)
                    .into(binding.fgIgPwImage)
            }
        })
    }

    fun interface PreviewListener {
        fun downloadImage()
    }
}