package com.example.leafnovel.data

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import com.example.leafnovel.R
import com.example.leafnovel.`interface`.NotificationHelper
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.*
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.notification.NotifyUtil
import com.example.leafnovel.receiver.DownloadResultReceiver
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis


class DownloadNovelService : IntentService("DownloadNovelService") {
    companion object {
        private const val TAG = "DownloadNovelService"
        const val DOWNLOAD_SINGLE_ACTION = "DOWNLOAD_Single_ACTION"
        const val DOWNLOAD_PLURAL_ACTION = "DOWNLOAD_PLURAL_ACTION"
        const val DOWNLOAD_CHAPTER_MESSENGER_KEY = "DOWNLOAD_CHAPTER_MESSENGER_KEY"
        const val DOWNLOAD_CHAPTER_RESULT_KEY = "DOWNLOAD_CHAPTER_RESULT_KEY"
        const val DOWNLOAD_OneThread_ACTION = "DOWNLOAD_OneThread_ACTION"
        const val DOWNLOAD_RESULT = "DOWNLOAD_RESULT"
        const val NOW_PROGRESS = "NOW_PROGRESS"
        const val MAX_PROGRESS = "MAX_PROGRESS"
        const val CHAPTER_TITLE = "CHAPTER_TITLE"
        val CREATOR = "CREATOR"
    }

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)


    private var isDownloadComplete = false
    lateinit var downloadNotify: NotifyUtil
    private val sbBooksDao = StoredBookDB.getInstance(this)?.storedbookDao()
    private val repository: Repository = sbBooksDao?.let { Repository(it) }!!
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                //更新進度
                1 -> {
                    with(msg.data) {
                        val nowProgress = getInt(NOW_PROGRESS, 0)
                        val maxProgress = getInt(MAX_PROGRESS, 0)
                        val chapterTitle = getString(CHAPTER_TITLE, "")
                        updateProgress(nowProgress, maxProgress, chapterTitle)
                    }
                }
                //下載完成
                2 -> {
                    downloadNotify.downloadCompleteNotify()
                }
            }
            super.handleMessage(msg)
        }
    }

    private fun updateProgress(nowProgress: Int, maxProgress: Int, chapterTitle: String) {
        downloadNotify.updateNotifyProgress(nowProgress, maxProgress, chapterTitle)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            DOWNLOAD_SINGLE_ACTION -> {
                scope.launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "請勿從後台關閉app以保持下載", Toast.LENGTH_SHORT).show()
                }
                val bookDownloadInfo = intent.getParcelableExtra<BookDownloadInfo>("bookDownloadInfo")
                val smallIcon: Int = R.drawable.ic_launcher_background
                val ticker = "開始下載小說章節"
                downloadNotify =
                    NotifyUtil(applicationContext, resources.getInteger(R.integer.DOWNLOAD_NOTIFICATION_CHANNEL))
                isDownloadComplete = false
                val time = measureTimeMillis {
                    bookDownloadInfo?.let {
                        downloadNotify.notifyProgress(
                            null, smallIcon, ticker, resources.getString(R.string.app_chines_name), "下載中",
                            sound = false, vibrate = false, lights = false, bookDownloadInfo.download.size
                        )
                        downloadAction(it)
                    }
                }
                Log.d(TAG, "總共花費 ${time / 1000} s")
                Log.d(TAG, "-----------任務結束-----------")
            }
            DOWNLOAD_PLURAL_ACTION -> {
                val bookDownloadInfo = intent.getParcelableExtra<BookDownloadInfo>("bookDownloadInfo")

                val smallIcon: Int = R.drawable.ic_launcher_background
                val ticker = "開始下載小說章節"
                val downloadNotify = NotifyUtil(this, resources.getInteger(R.integer.DOWNLOAD_NOTIFICATION_CHANNEL))
                isDownloadComplete = false
                downloadNotify.notifyProgress(
                    null,
                    smallIcon,
                    ticker,
                    resources.getString(R.string.app_chines_name),
                    "下載中",
                    sound = false,
                    vibrate = false,
                    lights = false,
                    bookDownloadInfo?.download?.size ?: 0
                )
                try {
                    Thread.sleep(5 * 1000.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                isDownloadComplete = true
                val resultIntent = Intent(this, DownloadResultReceiver::class.java).apply {
                    this.action = DOWNLOAD_CHAPTER_RESULT_KEY
                    putExtra(DOWNLOAD_RESULT, DownloadResultType.SUCCESS)
                }
                sendBroadcast(resultIntent)
            }

            DOWNLOAD_OneThread_ACTION -> {
                val bookDownloadInfo = intent.getParcelableExtra<BookDownloadInfo>("bookDownloadInfo")

                val smallIcon: Int = R.drawable.ic_launcher_background
                val ticker = "開始下載小說章節"
                val downloadNotify = NotifyUtil(this, resources.getInteger(R.integer.DOWNLOAD_NOTIFICATION_CHANNEL))
                isDownloadComplete = false
                downloadNotify.notifyProgress(
                    null,
                    smallIcon,
                    ticker,
                    resources.getString(R.string.app_chines_name),
                    "下載中",
                    sound = false,
                    vibrate = false,
                    lights = false,
                    bookDownloadInfo?.download?.size ?: 0
                )
//                repository.downloadBookChapter(bookDownloadInfo)
//                try {
//                    Thread.sleep(5 * 1000.toLong())
//                    downloadAction(bookDownloadInfo)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
                isDownloadComplete = true
                val resultIntent = Intent(this, DownloadResultReceiver::class.java).apply {
                    this.action = DOWNLOAD_CHAPTER_RESULT_KEY
                    putExtra(DOWNLOAD_RESULT, DownloadResultType.SUCCESS)
                }
                sendBroadcast(resultIntent)
            }
        }
    }

    enum class DownloadResultType {
        SUCCESS, FAIL, STOP, DEFAULT
    }

    private val notificationHelper = object : NotificationHelper {
        override fun onCall(): Boolean {
            return isDownloadComplete
        }
    }

    private fun downloadAction(bookDownloadInfo: BookDownloadInfo) {
        val downloadChaptersList = bookDownloadInfo.download
        val bookName = bookDownloadInfo.bookName
        val bookId = bookDownloadInfo.bookId
        val executor = Executors.newFixedThreadPool(5)
        val completionService = ExecutorCompletionService<ChapterDownloadResult>(executor)
        val allDownloadResults: MutableList<ChapterDownloadResult> = mutableListOf()
        for (i in downloadChaptersList) {
            completionService.submit {
                val time = measureTimeMillis {
                    val chapterContentText = repository.getSearchBookChaptersContextBeta(i.chUrl, i.chtitle, bookName)
                    repository.saveChapter(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
                }
                Log.d(TAG, "${i.chtitle} 耗時${time}s")

                ChapterDownloadResult(i.chIndex, i.chtitle, i.chUrl, ChapterDownloadState.SUCCESS)
            }
        }
        executor.shutdown()
        for (i in downloadChaptersList.indices) {
            try {
                val result = completionService.take().get()
//                downloadNotify.updateNotifyProgress(i+1,downloadChaptersList.size,result.chtitle)
                val msg: Message = Message()
                val bundle: Bundle = Bundle()
                msg.what = 1
                bundle.putInt(NOW_PROGRESS, i + 1)
                bundle.putInt(MAX_PROGRESS, downloadChaptersList.size)
                bundle.putString(CHAPTER_TITLE, result.chtitle)
                msg.data = bundle
                handler.sendMessage(msg)
//                Log.d(TAG, "進度 : ${i+1}/${downloadChaptersList.size} ${result.chtitle}")
//                Log.d(TAG, "completionService : ${i} ${result.chIndex}${result.chtitle}")

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e2: ExecutionException) {
                e2.printStackTrace()
            }
        }
        val msg = Message()
        msg.what = 2
        handler.sendMessage(msg)
//        downloadNotify.downloadCompleteNotify()
//        while (!executor.awaitTermination(5, TimeUnit.NANOSECONDS)){
//
//        }
    }


    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
        Log.d(TAG, "-----------服務結束-----------")
    }
//    inner class ChapterDownloadResult(var index:Int,var title:String,var downloadUrl:String,var result:DownloadResultType)


    //                val intent = Intent(this, OtherActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                val rightPendIntent = PendingIntent.getActivity(
//                    mContext,
//                    requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
//                )

//                val messenger :Messenger = intent.extras?.get(DOWNLOAD_CHAPTER_MESSENGER_KEY) as Messenger
//                val msg:Message = Message()
//                val bundle:Bundle = Bundle()
//                bundle.putString(DOWNLOAD_CHAPTER_RESULT_KEY,"下載完成")
//                msg.data = bundle
//                messenger.send(msg)
//
//    val beforeTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
//    Log.d(TAG, "-----------開始下載時間 ${beforeTime}-----------")
}