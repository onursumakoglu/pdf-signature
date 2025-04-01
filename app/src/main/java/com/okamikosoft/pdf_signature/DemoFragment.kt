package com.okamikosoft.pdf_signature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.okamikosoft.pdf_signature_sdk.PDFSignatureSDK
import com.okamikosoft.pdf_signature_sdk.R
import com.okamikosoft.pdf_signature_sdk.databinding.FragmentDemoBinding

class DemoFragment : Fragment(R.layout.fragment_demo) {

    private lateinit var binding: FragmentDemoBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDemoBinding.bind(view)

        PDFSignatureSDK.initSdk("https://css4.pub/2015/textbook/somatosensory.pdf", this)
    }

    companion object {
        fun newInstance() = DemoFragment().apply {}
    }
}