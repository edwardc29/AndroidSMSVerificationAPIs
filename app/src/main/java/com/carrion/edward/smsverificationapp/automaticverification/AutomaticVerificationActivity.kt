package com.carrion.edward.smsverificationapp.automaticverification

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
 * Example to get OTP from SMS when we have control of sender that message.
 * In this case the message need to have a special content
 *
 * <#> body message XXXXXX
 * has-code-here
 */
class AutomaticVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsBinding

    private lateinit var automaticVerificationBroadcastReceiver: AutomaticVerificationBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sms)

        startAutomaticVerification()
    }

    override fun onStart() {
        super.onStart()
        registerToSmsBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(automaticVerificationBroadcastReceiver)
    }

    private fun startAutomaticVerification() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        val task = SmsRetriever.getClient(this).startSmsRetriever()

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            Log.d(TAG, "LISTENING_SUCCESS")
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            Log.d(TAG, "LISTENING_FAILURE")
        }
    }

    private fun registerToSmsBroadcastReceiver() {
        automaticVerificationBroadcastReceiver = AutomaticVerificationBroadcastReceiver()
            .also {
                it.automaticVerificationBroadcastReceiverListener =
                    object :
                        AutomaticVerificationBroadcastReceiver.AutomaticVerificationBroadcastReceiverListener {
                        override fun onSuccess(message: String) {
                            binding.etOtp.setText(Util.fetchVerificationCode(message))
                            //startAutomaticVerification()//We can start listening again
                        }

                        override fun onFailure() {
                            //do something when it failure
                        }
                    }
            }

        //We register our broadcast at runtime
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(automaticVerificationBroadcastReceiver, intentFilter)
    }

    companion object {
        const val TAG = "SMS_AUTOMATIC_VERIFICAT"
    }
}