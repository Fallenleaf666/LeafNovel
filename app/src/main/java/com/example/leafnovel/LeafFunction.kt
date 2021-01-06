package com.example.leafnovel

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

//top level function
fun checkNetConnect(context: Context): Boolean {
    var isConnect = false
    val mConnectivityManage = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (android.os.Build.VERSION.SDK_INT >= 29) {
        val network = mConnectivityManage.activeNetwork
        val capabilities = mConnectivityManage.getNetworkCapabilities(network)
//            if(capabilities != null &&(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))){
        if (capabilities != null) {
            isConnect = true
        }
    } else {
//            mConnectivityManage.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
        val networkInfo = mConnectivityManage.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable) {
            isConnect = true
        }
    }
    return isConnect
}
fun dp2px(context: Context,dpValue:Float):Int{
    val scale = context.resources.displayMetrics.density
    return (dpValue*scale).toInt()
}

class HardWareInformationUtil
private constructor(context: Context){
//  手機出廠品牌
    val brand:String get() = Build.BRAND
//  手機目前sdk版本
    val sdkVersion:Int get() = Build.VERSION.SDK_INT
//  手機版本名稱
    val phoneVersionName:String get() = Build.PRODUCT
//  手機型號
    val model:String get() = Build.MODEL
//  系統開發版本號
    val codeName:String get() = Build.ID
    companion object{
        private var hardWareInformationUtil:HardWareInformationUtil? = null
        fun getHardWareInformationUtil(context: Context):HardWareInformationUtil{
            if(hardWareInformationUtil == null){
                synchronized(HardWareInformationUtil::class.java){
                    hardWareInformationUtil = HardWareInformationUtil(context)
                }
            }
                return hardWareInformationUtil!!
        }
    }
}
