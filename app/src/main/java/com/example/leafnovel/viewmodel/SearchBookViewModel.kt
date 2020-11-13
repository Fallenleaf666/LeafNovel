package com.example.leafnovel.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.leafnovel.Book
import com.example.leafnovel.BooksResults
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.bookcase.StoredBookDB
import com.example.leafnovel.data.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchBookViewModel(context: Context): ViewModel() {
    val searchBooksResults = MutableLiveData<ArrayList<Book>>()
//    var searchString: MutableLiveData<String> = MutableLiveData<String>("")

    private var parentJob = Job()
    private val coroutineContext : CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    val repository : Repository

    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
    }
//可把回傳取消掉
    fun SearchBooks(searchKey : String): MutableLiveData<ArrayList<Book>>{
//        searchString.postValue(searchKey)
        var allSearchBooks = ArrayList<Book>()
        scope.launch(Dispatchers.IO){
            allSearchBooks = repository.getSearchBooks(searchKey)
            searchBooksResults.postValue(allSearchBooks)
        }
        return searchBooksResults
    }
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}