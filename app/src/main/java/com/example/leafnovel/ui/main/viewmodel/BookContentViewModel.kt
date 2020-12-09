package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookChsResults
import com.example.leafnovel.data.model.ChapterContent
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BookContentViewModel(context: Context, firstBookChapter: BookChapter, chapterList: ArrayList<BookChapter>, _bookTitle : String) :
    ViewModel() {
    val bookTitle = _bookTitle
    var allChapter = chapterList
    val mContext = context
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: Repository

    val allChapterList: MutableLiveData<BookChsResults> = MutableLiveData()
    val chapterContent: MutableLiveData<ChapterContent> = MutableLiveData()
    val loadChapterContent: MutableLiveData<ChapterContent> = MutableLiveData()
    val allChapterContent: MutableLiveData<ArrayList<ChapterContent>> = MutableLiveData()
    val tempChapterIndex: MutableLiveData<Int> = MutableLiveData()

    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        var tempContent = ""
        scope.launch(Dispatchers.IO) {
            tempContent = repository.getSearchBookChaptersContextBeta(firstBookChapter.chUrl, firstBookChapter.chtitle,bookTitle)
            val tempChapterContent = ChapterContent(firstBookChapter.chtitle, tempContent, firstBookChapter.chUrl)
            chapterContent.postValue(tempChapterContent)
            allChapterContent.postValue(arrayListOf(tempChapterContent))
            for (i in allChapter.indices) {
                if (allChapter[i].chUrl == firstBookChapter.chUrl) {
                    tempChapterIndex.postValue(i)
                    break
                }
            }
        }
    }

    fun goNextChapter() = scope.launch(Dispatchers.IO) {
        var tempChUrl = ""
        var tempBookChTitle = ""
        var tempChapterContents = ""
        var hasNext = false
//            之後新增檢查正序倒序
        val chapterNum = tempChapterIndex.value
        chapterNum?.let {
            if (chapterNum != 0) {
                tempChUrl = allChapter[chapterNum - 1].chUrl
                tempBookChTitle = allChapter[chapterNum - 1].chtitle
                tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl,tempBookChTitle,bookTitle)
                tempChapterIndex.postValue(chapterNum - 1)
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
            } else {
                Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun goLastChapter() = scope.launch(Dispatchers.IO) {
        var tempChUrl = ""
        var tempBookChTitle = ""
        var tempChapterContents = ""
        var hasNext = false
//            之後新增檢查正序倒序
        val chapterNum = tempChapterIndex.value
        chapterNum?.let {
            if (chapterNum != 0) {
                tempChUrl = allChapter[chapterNum + 1].chUrl
                tempBookChTitle = allChapter[chapterNum + 1].chtitle
                tempChapterContents = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle,bookTitle)
                tempChapterIndex.postValue(chapterNum + 1)
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
            } else {
                Toast.makeText(mContext, "已經沒有上一章囉", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadNextChapter():MutableLiveData<ChapterContent> {
        scope.launch(Dispatchers.IO){
            var tempChUrl = ""
            var tempBookChTitle = ""
            var tempChapterContentText = ""
            var tempChapterContent: ChapterContent
            var hasNext = false
            val chapterNum = tempChapterIndex.value
            chapterNum?.let {
                if (tempChapterIndex.value != 0) {
                    tempChUrl = allChapter[it - 1].chUrl
                    tempBookChTitle = allChapter[it - 1].chtitle
                    tempChapterContentText = NovelApi.RequestChTextBETA(tempChUrl, tempBookChTitle,bookTitle)
                    hasNext = true
                } else {
                    hasNext = false
                }
            }
            launch(Dispatchers.Main) {
                if (hasNext) {
                    Toast.makeText(mContext, "下一章", Toast.LENGTH_SHORT).show()
                    tempChapterContent = ChapterContent(tempBookChTitle, tempChapterContentText, tempChUrl)
                    chapterNum?.let {tempChapterIndex.setValue(it - 1)}
                    loadChapterContent.value = tempChapterContent
                    val mAllChapterContent = allChapterContent.value
                    mAllChapterContent?.let {
                        it.add(tempChapterContent)
                        allChapterContent.postValue(it)
                    }
                } else {
                    Toast.makeText(mContext, "已經沒有下一章囉", Toast.LENGTH_SHORT).show()
                }
            }
    }
        return loadChapterContent
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun getAllChapters(bookId:String):MutableLiveData<BookChsResults>{
        scope.launch(Dispatchers.IO){
            allChapterList.postValue(repository.getSearchBookChaptersList(bookId))
        }
        return allChapterList
}

}