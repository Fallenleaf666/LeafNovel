package com.example.leafnovel.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leafnovel.data.model.BookWithChapter
import com.example.leafnovel.data.model.ChapterIndex
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.model.StoredChapter

@Dao
interface StoredBookDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storedBook: StoredBook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChapter(storedChapter: StoredChapter)

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