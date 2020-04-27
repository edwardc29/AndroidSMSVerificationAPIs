package com.carrion.edward.smsverificationapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.carrion.edward.smsverificationapp.automaticverification.AutomaticVerificationActivity
import com.carrion.edward.smsverificationapp.userconsent.UserConsentActivity
import com.carrion.edward.smsverificationapp.util.AppSignatureHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appSignatureHelper = AppSignatureHelper(this)
        appSignatureHelper.appSignatures
    }

    fun onClick(view: View) {
        if (view.id == R.id.btn_automatic_verification) {
            startActivity(Intent(this, AutomaticVerificationActivity::class.java))
        } else {
            startActivity(Intent(this, UserConsentActivity::class.java))
        }
    }

}