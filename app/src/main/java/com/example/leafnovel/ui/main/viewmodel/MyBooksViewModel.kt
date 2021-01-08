package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.StoredBookFolder
import com.example.leafnovel.data.repository.Repository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

//class MyBooksViewModel(val repository: Repository) : ViewModel() {
class MyBooksViewModel(context: Context) : ViewModel() {
//class MyBooksViewModel() : ViewModel() {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val mContext = context

    private val repository: Repository
    val allsbBooks: LiveData<List<StoredBook>>
    val allsbBookFolders: LiveData<List<StoredBookFolder>>

    init {
        val sbBooksDao = StoredBookDB.getInstance(context)?.storedbookDao()
        repository = sbBooksDao?.let { Repository(it) }!!
        allsbBooks = repository.allStoredBooks
        allsbBookFolders = repository.allStoredBookFolders
    }

    fun insert(sbBook: StoredBook) = scope.launch(Dispatchers.IO) {
        repository.insert(sbBook)
    }

    fun delete(sbBook: StoredBook) = scope.launch(Dispatchers.IO) {
        repository.delete(sbBook)
    }

    suspend fun addFolder(sbBookFolder: StoredBookFolder): Deferred<Long> =
        scope.async(Dispatchers.IO) {
            val id = repository.addBookFolder(sbBookFolder)
            withContext(Dispatchers.Main) {
                Toast.makeText(mContext, "\"${sbBookFolder.foldername}\"建立成功！id=$id", Toast.LENGTH_SHORT).show()
            }
            id
        }

//    suspend fun addFolder(sbBookFolder: StoredBookFolder): Deferred<Long> {
//        async{
//
//        }
//        scope.launch(Dispatchers.IO) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(mContext, "\"${sbBookFolder.foldername}\"建立成功！", Toast.LENGTH_SHORT).show()
//            }
//            repository.addBookFolder(sbBookFolder)
//        }
//    }

    fun deleteFolder(sbBookFolder: StoredBookFolder) = scope.launch(Dispatchers.IO) {
        repository.deleteBookFolder(sbBookFolder)
    }

    fun updateFolder(newName: String, storedBookFolderId: String) = scope.launch(Dispatchers.IO) {
        repository.updateBookFolder(newName, storedBookFolderId)
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}