package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.*
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*
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
        const val TAG = "ViewModel MVVM測試"
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
    private val isLoadMore: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isRefresh: MutableLiveData<Boolean> = MutableLiveData(false)

    //    擺放主畫面第一個chapter用的章節data，包含標題及內文
    val chapterContent: MutableLiveData<ChapterContentBeta> = MutableLiveData()

    //    擺放新讀取到的章節
    val loadChapterContent: MutableLiveData<ChapterContentBeta> = MutableLiveData()

    //    擺放目前柱列中中所有暫存的章節
    val nowLookAtIndex: MutableLiveData<Int> = MutableLiveData(firstBookChapter.chIndex)

    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        var tempContent: String
//        初始化第一篇章節 若db沒有就網路抓
        scope.launch(Dispatchers.IO) {
            val dbChapter: StoredChapter? = repository.getDownloadChapter(bookId, firstBookChapter.chIndex)
            tempContent = dbChapter?.chapterContent
                ?: repository.getSearchBookChaptersContextBeta(
                    firstBookChapter.chUrl,
                    firstBookChapter.chtitle,
                    bookTitle
                )
            val tempChapterContent = ChapterContentBeta(
                firstBookChapter.chIndex,
                firstBookChapter.chtitle,
                tempContent,
                firstBookChapter.chUrl
            )
            chapterContent.postValue(tempChapterContent)
        }
    }

    fun goNextChapter() = scope.launch(Dispatchers.IO) {
        isRefresh.postValue(true)
        var tempChUrl = ""
        var tempBookChTitle = ""
        var tempChapterContents = ""
        var tempIndex = 0
        var hasNext = false
        val chapterIndex = nowLookAtIndex.value
        chapterIndex?.let {
            if (chapterIndex != allChapter.size - 1) {
                tempIndex = allChapter[chapterIndex + 1].chIndex
                tempChUrl = allChapter[chapterIndex + 1].chUrl
                tempBookChTitle = allChapter[chapterIndex + 1].chtitle
                tempChapterContents = NovelApi.requestChapterText(tempChUrl, tempBookChTitle, bookTitle)
                hasNext = true
            } else {
                hasNext = false
            }
        }
        launch(Dispatchers.Main) {
            if (hasNext) {
                Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
                chapterContent.postValue(ChapterContentBeta(tempIndex, tempBookChTitle, tempChapterContents, tempChUrl))
                chapterIndex?.let { nowLookAtIndex.postValue(tempIndex) }
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
        var tempIndex = 0
        var hasNext = false
//            之後新增檢查正序倒序
        val chapterIndex = nowLookAtIndex.value
        chapterIndex?.let {
            if (chapterIndex != 0) {
                tempIndex = allChapter[chapterIndex - 1].chIndex
                tempChUrl = allChapter[chapterIndex - 1].chUrl
                tempBookChTitle = allChapter[chapterIndex - 1].chtitle
                tempChapterContents = NovelApi.requestChapterText(tempChUrl, tempBookChTitle, bookTitle)
                hasNext = true
            } else {
                hasNext = false
            }
        }
        launch(Dispatchers.Main) {
            if (hasNext) {
                Toast.makeText(mContext, "上一章", Toast.LENGTH_SHORT).show()
                chapterContent.postValue(ChapterContentBeta(tempIndex, tempBookChTitle, tempChapterContents, tempChUrl))
                chapterIndex?.let { nowLookAtIndex.postValue(tempIndex) }
            } else {
                Toast.makeText(mContext, "已經沒有上一章囉", Toast.LENGTH_SHORT).show()
            }
            isRefresh.postValue(false)
        }
    }

    fun loadNextChapter() {
        scope.launch(Dispatchers.IO) {
            isLoadMore.postValue(true)
            val tempChUrl: String
            val tempBookChTitle: String
            val tempChapterContentText: String
            val tempIndex: Int
            var hasNext = false
            val chapterIndex = nowLookAtIndex.value
            chapterIndex?.let {
                if (it != allChapter.size - 1) {
                    tempIndex = allChapter[it + 1].chIndex
                    tempChUrl = allChapter[it + 1].chUrl
                    tempBookChTitle = allChapter[it + 1].chtitle
                    tempChapterContentText = NovelApi.requestChapterText(tempChUrl, tempBookChTitle, bookTitle)
                    loadChapterContent.postValue(
                        ChapterContentBeta(
                            tempIndex,
                            tempBookChTitle,
                            tempChapterContentText,
                            tempChUrl
                        )
                    )
                    hasNext = true
                } else {
                    hasNext = false
                }
            }
            launch(Dispatchers.Main) {
                if (hasNext) {
                    Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                }
                isLoadMore.postValue(false)
            }
        }
    }

    fun goSpecialChapter(bookCh: BookChapter) {
        scope.launch(Dispatchers.IO) {
            val dbChapter: StoredChapter? = repository.getDownloadChapter(bookId, bookCh.chIndex)
            val tempContent = dbChapter?.chapterContent
                ?: repository.getSearchBookChaptersContextBeta(bookCh.chUrl, bookCh.chtitle, bookTitle)
            val tempChapterContent = ChapterContentBeta(bookCh.chIndex, bookCh.chtitle, tempContent, bookCh.chUrl)
            chapterContent.postValue(tempChapterContent)
        }
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun saveReadProgress(lastReadProgress: LastReadProgress) {
        scope.launch(Dispatchers.IO) {
            repository.saveReadProgress(lastReadProgress)
        }
    }

    fun refresh() {
        scope.launch(Dispatchers.IO) {
            val chapterIndex = nowLookAtIndex.value
            chapterIndex?.let {
                val tempIndex = allChapter[chapterIndex - 1].chIndex
                val tempChUrl = allChapter[chapterIndex - 1].chUrl
                val tempBookChTitle = allChapter[chapterIndex - 1].chtitle
                val tempContent = repository.getSearchBookChaptersContextBeta(tempChUrl, tempBookChTitle, bookTitle)
                val tempChapterContent = ChapterContentBeta(tempIndex, tempBookChTitle, tempContent, tempChUrl)
                chapterContent.postValue(tempChapterContent)
            }
        }
    }

//    fun getAllChapters(bookId:String):MutableLiveData<BookChsResults>{
//        scope.launch(Dispatchers.IO){
//            allChapterList.postValue(repository.getSearchBookChaptersList(bookId))
//        }
//        return allChapterList
//}

}