package com.okamikosoft.pdf_signature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.okamikosoft.pdf_signature_sdk.R
import com.okamikosoft.pdf_signature_sdk.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {

    private var binding: ActivityDemoBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater);
        setContentView(R.layout.activity_demo)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DemoFragment())
            .commit()
    }
}