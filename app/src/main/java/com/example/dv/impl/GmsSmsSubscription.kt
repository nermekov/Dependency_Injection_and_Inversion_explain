package com.example.dv.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.dv.SmsSubscription
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class GmsSmsSubscription(
    private val context: Context,
    private val onSmsReceived: (String) -> Unit
) : SmsSubscription {
    private val broadcastReceiver: BroadcastReceiver = createBReceiver()

    init {
        SmsRetriever.getClient(context).startSmsRetriever()
        context.registerReceiver(broadcastReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
    }

    private fun createBReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                val extras = intent.extras
                val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
                when (status?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        onSmsReceived(message)
                    }
                    CommonStatusCodes.TIMEOUT -> {

                    }
                }
            }
        }
    }

    override fun unsubscribe() {
        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (ignored: Exception) {
        }
    }
}