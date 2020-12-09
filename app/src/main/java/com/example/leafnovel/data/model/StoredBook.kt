package com.example.leafnovel.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StoredBook(
    @NonNull
    var bookname:String,
    @NonNull
    @ColumnInfo(name="author")
    var bookauthor:String,
    @NonNull
    @ColumnInfo(name="source")
    var booksource:String,
    var lastread:String,
    var bookUrl:String,
    @PrimaryKey
    @NonNull
    var bookid:String){
//    @PrimaryKey(autoGenerate = true)
//    var id:Long=0
}

