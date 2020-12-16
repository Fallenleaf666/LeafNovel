package com.example.leafnovel.ui.main.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.`interface`.BatteryChangeHelper
import com.example.leafnovel.`interface`.SystemTimeHelper

import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.ChapterContent
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.receiver.BatteryChangeReceiver
import com.example.leafnovel.receiver.TimeChangeReceiver
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class BookContentViewModel(
    context: Context,
    firstBookChapter: BookChapter,
    chapterList: ArrayList<BookChapter>,
    _bookTitle: String
) :
    ViewModel() {
    companion object {
        const val TAG = "ViewModel MVVM測試"
    }

    val bookTitle = _bookTitle
    private var allChapter = chapterList
    private val mContext = context
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: Repository
    private val isLoadMore: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isRefresh: MutableLiveData<Boolean> = MutableLiveData(false)

    private lateinit var mBatteryChangeReceiver: BatteryChangeReceiver
    private lateinit var mTimeChangeReceiver: TimeChangeReceiver

    val systemTime: MutableLiveData<String> = MutableLiveData()
    val batteryIsCharging: MutableLiveData<Boolean> = MutableLiveData(false)
    val batteryLevel: MutableLiveData<Int> = MutableLiveData()

    //    val allChapterList: MutableLiveData<BookChsResults> = MutableLiveData()
//    小說的所有章節data，包含url和title...
    val allChapterList: MutableLiveData<ArrayList<BookChapter>> = MutableLiveData(allChapter)

    //    擺放主畫面第一個chapter用的章節data，包含標題及內文
    val chapterContent: MutableLiveData<ChapterContent> = MutableLiveData()

    //    擺放新讀取到的章節
    val loadChapterContent: MutableLiveData<ChapterContent> = MutableLiveData()

    //    擺放目前柱列中中所有暫存的章節
    private val allChapterContent: MutableLiveData<ArrayList<ChapterContent>> = MutableLiveData()

    //    擺放目前閱讀章節的基底index
    val tempChapterIndex: MutableLiveData<Int> = MutableLiveData()

    //    擺放目前閱讀的章節index
    val tempChapterReadIndex: MutableLiveData<Int> = MutableLiveData(0)

    //    擺放目前加載到最新的章節index
//    val tempChapterReadEndIndex: MutableLiveData<Int> = MutableLiveData(0)
    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        var tempContent: String
        scope.launch(Dispatchers.IO) {
            tempContent =
                repository.getSearchBookChaptersContextBeta(firstBookChapter.chUrl, firstBookChapter.chtitle, bookTitle)
            val tempChapterContent = ChapterContent(firstBookChapter.chtitle, tempContent, firstBookChapter.chUrl)
            chapterContent.postValue(tempChapterContent)
            allChapterContent.postValue(arrayListOf(tempChapterContent))
//            for (i in allChapter.indices) {
//                if (allChapter[i].chUrl == firstBookChapter.chUrl) {
//                    tempChapterIndex.postValue(i)
//                    break
//                }
//            }
        }
    }

    fun goNextChapter() = scope.launch(Dispatchers.IO) {
        isRefresh.postValue(true)
        var tempChUrl = ""
        var tempBookChTitle = ""
        var tempChapterContents = ""
        var hasNext = false
//            之後新增檢查正序倒序
        val chapterNum = tempChapterReadIndex.value
        chapterNum?.let {
            if (chapterNum != 0) {
                tempChUrl = allChapter[chapterNum + 1].chUrl
                tempBookChTitle = allChapter[chapterNum + 1].chtitle
                tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle, bookTitle)
                hasNext = true
            } else {
                hasNext = false
            }
        }
        launch(Dispatchers.Main) {
            if (hasNext) {
                Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
                val tempChapter = ChapterContent(tempBookChTitle, tempChapterContents, tempChUrl)
                chapterContent.postValue(tempChapter)
                allChapterContent.postValue(arrayListOf(tempChapter))
                chapterNum?.let {
                    tempChapterIndex.value = it + 1
                    tempChapterReadIndex.value = it + 1
//                    tempChapterReadEndIndex.value = it - 1
                }
            } else {
                Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
            }
            isRefresh.postValue(false)
        }
    }

    fun goLastChapter() = scope.launch(Dispatchers.IO) {
        isRefresh.postValue(true)
        var tempChUrl = ""
        var tempBookChTitle = ""
        var tempChapterContents = ""
        var hasNext = false
//            之後新增檢查正序倒序
        val chapterNum = tempChapterReadIndex.value
        chapterNum?.let {
            if (chapterNum != 0) {
                tempChUrl = allChapter[chapterNum - 1].chUrl
                tempBookChTitle = allChapter[chapterNum - 1].chtitle
                tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle, bookTitle)
                hasNext = true
            } else {
                hasNext = false
            }
        }
        launch(Dispatchers.Main) {
            if (hasNext) {
                Toast.makeText(mContext, "上一章", Toast.LENGTH_SHORT).show()
                val tempChapter = ChapterContent(tempBookChTitle, tempChapterContents, tempChUrl)
                chapterContent.postValue(tempChapter)
                allChapterContent.postValue(arrayListOf(tempChapter))
                chapterNum?.let {
                    tempChapterIndex.value = it - 1
                    tempChapterReadIndex.value = it - 1
//                    tempChapterReadEndIndex.value = it + 1
                }
            } else {
                Toast.makeText(mContext, "已經沒有上一章囉", Toast.LENGTH_SHORT).show()
            }
            isRefresh.postValue(false)
        }
    }

    fun loadNextChapter() {
        Log.d(TAG, "----------------------loadNextChapter(一般)----------------------------")
        scope.launch(Dispatchers.IO) {
            Log.d(TAG, "----------------------loadNextChapter(IO)----------------------------")
            Log.d(TAG, "viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
            isLoadMore.postValue(true)
            val tempChUrl: String
            val tempBookChTitle: String
            val tempChapterContentText: String
//            var tempChapterContent: ChapterContent
            var hasNext = false
            val chapterNum = tempChapterIndex.value
            val chapterReadNum = tempChapterReadIndex.value

            if (chapterNum != null && chapterReadNum != null) {
                if (chapterReadNum != 0) {
                    tempChUrl = allChapter[chapterReadNum + 1].chUrl
                    tempBookChTitle = allChapter[chapterReadNum + 1].chtitle
                    tempChapterContentText = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle, bookTitle)

                    val tempChapterContent = ChapterContent(tempBookChTitle, tempChapterContentText, tempChUrl)
                    loadChapterContent.postValue(tempChapterContent)
                    Log.d(TAG, "After viewModel loadChapterContent post${loadChapterContent.value?.chTitle}")
                    val mAllChapterContent = allChapterContent.value
                    mAllChapterContent?.let {
                        it.add(tempChapterContent)
                        allChapterContent.postValue(it)
                    }
                    hasNext = true
                } else {
                    hasNext = false
                }
            }
            launch(Dispatchers.Main) {
                Log.d(TAG, "------------------------loadNextChapter(MAIN)---------------------------")
                Log.d(TAG, "MAIN viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
                if (hasNext) {
                    Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                }
                isLoadMore.postValue(false)
            }
        }
        Log.d(TAG, " return前 viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
        Log.d(TAG, "----------------------loadNextChapterEnd(一般)----------------------------")
    }
//    fun loadNextChapter(): MutableLiveData<ChapterContent> {
//        Log.d(TAG,"----------------------loadNextChapter(一般)----------------------------")
//        scope.launch(Dispatchers.IO){
//            Log.d(TAG,"----------------------loadNextChapter(IO)----------------------------")
//            Log.d(TAG,"viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
//            isLoadMore.postValue(true)
//            var tempChUrl = ""
//            var tempBookChTitle = ""
//            var tempChapterContentText = ""
////            var tempChapterContent: ChapterContent
//            var hasNext = false
//            val chapterNum = tempChapterIndex.value
//            val chapterReadNum = tempChapterReadIndex.value
//
//            if(chapterNum != null && chapterReadNum != null){
//                if (chapterReadNum != 0) {
//                    tempChUrl = allChapter[chapterReadNum -1].chUrl
//                    tempBookChTitle = allChapter[chapterReadNum -1].chtitle
//                    tempChapterContentText = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle,bookTitle)
//
//                    val tempChapterContent = ChapterContent(tempBookChTitle, tempChapterContentText, tempChUrl)
//                    loadChapterContent.postValue(tempChapterContent)
//                    Log.d(TAG,"After viewModel loadChapterContent post${loadChapterContent.value?.chTitle}")
//                    val mAllChapterContent = allChapterContent.value
//                    mAllChapterContent?.let {
//                        it.add(tempChapterContent)
//                        allChapterContent.postValue(it)
//                    }
//                    hasNext = true
//                } else {
//                    hasNext = false
//                }
//            }
//            launch(Dispatchers.Main) {
//                Log.d(TAG,"------------------------loadNextChapter(MAIN)---------------------------")
//                Log.d(TAG,"MAIN viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
//                if (hasNext) {
//                    Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
//                }
//                    isLoadMore.postValue(false)
//            }
//    }
//        Log.d(TAG," return前 viewModel loadChapterContent ${loadChapterContent.value?.chTitle}")
//        Log.d(TAG,"----------------------loadNextChapterEnd(一般)----------------------------")
//        return loadChapterContent
//    }


    override fun onCleared() {
        super.onCleared()
        mContext.unregisterReceiver(mTimeChangeReceiver)
        mContext.unregisterReceiver(mBatteryChangeReceiver)
        parentJob.cancel()
    }

    fun initBatteryLevel() {
        val batteryStat: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            mContext.registerReceiver(null, ifilter)
        }
        val batteryPct: Int? = batteryStat?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        }

        val status = batteryStat?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val IsCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL


        val mBatteryChangeHelper = object : BatteryChangeHelper {
            override fun updateBatteryStat(level: Int, isCharging: Boolean) {
                scope.launch(Dispatchers.IO) {

                    batteryIsCharging.postValue(isCharging)
                    batteryLevel.postValue(level)
                }
            }
        }
        mBatteryChangeHelper.updateBatteryStat(batteryPct ?: 100, IsCharging)
        mBatteryChangeReceiver = BatteryChangeReceiver(mBatteryChangeHelper)
        mContext.registerReceiver(mBatteryChangeReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    //        fun getAllChapters(bookId:String):MutableLiveData<BookChsResults>{
//        scope.launch(Dispatchers.IO){
//            allChapterList.postValue(repository.getSearchBookChaptersList(bookId))
//        }
//        return allChapterList
//}
    fun initSystemTime() {
        val mSystemTimeHelper = object : SystemTimeHelper {
            override fun updateTime() {
                scope.launch(Dispatchers.IO) {
                    val calendar = Calendar.getInstance()
                    var hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    val is24hFormat = true
                    if (is24hFormat && hour > 12) {
                        hour -= 12
                    }

                    var time = ""

                    time += if (hour >= 10) hour.toString()
                    else "0$hour:"

                    time += if (minute >= 10) minute.toString()
                    else "0$minute"

                    systemTime.postValue(time)
                }
            }
        }
        mTimeChangeReceiver = TimeChangeReceiver(mSystemTimeHelper)
        mContext.registerReceiver(mTimeChangeReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        mSystemTimeHelper.updateTime()
    }

}