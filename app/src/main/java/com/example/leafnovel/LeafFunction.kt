package com.example.leafnovel

import android.content.Context
import android.net.ConnectivityManager

class LeafFunction {
    companion object {
        fun CheckNetConnect(context: Context): Boolean {
            var isConect = false
            val mConnectivityManage = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (android.os.Build.VERSION.SDK_INT >= 29) {
                val network = mConnectivityManage.activeNetwork
                val capabilities = mConnectivityManage.getNetworkCapabilities(network)
//            if(capabilities != null &&(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))){
                if (capabilities != null) {
                    isConect = true
                }
            } else {
//            mConnectivityManage.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
                val networkInfo = mConnectivityManage.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable) {
                    isConect = true
                }
            }
            return isConect
        }
    fun dp2px(context: Context,dpValue:Float):Int{
        val scale = context.resources.displayMetrics.density
        return (dpValue*scale).toInt()
    }
    }
}