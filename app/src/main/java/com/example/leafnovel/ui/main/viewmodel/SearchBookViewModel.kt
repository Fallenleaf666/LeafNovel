package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.leafnovel.R
import com.example.leafnovel.checkNetConnect
import com.example.leafnovel.customToast
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.BooksResults
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.base.ToastCustomUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchBookViewModel(context: Context): ViewModel() {
    val searchBooksResults = MutableLiveData<ArrayList<Book>>()
    val isHasSearchBooks = MutableLiveData<Boolean?>()
//    var searchString: MutableLiveData<String> = MutableLiveData<String>("")

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository: Repository
    private val mContext = context


    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
    }

    
    fun queryBooks(searchKey: String) {
        scope.launch(Dispatchers.IO) {
            if (checkNetConnect(mContext)) {
                with(repository.getSearchBooks(searchKey)){
                    searchBooksResults.postValue(this)
                    isHasSearchBooks.postValue(this.size!=0)
                }
            } else {
                withContext(Dispatchers.Main) {
                    customToast(mContext, mContext.getString(R.string.please_check_net_connect_state)).show()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}