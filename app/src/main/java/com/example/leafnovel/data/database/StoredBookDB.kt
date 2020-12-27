package com.example.leafnovel.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.leafnovel.data.model.LastReadProgress
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.model.StoredChapter

@Database(entities = [StoredBook::class, StoredChapter::class, LastReadProgress::class], version = 1)
abstract class StoredBookDB : RoomDatabase() {
    abstract fun storedbookDao(): StoredBookDao

    companion object {
        private var instance: StoredBookDB? = null
        fun getInstance(context: Context): StoredBookDB? {
            if (instance == null) {
//                instance = Room.databaseBuilder(context,StoredBookDB::class.java,"leafnoveldb")
                instance = Room.databaseBuilder(context.applicationContext, StoredBookDB::class.java, "leafnoveldb")
                    .build()
            }
            return instance
        }
    }
}