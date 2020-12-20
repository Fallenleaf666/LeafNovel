package com.example.leafnovel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import com.example.leafnovel.`interface`.BatteryChangeHelper
import com.example.leafnovel.`interface`.SystemTimeHelper
import com.example.leafnovel.data.DownloadNovelService.DownloadResultType
import com.example.leafnovel.ui.base.BatteryView
import kotlinx.android.synthetic.main.fragment_my_books.*

class DownloadResultReceiver() : BroadcastReceiver() {
    companion object{
        const val DOWNLOAD_CHAPTER_RESULT_KEY = "DOWNLOAD_CHAPTER_RESULT_KEY"
        const val DOWNLOAD_RESULT = "DOWNLOAD_RESULT"
    }
    override fun onReceive(p0: Context?, intent: Intent?) {
        val action :String? = intent?.action
        Log.d("receiver","動作接收")
        action?.let {
        when(it){
            DOWNLOAD_CHAPTER_RESULT_KEY -> {
                val messenger = intent.getSerializableExtra(DOWNLOAD_RESULT)
                if(messenger == DownloadResultType.SUCCESS){
                Toast.makeText(p0, "下載完成了親", Toast.LENGTH_SHORT).show()
                    Log.d("receiver","下載完成接收")
                }
            }
        }
        }

    }
}