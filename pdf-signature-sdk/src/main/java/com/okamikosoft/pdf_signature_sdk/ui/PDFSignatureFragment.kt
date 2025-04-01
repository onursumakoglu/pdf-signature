package com.okamikosoft.pdf_signature_sdk.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import com.github.barteksc.pdfviewer.PDFView
import com.okamikosoft.pdf_signature_sdk.R
import com.okamikosoft.pdf_signature_sdk.databinding.FragmentPdfSignatureBinding
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.PDSignature
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface
import com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.X509Certificate
import java.util.Calendar

class PDFSignatureFragment : Fragment(R.layout.fragment_pdf_signature) {
    private lateinit var pdfView: PDFView

    //private lateinit var signatureView: SignatureView
    private lateinit var saveButton: Button
    private lateinit var pdfFile: File

    private var pdfUrl: String? = null
    private var pdfByteArray: ByteArray? = null
    private var binding: FragmentPdfSignatureBinding? = null

    companion object {
        fun newInstance(pdfUrl: String?, pdfByteArray: ByteArray?): PDFSignatureFragment {
            return PDFSignatureFragment().apply {
                this.pdfUrl = pdfUrl
                this.pdfByteArray = pdfByteArray
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPdfSignatureBinding.bind(view)

        // "https://www.orimi.com/pdf-test.pdf"
        downloadAndLoadPdf(pdfUrl.orEmpty())

        /*
        saveButton.setOnClickListener {
            addSignatureToPdf()
        }

         */
    }

    private fun downloadAndLoadPdf(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlConnection = URL(url).openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                val inputStream = urlConnection.inputStream
                val byteArray = inputStream.readBytes()

                withContext(Dispatchers.Main) {
                    loadPdfFromBytes(byteArray)
                }
            } catch (e: Exception) {
                Log.e("SignatureFragment", "PDF indirme hatası", e)
            }
        }
    }

    private fun loadPdfFromBytes(byteArray: ByteArray) {
        pdfFile = File(requireContext().cacheDir, "temp.pdf")
        val outputStream = FileOutputStream(pdfFile)
        outputStream.write(byteArray)
        outputStream.close()
        binding?.progressBar?.isVisible = false
        binding?.pdfView?.fromFile(pdfFile)?.load()
    }

    private fun addSignatureToPdf() {
        val signatureBitmap = binding?.signatureView?.getSignatureBitmap()
        if (signatureBitmap == null) {
            Toast.makeText(requireContext(), "Lütfen önce bir imza çizin!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val outputPdfFile = File(requireContext().cacheDir, "signed.pdf")

            // 1️⃣ PDF'yi yükle
            val pdfDocument = PDDocument.load(pdfFile)

            // 2️⃣ Son sayfayı seç
            val page = pdfDocument.getPage(pdfDocument.numberOfPages - 1)

            // 3️⃣ İmzayı bitmap'ten dönüştür
            val stream = ByteArrayOutputStream()
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val imageBytes = stream.toByteArray()

            val pdImage = PDImageXObject.createFromByteArray(pdfDocument, imageBytes, "signature")

            // 4️⃣ İmzanın pozisyonunu belirle
            val contentStream = PDPageContentStream(pdfDocument, page, PDPageContentStream.AppendMode.APPEND, true)

            val x = 100f
            val y = 100f
            val width = 250f
            val height = 100f

            contentStream.drawImage(pdImage, x, y, width, height)
            contentStream.close()

            addDigitalSignatureToPdf(pdfDocument)

            // 5️⃣ PDF'yi kaydet ve kapat
            pdfDocument.save(outputPdfFile)
            pdfDocument.close()

            // Güncellenmiş PDF'yi açalım
            pdfView.fromFile(outputPdfFile).load()

            Toast.makeText(requireContext(), "İmza eklendi!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "İmza eklenirken hata oluştu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDigitalSignatureToPdf(pdfDocument: PDDocument) {
        try {
            val outputPdfFile = File(requireContext().cacheDir, "digitally_signed.pdf")

            // 1️⃣ PDF'yi yükle

            // 2️⃣ İmza alanı oluştur
            val signatureField = PDSignature()
            signatureField.signDate = Calendar.getInstance()
            signatureField.setFilter(PDSignature.FILTER_ADOBE_PPKLITE) // Adobe standardı
            signatureField.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED)
            signatureField.name = "Dijital İmza"

            pdfDocument.addSignature(signatureField)

            // 3️⃣ Sertifika dosyasını oku (PKCS#12)
            val keystoreFile = File(requireContext().filesDir, "test_certificate.pfx")
            val keystoreStream = FileInputStream(keystoreFile)
            val keystore = KeyStore.getInstance("PKCS12")
            keystore.load(keystoreStream, "123456".toCharArray()) // PFX şifresi

            val alias = keystore.aliases().nextElement()
            val privateKey = keystore.getKey(alias, "123456".toCharArray()) as PrivateKey
            val certificate = keystore.getCertificate(alias) as X509Certificate

            // 4️⃣ SHA-256 ile PDF'yi imzala
            val signatureOptions = SignatureOptions()
            signatureOptions.preferredSignatureSize = 8192 // İmza alanı büyüklüğü

            // 5️⃣ SignatureInterface implementasyonu
            val signingService = SignatureInterface { content: InputStream ->
                val signatureInstance = Signature.getInstance("SHA256withRSA")
                signatureInstance.initSign(privateKey)

                // İçeriği okuyup imzalama işlemi
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while (content.read(buffer).also { bytesRead = it } != -1) {
                    signatureInstance.update(buffer, 0, bytesRead)
                }

                // İmzalanmış veriyi döndürüyoruz
                signatureInstance.sign()
            }

            // 6️⃣ PDF imzalama işlemi
            pdfDocument.addSignature(signatureField, signingService, signatureOptions)

            // 7️⃣ Çıktıyı kaydet
            FileOutputStream(outputPdfFile).use { output ->
                pdfDocument.save(output)
            }

            pdfDocument.close()

            Toast.makeText(requireContext(), "PDF dijital olarak imzalandı!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Dijital imzalama başarısız!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveSignatureToPdf(): String {
        val signedPdfPath = requireContext().filesDir.absolutePath + "/signed.pdf"
        //addSignatureToPdf(pdfPath!!, signatureView.getSignatureBitmap(), signedPdfPath)
        return signedPdfPath
    }

}
