package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//class MyBooksViewModel(val repository: Repository) : ViewModel() {
class MyDownloadViewModel(context: Context) : ViewModel() {
//class MyBooksViewModel() : ViewModel() {

    private var parentJob = Job()
    private val coroutineContext : CoroutineContext
    get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository : Repository
    val allDownloadMission : LiveData<List<StoredBook>>
    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        allDownloadMission = repository.allStoredBooks
    }

    fun stopDownloadById(sbBook: StoredBook) = scope.launch(Dispatchers.IO){
//        repository.insert(sbBook)
    }

    fun deleteDownloadMission(sbBook: StoredBook) = scope.launch(Dispatchers.IO){
//        repository.delete(sbBook)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}