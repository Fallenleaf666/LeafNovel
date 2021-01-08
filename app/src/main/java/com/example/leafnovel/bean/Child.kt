package com.example.leafnovel.bean

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey

class Child : Item() {
    var group: Group? = null
    var bookId = ""
    var bookName:String = ""
    var bookAuthor:String = ""
    var bookSource:String = ""
    var newChapter:String = ""
    var lastRead:String = ""
    var bookUrl:String = ""
    var isMostLike:Boolean = false


    companion object{
        private const val TYPE_CHILD = 0xfa02
    }
    override fun getType(): Int {
        return TYPE_CHILD
    }
}