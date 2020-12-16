package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import kotlin.coroutines.CoroutineContext

class BookDetailViewModel(context : Context,storedBook : StoredBook,repository:Repository)
    : ViewModel() {
    private val mContext = context
    private val mRepository = repository
    private val mStoredBook = storedBook

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    val bookInformation : MutableLiveData<Map<String,String>> = MutableLiveData()
    val storedBookInformation : MutableLiveData<StoredBook> = MutableLiveData(storedBook)
    init {
        scope.launch(Dispatchers.IO){
            bookInformation.postValue(mRepository.requestNovelDetail(mStoredBook.bookid,mStoredBook.bookname))
        }
    }

    private fun getBookDetail() {
        scope.launch(Dispatchers.IO){
            bookInformation.postValue(mRepository.requestNovelDetail(mStoredBook.bookid,mStoredBook.bookname))
        }
    }

    fun storedBook(){
        scope.launch(Dispatchers.IO){
//            val bookMap = mutableMapOf("novelState" to novelState,
//                "newChapter" to newChapter,
//                "bookDescripe" to bookDescripe,
//                "updateTime" to updateTime,
//                "imgUrl" to imgUrl)
//            bookInformation.value[]
//

        }
//                    if(booktitle!=null && author!=null && bookId!=null){
//                val storedbook = StoredBook(
//                booktitle!!,author!!,
//                "UU看書",
//                NewChapterText.text.toString(),
//                bookDetailMap["imgUrl"].toString(),
//                bookId!!)
//                CoroutineScope(Dispatchers.IO).launch {
//                repository?.insert(storedbook)
//            }
//            }
    }

}