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
    constructor():this("無名","","","匿名","","")
    constructor(bookUrl: String,author: String,booktitle: String):this(booktitle,"",bookUrl,author,"","")
}

class BookChsResults:java.util.ArrayList<BookChapter>()
@Parcelize
data class BookChapter(
    var chIndex: Int,
    var chtitle: String,
    var chUrl: String
):Parcelable{
    constructor():this(0, "", "")
}

@Parcelize
data class ChapterContent(
    var chTitle: String,
    val chContent: String,
    var chUrl: String
):Parcelable

@Parcelize
data class ChapterContentBeta(
    var chIndex: Int,
    var chTitle: String,
    val chContent: String,
    var chUrl: String
):Parcelable

@Parcelize
data class BookDownloadInfo(
    var bookName: String,
    var bookId: String,
    var download: List<BookChapter>
):Parcelable{
}

//@Parcelize
//data class ChapterDownloadInfo(
//    var index: Int,
//    var chtitle: String,
//    var chUrl: String
//):Parcelable{
//}

@Parcelize
data class SingleBookDownloadInfo(
    var bookName: String,
    var bookId: String,
    var bookChapter: BookChapter
):Parcelable

data class ChapterIndex(
    @ColumnInfo(name = "index")val index:Int?
)


@Parcelize
data class ChapterDownloadResult(
    var chIndex: Int,
    var chtitle: String,
    var chUrl: String,
    var state:ChapterDownloadState
):Parcelable{}

enum class ChapterDownloadState {
    SUCCESS,FAIL,STOP
}
