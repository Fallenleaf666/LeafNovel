package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.BooksResults
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchBookViewModel(context: Context): ViewModel() {
    val searchBooksResults = MutableLiveData<ArrayList<Book>>()
//    var searchString: MutableLiveData<String> = MutableLiveData<String>("")

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: Repository


    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
    }

    
    fun queryBooks(searchKey: String) {
        scope.launch(Dispatchers.IO){
            searchBooksResults.postValue(repository.getSearchBooks(searchKey))
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}