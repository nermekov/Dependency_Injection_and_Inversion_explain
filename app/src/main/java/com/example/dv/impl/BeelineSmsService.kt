package com.example.dv.impl

import android.content.Context
import com.example.dv.SmsService

class BeelineSmsService(
    private val context: Context,
    private val onSmsReceived: (String) -> Unit
) : SmsService