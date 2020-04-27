package com.carrion.edward.smsverificationapp.userconsent

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.carrion.edward.smsverificationapp.R
import com.carrion.edward.smsverificationapp.util.Util
import com.carrion.edward.smsverificationapp.databinding.ActivitySmsBinding
import com.google.android.gms.auth.api.phone.SmsRetriever

/**
 * Example to get OTP from SMS when we don't have control of sender that message.
 *
 * With this approach Android launch a bottom sheet dialog to request permission to read that message
 * only once
 */
class UserConsentActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsBinding

    private lateinit var userConsentBroadcastReceiver: UserConsentBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sms)

        startUserConsent()
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(userConsentBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_USER_CONSENT -> {
                // Obtain the phone number from the result
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val code = message?.let { Util.fetchVerificationCode(it) }

                    binding.etOtp.setText(code)
                    //startUserConsent()//We can start listening again
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    //startUserConsent()//We can start listening again
                }

            }
        }
    }

    private fun startUserConsent() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetriever.getClient(this).also {
            //We can add user phone number or leave it blank
            it.startSmsUserConsent(null)
                .addOnSuccessListener {
                    // Successfully started retriever, expect broadcast intent
                    Log.d(TAG, "LISTENING_SUCCESS")
                }
                .addOnFailureListener {
                    // Failed to start retriever, inspect Exception for more details
                    Log.d(TAG, "LISTENING_FAILURE")
                }
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        userConsentBroadcastReceiver = UserConsentBroadcastReceiver()
            .also {
                it.userConsentBroadcastReceiverListener =
                    object :
                        UserConsentBroadcastReceiver.UserConsentBroadcastReceiverListener {
                        override fun onSuccess(intent: Intent?) {
                            intent?.let { context ->
                                // Start activity to show consent dialog to user, activity must be started in
                                // 5 minutes, otherwise you'll receive another TIMEOUT intent
                                startActivityForResult(
                                    context,
                                    REQ_USER_CONSENT
                                )
                            }
                        }

                        override fun onFailure() {
                        }
                    }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(userConsentBroadcastReceiver, intentFilter)
    }

    companion object {
        const val TAG = "SMS_USER_CONSENT"

        const val REQ_USER_CONSENT = 100// Set to an unused request code
    }
}
