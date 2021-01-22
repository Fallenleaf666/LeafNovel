package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.text.style.LineHeightSpan
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.R
import com.example.leafnovel.`interface`.BatteryChangeHelper
import com.example.leafnovel.`interface`.SystemTimeHelper
import com.example.leafnovel.checkNetConnect
import com.example.leafnovel.customToast
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.*
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.receiver.BatteryChangeReceiver
import com.example.leafnovel.receiver.TimeChangeReceiver
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class BookContentBetaViewModel(
    context: Context,
    firstBookChapter: BookChapter,
    chapterList: ArrayList<BookChapter>,
    _bookTitle: String,
    _bookId: String
) :
    ViewModel() {
    companion object {
        const val TAG = "BookContentBetaViewModel"
    }

    val bookTitle = _bookTitle
    val bookId = _bookId
    private var allChapter = chapterList
    private val mContext = context
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: Repository
    val isLoadMore: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefresh: MutableLiveData<Boolean> = MutableLiveData(false)
    private var hasNetConnect = true
    private var hasDbData = true
    //    擺放主畫面第一個chapter用的章節data，包含標題及內文
    val chapterContent: MutableLiveData<ChapterContentBeta> = MutableLiveData()

    //    擺放新讀取到的章節
    val loadChapterContent: MutableLiveData<ChapterContentBeta> = MutableLiveData()

    //    擺放目前柱列中中所有暫存的章節
    val nowLookAtIndex: MutableLiveData<Int> = MutableLiveData(firstBookChapter.chIndex)

    private lateinit var mBatteryChangeReceiver: BatteryChangeReceiver
    private lateinit var mTimeChangeReceiver: TimeChangeReceiver

    val systemTime: MutableLiveData<String> = MutableLiveData()
    val batteryIsCharging: MutableLiveData<Boolean> = MutableLiveData(false)
    val batteryLevel: MutableLiveData<Int> = MutableLiveData()


    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        //初始化第一篇章節 若db沒有就網路抓
        getChapter(Target.ASSIGN, false, firstBookChapter.chIndex)
    }

    fun goNextChapter() = getChapter(Target.NEXT, false, null)

    fun goLastChapter() = getChapter(Target.LAST, false, null)

    fun loadNextChapter() = getChapter(Target.NEXT, true, null)

    fun goSpecialChapter(bookChapter: BookChapter) = getChapter(Target.ASSIGN, false, bookChapter.chIndex)

    fun saveReadProgress(lastReadProgress: LastReadProgress) = scope.launch(Dispatchers.IO) {
        repository.saveReadProgress(lastReadProgress)
    }


    fun refresh() = scope.launch(Dispatchers.IO) {
        isRefresh.postValue(true)
        val chapterIndex = nowLookAtIndex.value
        chapterIndex?.let {
            val tempIndex = allChapter[it].chIndex
            val tempChUrl = allChapter[it].chUrl
            val tempBookChTitle = allChapter[it].chtitle
            if (checkNetConnect(mContext)) {
                val tempContent = repository.getSearchBookChaptersContextBeta(tempChUrl, tempBookChTitle, bookTitle)
                val tempChapterContent = ChapterContentBeta(tempIndex, tempBookChTitle, tempContent, tempChUrl)
                chapterContent.postValue(tempChapterContent)
            } else {
                withContext(Dispatchers.Main) {
                    customToast(mContext, mContext.getString(R.string.please_check_net_connect_state)).show()
                }
            }
        }
        isRefresh.postValue(false)
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

                    time += if (hour >= 10) "$hour:"
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
//    fun getAllChapters(bookId:String):MutableLiveData<BookChsResults>{
//        scope.launch(Dispatchers.IO){
//            allChapterList.postValue(repository.getSearchBookChaptersList(bookId))
//        }
//        return allChapterList
//}

    //targetChapter 欲取得章節的方向 ，isLoadMoreChapter 是否為上拉加載的章節 ，specialChapter 若是由目錄選擇章節則傳入該章節屬性
    private fun getChapter(targetChapter: Target, isLoadMoreChapter: Boolean, specialChapterIndex: Int?) {
        scope.launch(Dispatchers.IO) {
            var tempChUrl = ""
            var tempBookChTitle = ""
            var tempChapterContents = ""
            var tempIndex = 0
            var targetIndex = 0
            var hasTarget = false
            val chapterIndex = nowLookAtIndex.value

            isLoadMore.postValue(true)
            chapterIndex?.let {
                when (targetChapter) {
                    Target.NEXT -> {
                        hasTarget = chapterIndex != allChapter.size - 1
                        targetIndex = chapterIndex + 1
                    }
                    Target.LAST -> {
                        hasTarget = chapterIndex != 0
                        targetIndex = chapterIndex - 1
                    }
                    Target.ASSIGN -> {
                        specialChapterIndex?.let {
                            hasTarget = it < allChapter.size
                            targetIndex = it
                        }
                    }
                }
                if (hasTarget) {
                    tempIndex = allChapter[targetIndex].chIndex
                    tempChUrl = allChapter[targetIndex].chUrl
                    tempBookChTitle = allChapter[targetIndex].chtitle
                    val dbChapter: StoredChapter? = repository.getDownloadChapter(bookId, tempIndex)
                    tempChapterContents = dbChapter?.chapterContent
                        ?: mContext.getString(R.string.db_no_data)
                    if (tempChapterContents == mContext.getString(R.string.db_no_data)) {
                        hasDbData = false
                        if (checkNetConnect(mContext)) {
                            hasNetConnect = true
                            tempChapterContents =
                                repository.getSearchBookChaptersContextBeta(tempChUrl, tempBookChTitle, bookTitle)
                        } else {
                            hasNetConnect = false
                            tempChapterContents = "無法讀取 ${bookTitle}${allChapter[targetIndex].chtitle}之章節資訊，請在確認網路連線後，下拉刷新！"
                        }
                    } else {
                        hasDbData = true
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (hasTarget && (hasNetConnect || hasDbData)) {
                    when (targetChapter) {
                        Target.NEXT ->
                            customToast(mContext,mContext.getString(R.string.next_chapter)).show()
                        Target.LAST ->
                            customToast(mContext,mContext.getString(R.string.last_chapter)).show()
                        Target.ASSIGN -> {
                        }
                    }
                    if (isLoadMoreChapter) {
                        loadChapterContent.postValue(
                            ChapterContentBeta(
                                tempIndex,
                                tempBookChTitle,
                                tempChapterContents,
                                tempChUrl
                            )
                        )
                    } else {
                        chapterContent.postValue(
                            ChapterContentBeta(
                                tempIndex,
                                tempBookChTitle,
                                tempChapterContents,
                                tempChUrl
                            )
                        )
                        chapterIndex?.let { nowLookAtIndex.postValue(tempIndex) }
                    }
                } else if (!hasTarget) {
                    when (targetChapter) {
                        Target.NEXT ->
                            customToast(mContext,mContext.getString(R.string.no_next_chapter)).show()
                        Target.LAST ->
                            customToast(mContext,mContext.getString(R.string.no_last_chapter)).show()
                        Target.ASSIGN -> {
                        }
                    }
                } else {
                    if(!isLoadMoreChapter){
                        chapterContent.postValue(
                            ChapterContentBeta(
                                tempIndex,
                                tempBookChTitle,
                                tempChapterContents,
                                tempChUrl
                            )
                        )
                    }
                    customToast(mContext,mContext.getString(R.string.please_check_net_connect_state)).show()
                }
                isLoadMore.postValue(false)
            }
        }
    }

    enum class Target {
        NEXT, LAST, ASSIGN
    }

    override fun onCleared() {
        super.onCleared()
        mContext.unregisterReceiver(mTimeChangeReceiver)
        mContext.unregisterReceiver(mBatteryChangeReceiver)
        parentJob.cancel()
    }
}