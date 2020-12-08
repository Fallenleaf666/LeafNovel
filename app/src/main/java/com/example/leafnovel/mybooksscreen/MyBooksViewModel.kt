package com.example.leafnovel.mybooksscreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.bookcase.StoredBookDB
import com.example.leafnovel.data.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//class MyBooksViewModel(val repository: Repository) : ViewModel() {
class MyBooksViewModel(context: Context) : ViewModel() {
//class MyBooksViewModel() : ViewModel() {

    private var parentJob = Job()
    private val coroutineContext : CoroutineContext
    get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository : Repository
    val allsbBooks : LiveData<List<StoredBook>>
    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        allsbBooks = repository.allStoredBooks
    }

    fun insert(sbBook:StoredBook) = scope.launch(Dispatchers.IO){
        repository.insert(sbBook)
    }

    fun delete(sbBook:StoredBook) = scope.launch(Dispatchers.IO){
        repository.delete(sbBook)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}