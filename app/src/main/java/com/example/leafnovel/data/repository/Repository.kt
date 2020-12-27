package com.example.leafnovel.data.repository

import com.example.leafnovel.data.api.NovelApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.leafnovel.data.database.StoredBookDao
import com.example.leafnovel.data.model.*

//  for room crud operation
class Repository constructor(private val sbBooksDao: StoredBookDao) {
    val allStoredBooks : LiveData<List<StoredBook>> = sbBooksDao.getAll()

    @WorkerThread
    fun insert(storedbook: StoredBook){
        sbBooksDao.insert(storedbook)
    }

    @WorkerThread
    fun saveReadProgress(lastReadProgress: LastReadProgress){
//        sbBooksDao.saveLastReadProgress(lastReadProgress)
        sbBooksDao.updateLastReadProgress(lastReadProgress)
    }

    @WorkerThread
    fun getLastReadProgress(bookId:String) : LiveData<LastReadProgress>{
        return sbBooksDao.getLastReadProgress(bookId)
    }

    @WorkerThread
    fun saveChapter(storedChapter: StoredChapter){
        sbBooksDao.saveChapter(storedChapter)
    }

    @WorkerThread
    fun getDownloadChapter(bookId:String,index:Int):StoredChapter{
        return sbBooksDao.getChapterWithBookIdAndIndex(bookId,index)
    }

    @WorkerThread
    fun queryBookChpapterIndexes(bookId:String):LiveData<List<ChapterIndex>>{
        return sbBooksDao.queryBookChpapterIndexes(bookId)
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
    fun downloadBookChapter(bookIdDownloadInfo:BookDownloadInfo){
        for(i in NovelApi.downloadBookChapter(bookIdDownloadInfo)){
            sbBooksDao.saveChapter(i)
        }
    }

    @WorkerThread
    fun getSearchBooks(searchKey:String): BooksResults {
        return NovelApi.requestSearchNovel(searchKey)
    }
    @WorkerThread
    fun getSearchBookChaptersList(bookId:String): BookChsResults {
        return NovelApi.requestChapterList(bookId)
    }
//    @WorkerThread
//    fun getSearchBookChaptersContext(chapterId:String){
//        com.example.leafnovel.data.api.NovelApi.RequestChText(chapterId)
//    }
    @WorkerThread
    fun getSearchBookChaptersContextBeta(chapterUrl:String,bookChTitle:String,bookTitle:String):String{
        return NovelApi.requestChapterText(chapterUrl,bookChTitle,bookTitle)
    }

    @WorkerThread
    fun requestNovelDetail(bookId:String,bookTitle:String):MutableMap<String, String>{
        return NovelApi.requestNovelDetail(bookId,bookTitle)
    }
}

