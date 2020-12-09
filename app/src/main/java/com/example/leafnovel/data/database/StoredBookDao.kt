package com.example.leafnovel.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leafnovel.data.model.StoredBook

@Dao
interface StoredBookDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storedBook: StoredBook)

    @Query(value = "select * from storedbook")
    fun getAll() : LiveData<List<StoredBook>>

    @Query(value = "delete from storedbook")
    fun deleteAll()

    @Delete
    fun delete(storedbook: StoredBook)
}