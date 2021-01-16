package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.DownloadNovelService
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.*
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*

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
    val isBookStored: MutableLiveData<Boolean> = MutableLiveData()
    val isBookStoredStateChange: MutableLiveData<Boolean> = MutableLiveData(false)
    var bookFavorite: LiveData<BookFavorite>
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
        bookFavorite = repository.getFavoriteBook(mStoredBook.bookid)
        chaptersIndexSaved = repository.queryBookChapterIndexes(mStoredBook.bookid)
        bookLastReadInfo = repository.getLastReadProgress(mStoredBook.bookid)
    }

    private fun getBookDetail() {
        scope.launch(Dispatchers.IO) {
            bookOtherInformation.postValue(mRepository.requestNovelDetail(mStoredBook.bookid, mStoredBook.bookname))
        }
    }
//    suspend fun getBookDetail2() =
//        withContext(Dispatchers.IO){
//            mRepository.requestNovelDetail(mStoredBook.bookid, mStoredBook.bookname)
//        }


    fun storedBook() {
        scope.launch(Dispatchers.IO){
            val bI = bookInformation.value
            val bOI = bookOtherInformation.value
            if (bI != null && bOI != null) {
                val storedBook = StoredBook(
                    bI.bookname, bI.bookauthor, bI.booksource,
                    bOI["newChapter"] ?: "", "", bOI["imgUrl"] ?: "", false, -5, bI.bookid
                )
                mRepository.insert(storedBook)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(mContext, "已將${bI.bookname}放入書櫃", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }


    fun storedBookInfo(folderId:Long) {
        scope.launch(Dispatchers.IO) {
            val bI = bookInformation.value
            val bOI = bookOtherInformation.value
            if (bI != null && bOI != null) {
                val storedBook = StoredBook(
                    bI.bookname, bI.bookauthor, bI.booksource,
                    bOI["newChapter"] ?: "", "", bOI["imgUrl"] ?: "", false, folderId, bI.bookid
                )
                mRepository.insert(storedBook)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(mContext, "已將${bI.bookname}放入書櫃", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    fun addFavoriteBook(folderId:Long) {
        scope.launch(Dispatchers.IO) {
            val bI = bookInformation.value
            val bOI = bookOtherInformation.value
            if (bI != null && bOI != null) {
                val storedBook = StoredBook(
                    bI.bookname, bI.bookauthor, bI.booksource,
                    bOI["newChapter"] ?: "", "", bOI["imgUrl"] ?: "", false, -5, bI.bookid
                )
//                mRepository.insert(storedBook)
                mRepository.addFavoriteBook(storedBook,BookFavorite(folderId, bI.bookid, creattime = System.currentTimeMillis()))
                isBookStoredStateChange.postValue(true)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(mContext, "已將${bI.bookname}放入書櫃", Toast.LENGTH_SHORT).show()
//                }
            }
//            bookInformation.value?.let {
//                mRepository.addFavoriteBook(BookFavorite(folderId,it.bookid))
////                withContext(Dispatchers.Main) {
////                    Toast.makeText(mContext, "已將${bI.bookname}放入書櫃", Toast.LENGTH_SHORT).show()
////                }
//            }
        }
    }

    fun removeFavoriteBook() {
        scope.launch(Dispatchers.IO) {
            bookInformation.value?.let {
                mRepository.removeFavoriteBook(it.bookid)
                isBookStoredStateChange.postValue(true)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(mContext, "已將${bI.bookname}放入書櫃", Toast.LENGTH_SHORT).show()
//                }
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
//                    action = DownloadNovelService.DOWNLOAD_SINGLE_ACTION
                    action = DownloadNovelService.DOWNLOAD_PLURAL_ACTION
                }
                mContext.startService(intent)
            }
        }
    }

    fun downLoadOneThreadChapter(bookInfo: BookDownloadInfo?) {
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


    fun getBookFolders():Deferred<List<StoredBookFolder>> = scope.async(Dispatchers.IO) {
        mRepository.getBookFolders()
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}
