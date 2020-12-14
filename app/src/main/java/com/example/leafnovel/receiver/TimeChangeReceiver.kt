package com.example.leafnovel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.leafnovel.`interface`.SystemTimeHelper

class TimeChangeReceiver(_systemTimeHelper:SystemTimeHelper) : BroadcastReceiver() {
    private val systemTimeHelper = _systemTimeHelper
    override fun onReceive(p0: Context?, intent: Intent?) {
        intent?.let {
            if(it.action.equals(Intent.ACTION_TIME_TICK)){
                systemTimeHelper.updateTime()
            }
        }
    }
}