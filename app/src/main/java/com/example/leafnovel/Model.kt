package com.example.leafnovel

class BooksResults:java.util.ArrayList<Book>()
data class Book(
    val bookNum: String,
    var booktitle: String,
    val bookId: String,
    var bookUrl: String,
    var author: String,
    val bookDescripe: String,
    val updateTime: String
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