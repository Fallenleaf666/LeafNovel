package com.example.leafnovel.data

import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookDownloadInfo
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.receiver.DownloadResultReceiver

class DownloadNovelService():IntentService("DownloadNovelService") {
    companion object{
        private const val TAG = "DownloadNovelService"
        const val DOWNLOAD_SINGLE_ACTION = "DOWNLOAD_Single_ACTION"
        const val DOWNLOAD_PLURAL_ACTION = "DOWNLOAD_PLURAL_ACTION"
        const val DOWNLOAD_CHAPTER_MESSENGER_KEY = "DOWNLOAD_CHAPTER_MESSENGER_KEY"
        const val DOWNLOAD_CHAPTER_RESULT_KEY = "DOWNLOAD_CHAPTER_RESULT_KEY"
        const val DOWNLOAD_RESULT = "DOWNLOAD_RESULT"
        val CREATOR = "CREATOR"
    }
    private val sbBooksDao = StoredBookDB.getInstance(this)?.storedbookDao()
    private val repository : Repository =sbBooksDao?.let { Repository(it) }!!
    override fun onHandleIntent(intent: Intent?) {
        val action :String? = intent?.action
//        val creator :String? = intent?.extras?.getString(CREATOR)
        when(intent?.action){
            DOWNLOAD_SINGLE_ACTION->{
                val bookDownloadInfo = intent.getParcelableExtra<BookDownloadInfo>("bookDownloadInfo")
                repository.downloadBookChapter(bookDownloadInfo)
//                val messenger :Messenger = intent.extras?.get(DOWNLOAD_CHAPTER_MESSENGER_KEY) as Messenger
//                val msg:Message = Message()
//                val bundle:Bundle = Bundle()
//                bundle.putString(DOWNLOAD_CHAPTER_RESULT_KEY,"下載完成")
//                msg.data = bundle
//                messenger.send(msg)
                val resultIntent = Intent(this,DownloadResultReceiver::class.java).apply {
                    this.action = DOWNLOAD_CHAPTER_RESULT_KEY
                    putExtra(DOWNLOAD_RESULT,DownloadResultType.SUCCESS)
                }
                sendBroadcast(resultIntent)
            }
            DOWNLOAD_PLURAL_ACTION->{}
        }
    }
    enum class DownloadResultType{
        SUCCESS,FAIL,STOP
    }
}