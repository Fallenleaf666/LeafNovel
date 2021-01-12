package com.example.leafnovel.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.leafnovel.data.model.*

/*
StoredBook 書本的資訊
StoredChapter 下載的章節
LastReadProgress 書最後的閱讀進度
StoredBookFolder 分類擺放書本的資料夾
BookFavorite 當前收藏狀態
*/
@Database(entities = [StoredBook::class, StoredChapter::class, LastReadProgress::class, StoredBookFolder::class,BookFavorite::class], version = 1)
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