package com.example.leafnovel.data.api

import com.example.leafnovel.data.model.BookChsResults
import com.example.leafnovel.data.model.BooksResults

interface NovelRequestApi {
    fun RequestChapterContent(url: String, bookTitle: String): String
    fun RequestSearchNovels(searchContent: String): BooksResults
    fun RequestChapterList(id: String): BookChsResults
    fun RequestNovelDetail(bookId: String): MutableMap<String, String>
}