package com.example.leafnovel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.util.Log
import android.widget.Toast
import com.example.leafnovel.customToast
import com.example.leafnovel.data.DownloadNovelService.DownloadResultType

open class DownloadResultReceiver() : BroadcastReceiver() {
    companion object {
        const val DOWNLOAD_CHAPTER_RESULT_KEY = "DOWNLOAD_CHAPTER_RESULT_KEY"
        const val DOWNLOAD_RESULT = "DOWNLOAD_RESULT"
    }

    override fun onReceive(p0: Context?, intent: Intent?) {
        val action: String? = intent?.action
        Log.d("receiver", "動作接收")
        action?.let {
            when (it) {
                DOWNLOAD_CHAPTER_RESULT_KEY -> {
                    val messenger = intent.getSerializableExtra(DOWNLOAD_RESULT)
                    if (messenger == DownloadResultType.SUCCESS) {
                        p0?.let { p0 ->
                            customToast(p0, "下載完成").show()
//                        Toast.makeText(p0, "下載完成", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        }

    }
}