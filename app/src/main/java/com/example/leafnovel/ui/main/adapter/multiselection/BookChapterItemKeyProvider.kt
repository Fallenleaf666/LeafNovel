package com.example.leafnovel.ui.main.adapter.multiselection

import androidx.recyclerview.selection.ItemKeyProvider
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.adapter.BookChAdapter

class BookChapterItemKeyProvider(private val adapter:BookChAdapter):ItemKeyProvider<BookChapter>(SCOPE_CACHED){
    override fun getKey(position: Int): BookChapter? {
        return adapter.getItem(position)
    }

    override fun getPosition(key: BookChapter): Int {
        return adapter.getPosition(key.chId)
    }
}