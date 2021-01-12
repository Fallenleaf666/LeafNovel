package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class StoredBook(
    @NonNull
    @ColumnInfo(name = "bookname")
    var bookname: String,
    @NonNull
    @ColumnInfo(name = "author")
    var bookauthor: String,
    @NonNull
    @ColumnInfo(name = "source")
    var booksource: String,
    @ColumnInfo(name = "newchapter")
    var newchapter: String,
    @ColumnInfo(name = "lastread")
    var lastread: String,
    @ColumnInfo(name = "bookUrl")
    var bookUrl: String,
    @Ignore
    var ismostlike: Boolean = false,
    @NonNull
//    @Ignore
    @ColumnInfo(name = "parent")
    var parentfolderid: Long = -5,
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "bookid")
    var bookid: String
) : Parcelable {
    constructor():this("", "", "", "", "", "", false, -5, "")
//    @PrimaryKey(autoGenerate = true)
//    var id:Long=0
}

