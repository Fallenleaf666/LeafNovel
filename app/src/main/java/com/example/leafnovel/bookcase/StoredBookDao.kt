package com.example.leafnovel.bookcase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoredBookDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storedBook: StoredBook)

    @Query(value = "select * from storedbook")
    fun getAll() : LiveData<List<StoredBook>>

    @Query(value = "delete from storedbook")
    fun deleteAll()
}