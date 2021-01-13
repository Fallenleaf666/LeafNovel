package com.example.leafnovel.ui.main.viewmodel

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.leafnovel.bean.Child
import com.example.leafnovel.bean.Group
import com.example.leafnovel.customToast
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
    val lastReadBookItem : MutableLiveData<Child> = MutableLiveData()

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

    fun moveBook(bookId:String,folderId:Long) = scope.launch(Dispatchers.IO) {
        repository.moveBook(bookId,folderId)
    }

    fun deleteBookById(bookId:String) = scope.launch(Dispatchers.IO) {
        repository.deleteBookById(bookId)
    }

    fun getBookFolders():Deferred<List<StoredBookFolder>> = scope.async(Dispatchers.IO) {
        repository.getBookFolders()
    }

    suspend fun addFolder(sbBookFolder: StoredBookFolder): Deferred<Long> =
        scope.async(Dispatchers.IO) {
            val id = repository.addBookFolder(sbBookFolder)
            withContext(Dispatchers.Main) {
//                Toast.makeText(mContext, "\"${sbBookFolder.foldername}\"建立成功！id=$id", Toast.LENGTH_SHORT).show()
                customToast(mContext,"\"${sbBookFolder.foldername}\"建立成功！id=$id").show()
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

    fun deleteBookFolderAndUpdate(folderId: Long) = scope.launch(Dispatchers.IO) {
        repository.deleteBookFolderAndUpdate(folderId)
    }

    fun updateFolder(newName: String, storedBookFolderId: Long) = scope.launch(Dispatchers.IO) {
        repository.updateBookFolder(newName, storedBookFolderId)
    }

    fun getFolderWithBook():Deferred<ArrayList<Group>> = scope.async(Dispatchers.IO) {
        repository.getFolderWithBook()
    }

    fun updateBookParentFolder(oldFolderId:Long) = scope.async(Dispatchers.IO) {
        repository.updateBookParentFolder(oldFolderId)
    }


    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun updateStoredBookOnline():Deferred<ArrayList<Group>> = scope.async(Dispatchers.IO) {
        repository.updateStoredBookOnline()
    }

}