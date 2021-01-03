package com.example.leafnovel.data

import android.app.IntentService
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
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
        val CREATOR = "CREATOR"
    }

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.IO
    private val scope = CoroutineScope(coroutineContext)

    private var isDownloadComplete = false
    private val sbBooksDao = StoredBookDB.getInstance(this)?.storedbookDao()
    private val repository: Repository = sbBooksDao?.let { Repository(it) }!!
    override fun onHandleIntent(intent: Intent?) {
        val action: String? = intent?.action
        when (intent?.action) {
            DOWNLOAD_SINGLE_ACTION -> {
                Toast.makeText(applicationContext, "請勿從後台關閉app以保持下載", Toast.LENGTH_SHORT).show()
                val bookDownloadInfo = intent.getParcelableExtra<BookDownloadInfo>("bookDownloadInfo")
                val smallIcon: Int = R.drawable.ic_launcher_background
                val ticker = "開始下載小說章節"
                val downloadNotify =
                    NotifyUtil(applicationContext, resources.getInteger(R.integer.DOWNLOAD_NOTIFICATION_CHANNEL))
                val hasDownloadChapter: ArrayList<ChapterDownloadResult> = arrayListOf()
                isDownloadComplete = false
                downloadNotify.notifyProgress(
                    null, smallIcon, ticker, resources.getString(R.string.app_chines_name), "下載中",
                    sound = false, vibrate = false, lights = false, notificationHelper
                )
                val beforeTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
                Log.d(TAG, "-----------Start at ${beforeTime}-----------")
                bookDownloadInfo?.let { downloadAction(it, hasDownloadChapter) }
                Thread {
                    var canBreak = false
                    while (!isDownloadComplete) {
                        bookDownloadInfo.let {
                            if (hasDownloadChapter.size == bookDownloadInfo.download.size) {
                                isDownloadComplete = true
                                canBreak = true
                            }
                        }
                        if (canBreak) {
                            val afterTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
                            Log.d(TAG, "-----------after at ${afterTime}-----------")
                            val resultIntent = Intent(applicationContext, DownloadResultReceiver::class.java).apply {
                                this.action = DOWNLOAD_CHAPTER_RESULT_KEY
                                putExtra(DOWNLOAD_RESULT, DownloadResultType.SUCCESS)
                            }
                            sendBroadcast(resultIntent)
                            break
                        }
                        try {
                            Thread.sleep(5 * 1000.toLong())
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
                Log.d(TAG, "-----------先執行後面句子-----------")
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
                    notificationHelper
                )
                try {
                    Thread.sleep(5 * 1000.toLong())
//                    downloadAction(bookDownloadInfo, hasDownloadChapter)
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
                    notificationHelper
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
//    private fun downloadAction(bookDownloadInfo: BookDownloadInfo) {
//        val downloadChaptersList = bookDownloadInfo.download
//        val bookName = bookDownloadInfo.bookName
//        val bookId = bookDownloadInfo.bookId
//        scope.launch {
//            for(i in downloadChaptersList){
//                val time = measureTimeMillis {
//                    val chapterContentText = repository.getSearchBookChaptersContextBeta(i.chUrl,i.chtitle, bookName)
//                    repository.saveChapter(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
//                }
//                Log.d(TAG, "$time ms ${i.chtitle} ${Thread.currentThread()}")
//            }
//        }
//    }

    private fun downloadAction(
        bookDownloadInfo: BookDownloadInfo,
        downloadChapterState: ArrayList<ChapterDownloadResult>
    ) {
        val downloadChaptersList = bookDownloadInfo.download
        val bookName = bookDownloadInfo.bookName
        val bookId = bookDownloadInfo.bookId
        val executor = Executors.newFixedThreadPool(5)
        for (i in downloadChaptersList) {
            val task = Runnable {
                val time = measureTimeMillis {
                    Log.d(TAG, "${Thread.currentThread()}")
                    val chapterContentText = repository.getSearchBookChaptersContextBeta(i.chUrl, i.chtitle, bookName)
                    repository.saveChapter(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
                    downloadChapterState.add(
                        ChapterDownloadResult(
                            i.chIndex,
                            i.chtitle,
                            i.chUrl,
                            ChapterDownloadState.SUCCESS
                        )
                    )
                }
                Log.d(
                    TAG,
                    "-----------End at ${SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))}-----------"
                )
            }
            executor.execute(task)
        }
    }

//    private suspend fun getSearchBookChaptersContext(bookName: String, bookChapter: BookChapter) =
//        withContext(Dispatchers.IO) {
//            repository.getSearchBookChaptersContextBeta(bookChapter.chUrl, bookChapter.chtitle, bookName)
//        }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
        Log.d(TAG, "-----------服務結束-----------")
    }


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
}