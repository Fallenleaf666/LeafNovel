package com.example.leafnovel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BooksResults:java.util.ArrayList<Book>()
//class BooksResults:List<Book>
data class Book(
    var bookNum: String,
    var booktitle: String,
    var bookId: String,
    var bookUrl: String,
    var author: String,
    var bookDescripe: String,
    var updateTime: String
)
class BookChsResults:java.util.ArrayList<BookChapter>()
data class BookChapter(
    var chtitle: String,
    val chId: String,
    var chUrl: String
)

class ChapterContents:java.util.ArrayList<ChapterContent>()
data class ChapterContent(
    var chapterLine: Int,
    var chapterLineContent: String
)