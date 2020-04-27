package com.carrion.edward.smsverificationapp.userconsent

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class UserConsentBroadcastReceiver : BroadcastReceiver() {
    lateinit var userConsentBroadcastReceiverListener: UserConsentBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get consent intent
                    extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT).also {
                        userConsentBroadcastReceiverListener.onSuccess(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    // Time out occurred, handle the error.
                    userConsentBroadcastReceiverListener.onFailure()
                }
            }
        }
    }

    interface UserConsentBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}