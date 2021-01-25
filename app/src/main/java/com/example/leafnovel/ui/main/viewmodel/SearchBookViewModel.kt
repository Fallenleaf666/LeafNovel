package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.util.Log
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
import com.example.leafnovel.data.model.SearchResult
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.base.ToastCustomUtil
import com.github.houbb.opencc4j.util.ZhConverterUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

class SearchBookViewModel(context: Context): ViewModel() {
    companion object{
        const val TAG = "SearchBookViewModel"
    }
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
        scope.launch(Dispatchers.IO) {
            var s ="这是一个测试字串，因为初次使用会等待较久时间"
//            val time = measureTimeMillis {
                s = ZhConverterUtil.toTraditional(s)
//            }
//            Log.d(TAG,"time${time} ${s}")
        }
    }

    
    fun queryBooks(searchKey: String) {
        scope.launch(Dispatchers.IO) {
            if (checkNetConnect(mContext)) {
                with(repository.getSearchBooks(searchKey)){
                    when(this.state){
                        SearchResult.SUCCESS ->{
                            searchBooksResults.postValue(this.booksResults)
                            isHasSearchBooks.postValue(this.booksResults.size!=0)
                        }
                        SearchResult.FAIL ->{
                            searchBooksResults.postValue(this.booksResults)
                            customToast(mContext, mContext.getString(R.string.server_longtime_no_response)).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    customToast(mContext, mContext.getString(R.string.please_check_net_connect_state)).show()
                    searchBooksResults.postValue(null)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}