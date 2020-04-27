package com.carrion.edward.smsverificationapp.automaticverification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class AutomaticVerificationBroadcastReceiver : BroadcastReceiver() {
    lateinit var automaticVerificationBroadcastReceiverListener: AutomaticVerificationBroadcastReceiverListener

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                // Get SMS message contents
                CommonStatusCodes.SUCCESS -> if (extras.containsKey(SmsRetriever.EXTRA_SMS_MESSAGE)) {
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    (extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String).also {
                        automaticVerificationBroadcastReceiverListener.onSuccess(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    // Waiting for SMS timed out (5 minutes)
                    automaticVerificationBroadcastReceiverListener.onFailure()
                }
            }
        }
    }

    interface AutomaticVerificationBroadcastReceiverListener {
        fun onSuccess(message: String)
        fun onFailure()
    }
}