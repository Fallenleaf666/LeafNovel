package com.example.leafnovel.bean

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class MyBookSecondBean(
    @NonNull
    var bookname:String,
    @NonNull
    @ColumnInfo(name="author")
    var bookauthor:String,
    @NonNull
    @ColumnInfo(name="source")
    var booksource:String,
    var newchapter:String,
    var lastread:String,
    var bookUrl:String,
    @Ignore
    var ismostlike:Boolean = false,
    @PrimaryKey
    @NonNull
    var bookid:String) : Parcelable {
    constructor():this("","","","","","",false,"")
//    @PrimaryKey(autoGenerate = true)
//    var id:Long=0
}