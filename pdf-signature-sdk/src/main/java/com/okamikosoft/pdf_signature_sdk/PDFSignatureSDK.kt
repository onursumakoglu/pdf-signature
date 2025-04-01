package com.okamikosoft.pdf_signature_sdk

import androidx.fragment.app.Fragment
import com.okamikosoft.pdf_signature_sdk.ui.PDFSignatureFragment

class PDFSignatureSDK {
    private var pdfUrl: String? = null
    private var pdfByteArray: ByteArray? = null
    private var fragment: Fragment? = null

    companion object {
        fun initSdk(pdfUrl: String, fragment: Fragment) {
            val sdkInstance = PDFSignatureSDK()
            sdkInstance.pdfUrl = pdfUrl
            sdkInstance.fragment = fragment
            sdkInstance.start()
        }
    }

    private fun start() {
        val pdfFragment = PDFSignatureFragment.newInstance(pdfUrl, pdfByteArray)

        val containerId = fragment?.view?.id
            ?: throw IllegalStateException("Fragment must have a valid container ID")

        fragment?.childFragmentManager?.beginTransaction()
            ?.replace(containerId, pdfFragment)
            ?.commit()
    }

}

interface SignatureListener {
    fun onSignatureCompleted(signedPdfPath: String)
}