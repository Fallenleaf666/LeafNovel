package com.example.leafnovel.data.repository

import android.util.Log
import com.example.leafnovel.data.api.NovelApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.bumptech.glide.Glide
import com.example.leafnovel.bean.Group
import com.example.leafnovel.data.database.StoredBookDao
import com.example.leafnovel.data.model.*
import kotlinx.android.synthetic.main.fragment_book_introduce.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

//  for room crud operation
class Repository constructor(private val sbBooksDao: StoredBookDao) {
    companion object{
        const val TAG = "Repository"
    }
    val allStoredBooks : LiveData<List<StoredBook>> = sbBooksDao.getAll()
    val allStoredBookFolders : LiveData<List<StoredBookFolder>> = sbBooksDao.getAllStoredBookFolder()

    @WorkerThread
    fun insert(storedBook: StoredBook){
        sbBooksDao.insert(storedBook)
    }

//    @WorkerThread
//    fun updateStoredBook(storedBook: StoredBook){
//        sbBooksDao.insert(storedBook)
//    }

    @WorkerThread
    fun getStoredBook(storedBookId: String):StoredBook{
        return sbBooksDao.getStoredBook(storedBookId)
    }


    @WorkerThread
    fun addBookFolder(storedBookFolder: StoredBookFolder):Long{
        return sbBooksDao.addBookFolder(storedBookFolder)
    }

    @WorkerThread
    fun deleteBookFolderAndUpdate(folderid: Long){
//        sbBooksDao.deleteBookFolder(folderid)
        sbBooksDao.deleteBookFolderAndUpdate(folderid)
    }

    @WorkerThread
    fun updateBookFolder(newName:String,storedBookFolderId: Long){
        sbBooksDao.updateBookFolder(newName,storedBookFolderId)
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
    fun queryBookChapterIndexes(bookId:String):LiveData<List<ChapterIndex>>{
        return sbBooksDao.queryBookChpapterIndexes(bookId)
    }
    @WorkerThread
    fun deleteAll(){
        sbBooksDao.deleteAll()
    }
    @WorkerThread
    fun deleteAllChapter(){
        sbBooksDao.deleteAllChapter()
    }
    @WorkerThread
    fun delete(storedBook: StoredBook){
        sbBooksDao.delete(storedBook)
    }

    @WorkerThread
    fun deleteBookById(bookId:String){
        sbBooksDao.deleteBookById(bookId)
    }

//    @WorkerThread
//    fun downloadBookChapter(bookIdDownloadInfo:BookDownloadInfo){
//        Log.d(TAG,"-----------NotOneThread-----------")
//        val beforeTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
//        var tempTime = ""
//        Log.d(TAG,"before$beforeTime")
//        for(i in NovelApi.downloadBookChapterThread(bookIdDownloadInfo)){
//                tempTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
//                sbBooksDao.saveChapter(i)
//                Log.d(TAG,"$i $tempTime")
//        }
//        val afterTime = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Date(System.currentTimeMillis()))
//        Log.d(TAG,"after $afterTime")
//        Log.d(TAG,"-----------NotOneThread-----------")
//    }

//    @WorkerThread
//    fun downloadBookChapterBeta(bookIdDownloadInfo:BookDownloadInfo){
//        val bookName = bookIdDownloadInfo.bookName
//        val bookId = bookIdDownloadInfo.bookId
//        val downloadChaptersList = bookIdDownloadInfo.download
//        Log.d(TAG,"-----------OneThread-----------")
//        for(i in downloadChaptersList){
//            val chapterContentText = NovelApi.requestChapterText(i.chUrl,i.chtitle, bookName)
//            sbBooksDao.saveChapter(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
//        }
//        Log.d(TAG,"-----------OneThread-----------")
//    }

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
    @WorkerThread
    fun getFolderWithBook():ArrayList<Group>{
//        return sbBooksDao.getFolderWithBook()
        return sbBooksDao.getFolderWithBookBeta()
    }

    @WorkerThread
    fun updateBookParentFolder(oldFolderId:Long){
        return sbBooksDao.updateBookParentFolder(oldFolderId)
    }

    @WorkerThread
    fun getBookFolders():List<StoredBookFolder>{
        return sbBooksDao.getBookFolders()
    }

    @WorkerThread
    fun moveBook(bookId: String, folderId: Long) {
        return sbBooksDao.moveBook(bookId,folderId)
    }
    @WorkerThread
    fun getFavoriteBook(bookId:String) : LiveData<BookFavorite>{
        return sbBooksDao.getFavoriteBook(bookId)
    }

    @WorkerThread
    fun addFavoriteBook(storedBook:StoredBook,bookFavorite:BookFavorite){
//        return sbBooksDao.addFavoriteBook(storedBook,bookFavorite)
        return sbBooksDao.addStoredBookAndFavoriteBook(storedBook,bookFavorite)
    }


    @WorkerThread
    fun removeFavoriteBook(bookId:String){
        return sbBooksDao.removeFavoriteBook(bookId)
    }

    @WorkerThread
    fun updateStoredBookOnline(): ArrayList<Group> {
        val favoriteBooks = sbBooksDao.getAllFavoriteBook()
        for(book in favoriteBooks){
            val oldBookInfo = sbBooksDao.getStoredBook(book.bookid)
            val newBookInfo = NovelApi.requestNovelDetailForRefresh(oldBookInfo.bookid,oldBookInfo.bookname)
//            val updateStoredBookInfo = StoredBookUpdateInfo(newBookInfo["newChapter"]?:oldBookInfo.newchapter)
            sbBooksDao.updateStoredBook(oldBookInfo.bookid,newBookInfo["newChapter"]?:oldBookInfo.newchapter)
        }
        return sbBooksDao.getFolderWithBookBeta()
    }
}

