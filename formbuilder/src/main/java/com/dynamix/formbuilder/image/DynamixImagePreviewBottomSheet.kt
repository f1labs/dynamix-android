package com.dynamix.formbuilder.image

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.dynamix.core.view.DynamixBaseBottomSheet
import com.dynamix.formbuilder.databinding.DynamixLayoutImagePreviewBottomSheetBinding

class DynamixImagePreviewBottomSheet : DynamixBaseBottomSheet {
    private var _binding: DynamixLayoutImagePreviewBottomSheetBinding? = null
    private val binding: DynamixLayoutImagePreviewBottomSheetBinding
        get() = _binding!!

    private var fileUri: Uri? = null
    private var bitmap: Bitmap? = null
    private val listener: ClickListener

    constructor(title: String, fileUri: Uri?, listener: ClickListener) : super(title) {
        this.fileUri = fileUri
        this.listener = listener
    }

    constructor(title: String, bitmap: Bitmap?, listener: ClickListener) : super(title) {
        this.bitmap = bitmap
        this.listener = listener
    }

    override fun setupViews() {
        super.setupViews()
        binding.ltIpBsChooseAnother.setOnClickListener {
            listener.onChangeImage()
            dismiss()
        }
    }

    override val layoutView: View
        get() {
            _binding = DynamixLayoutImagePreviewBottomSheetBinding.inflate(
                LayoutInflater.from(requireContext()),
                null,
                false
            )
            if (bitmap != null) {
                binding.ltIpBsImage.setImageBitmap(bitmap)
            } else if (fileUri != null) {
                binding.ltIpBsImage.setImageURI(fileUri)
            } else {
                appLoggerProvider.warning("no bitmap or fileUri provided")
            }
            return binding.root
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun interface ClickListener {
        fun onChangeImage()
    }
}