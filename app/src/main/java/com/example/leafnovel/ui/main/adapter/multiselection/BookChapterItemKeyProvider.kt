package com.example.leafnovel.ui.main.adapter.multiselection

import androidx.recyclerview.selection.ItemKeyProvider
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.adapter.BookChapterAdapter

class BookChapterItemKeyProvider(private val adapter:BookChapterAdapter):ItemKeyProvider<BookChapter>(SCOPE_CACHED){
    override fun getKey(position: Int): BookChapter? {
        return adapter.getItem(position)
    }

    override fun getPosition(key: BookChapter): Int {
        return adapter.getPosition(key.chIndex)
    }
}