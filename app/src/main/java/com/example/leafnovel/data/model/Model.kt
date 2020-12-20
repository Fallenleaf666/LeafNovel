package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

class BooksResults:java.util.ArrayList<Book>()
//class BooksResults:List<Book>

@Parcelize
data class Book(
//    var bookNum: String,
    var booktitle: String,
    var bookId: String,
    var bookUrl: String,
    var author: String,
    var bookDescripe: String,
    var updateTime: String
):Parcelable{
    constructor():this("","","","","","")
}

class BookChsResults:java.util.ArrayList<BookChapter>()
@Parcelize
data class BookChapter(
    var chtitle: String,
    var chId: String,
    var chUrl: String
):Parcelable

@Parcelize
data class ChapterContent(
    var chTitle: String,
    val chContent: String,
    var chUrl: String
):Parcelable

@Parcelize
data class BookDownloadInfo(
    var bookName: String,
    var bookId: String,
    var download:List<ChapterDownloadInfo>
):Parcelable{
}

@Parcelize
data class ChapterDownloadInfo(
    var chtitle: String,
    val chId: String,
    var chUrl: String,
    var index: Int
):Parcelable{
}

data class ChapterIndex(
    @ColumnInfo(name = "index")val index:Int?
)
