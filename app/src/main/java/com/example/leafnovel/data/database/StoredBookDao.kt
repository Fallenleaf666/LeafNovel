package com.example.leafnovel.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leafnovel.data.model.*

@Dao
interface StoredBookDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storedBook: StoredBook)

    @Query("UPDATE storedbook SET lastread = :lastReadChapter where bookid = :bookId")
    fun updateStoreBookLastReadProgress(lastReadChapter:String,bookId:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChapter(storedChapter: StoredChapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLastReadProgress(lastReadProgress: LastReadProgress)

    @Transaction
    fun updateLastReadProgress(lastReadProgress: LastReadProgress){
        saveLastReadProgress(lastReadProgress)
        updateStoreBookLastReadProgress(lastReadProgress.chapterTitle,lastReadProgress.bookId)
    }

    @Query(value = "select * from LastReadProgress where bookId = :bookId")
    fun getLastReadProgress(bookId:String) : LiveData<LastReadProgress>

    @Query(value = "select * from storedBook")
    fun getAll() : LiveData<List<StoredBook>>

    @Query(value = "delete from storedBook")
    fun deleteAll()

    @Delete
    fun delete(storedbook: StoredBook)

    @Transaction
    @Query("select * from storedBook where bookId IN(:bookId) ")
    fun getChapterWithBook(bookId:String):List<BookWithChapter>

    @Query("select * from StoredChapter where bookId = :bookId and [index] = :index")
    fun getChapterWithBookIdAndIndex(bookId:String,index:Int):StoredChapter

    @Query("select [index] from StoredChapter where bookId = :bookId")
    fun queryBookChpapterIndexes(bookId:String):LiveData<List<ChapterIndex>>
}