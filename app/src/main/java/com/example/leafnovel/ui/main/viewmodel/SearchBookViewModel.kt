package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.repository.Repository
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
    private val repository : Repository

    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
    }
//可把回傳取消掉
    fun searchBooks(searchKey: String): MutableLiveData<ArrayList<Book>>{
//        searchString.postValue(searchKey)
//        var allSearchBooks = ArrayList<Book>()
        scope.launch(Dispatchers.IO){
//            allSearchBooks = repository.getSearchBooks(searchKey)
            searchBooksResults.postValue(repository.getSearchBooks(searchKey))
        }
        return searchBooksResults
    }
    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}