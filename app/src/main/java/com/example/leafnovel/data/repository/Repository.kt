package com.example.leafnovel.data.repository

import com.example.leafnovel.data.api.NovelApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.leafnovel.data.model.BookChsResults
import com.example.leafnovel.data.model.BooksResults
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDao

//  for room crud operation
class Repository constructor(private val sbBooksDao: StoredBookDao) {
    val allStoredBooks : LiveData<List<StoredBook>> = sbBooksDao.getAll()

    @WorkerThread
    fun insert(storedbook: StoredBook){
        sbBooksDao.insert(storedbook)
    }
    @WorkerThread
    fun deletdAll(){
        sbBooksDao.deleteAll()
    }
    @WorkerThread
    fun delete(storedbook: StoredBook){
        sbBooksDao.delete(storedbook)
    }
    @WorkerThread
    fun getSearchBooks(searchKey:String): BooksResults {
        return NovelApi.RequestSearchNovelBeta(searchKey)
    }
    @WorkerThread
    fun getSearchBookChaptersList(bookId:String): BookChsResults {
        return NovelApi.RequestChList(bookId)
    }
//    @WorkerThread
//    fun getSearchBookChaptersContext(chapterId:String){
//        com.example.leafnovel.data.api.NovelApi.RequestChText(chapterId)
//    }
    @WorkerThread
    fun getSearchBookChaptersContextBeta(chapterUrl:String,bookChTitle:String,bookTitle:String):String{
        return NovelApi.RequestChTextBETA(chapterUrl,bookChTitle,bookTitle)
    }

    @WorkerThread
    fun requestNovelDetail(bookId:String,bookTitle:String):MutableMap<String, String>{
        return NovelApi.RequestNovelDetail(bookId,bookTitle)
    }
}

