package com.example.leafnovel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.leafnovel.`interface`.BatteryChangeHelper
import com.example.leafnovel.`interface`.SystemTimeHelper
import com.example.leafnovel.ui.base.BatteryView
import kotlinx.android.synthetic.main.fragment_my_books.*

class BatteryChangeReceiver(_batteryChangeHelper: BatteryChangeHelper) : BroadcastReceiver() {
    private val batteryChangeHelper = _batteryChangeHelper
    override fun onReceive(p0: Context?, intent: Intent?) {
        intent?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL

            batteryChangeHelper.updateBatteryStat(level * 100 / scale, isCharging)
        }
    }
}