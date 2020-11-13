package com.example.leafnovel.data

import NovelApi
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.leafnovel.Book
import com.example.leafnovel.BooksResults
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.bookcase.StoredBookDB
import com.example.leafnovel.bookcase.StoredBookDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

//  for room crud operation
class Repository constructor(private val sbBooksDao:StoredBookDao) {
    val allStoredBooks : LiveData<List<StoredBook>> = sbBooksDao.getAll()

    @WorkerThread
    suspend fun insert(storedbook:StoredBook){
        sbBooksDao.insert(storedbook)
    }
    @WorkerThread
    suspend fun deletdAll(){
        sbBooksDao.deleteAll()
    }
    @WorkerThread
    fun getSearchBooks(searchKey:String):BooksResults{
        return NovelApi.RequestSearchNovelBeta(searchKey)
    }
    @WorkerThread
    fun getSearchBookChaptersList(bookId:String){
        NovelApi.RequestChList(bookId)
    }
    @WorkerThread
    fun getSearchBookChaptersContext(chapterId:String){
        NovelApi.RequestChText(chapterId)
    }
}

