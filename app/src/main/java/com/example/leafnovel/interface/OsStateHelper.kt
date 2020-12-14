package com.example.leafnovel.`interface`

interface BatteryChangeHelper {
    fun updateBatteryStat(level:Int,isCharging:Boolean)
}

interface SystemTimeHelper {
    fun updateTime()
}