package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.DownloadNovelService
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.*
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import kotlin.coroutines.CoroutineContext

class BookDetailViewModel(context: Context, storedBook: StoredBook, repository: Repository) : ViewModel() {
    private val mContext = context
    private val mRepository = repository
    private val mStoredBook = storedBook

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    val bookOtherInformation: MutableLiveData<Map<String, String>> = MutableLiveData()
    val bookInformation: MutableLiveData<StoredBook> = MutableLiveData(storedBook)
    val bookChapterList: MutableLiveData<BookChsResults> = MutableLiveData()
//    上次閱讀
    var bookLastReadInfo: LiveData<LastReadProgress>
//    下載章節
    var chaptersIndexSaved: LiveData<List<ChapterIndex>>

    init {
        scope.launch(Dispatchers.IO) {
            bookOtherInformation.postValue(mRepository.requestNovelDetail(mStoredBook.bookid, mStoredBook.bookname))
            val bookChResults = NovelApi.requestChapterList(mStoredBook.bookid)
            bookChapterList.postValue(bookChResults)
        }
        chaptersIndexSaved = repository.queryBookChpapterIndexes(mStoredBook.bookid)
        bookLastReadInfo = repository.getLastReadProgress(mStoredBook.bookid)
    }

    private fun getBookDetail() {
        scope.launch(Dispatchers.IO) {
            bookOtherInformation.postValue(mRepository.requestNovelDetail(mStoredBook.bookid, mStoredBook.bookname))
        }
    }

    fun storedBook() {
        scope.launch(Dispatchers.IO) {
            val bI = bookInformation.value
            val bOI = bookOtherInformation.value
            if (bI != null && bOI != null) {
                val storedbook = StoredBook(
                    bI.bookname, bI.bookauthor, bI.booksource,
                    bOI["newChapter"] ?: "", "", bOI["imgUrl"] ?: "", false, bI.bookid
                )
                try {
                    mRepository.insert(storedbook)
                    Toast.makeText(mContext, "已將此書收藏", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
            }
        }
    }

    fun downLoadChapter(bookInfo: BookDownloadInfo?) {
//        Toast.makeText(mContext, "開始下載", Toast.LENGTH_SHORT).show()
        scope.launch(Dispatchers.IO) {
            bookInfo?.let {
                val intent = Intent().apply {
                    setClass(mContext, DownloadNovelService()::class.java)
                    putExtra("bookDownloadInfo", bookInfo)
                    action = DownloadNovelService.DOWNLOAD_SINGLE_ACTION
                }
                mContext.startService(intent)
            }
        }
    }
}
