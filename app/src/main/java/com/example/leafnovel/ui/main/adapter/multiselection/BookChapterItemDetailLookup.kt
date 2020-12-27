package com.example.leafnovel.ui.main.adapter.multiselection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.adapter.BookChapterAdapter

class BookChapterItemDetailLookup(private val recyclerView: RecyclerView)
    :ItemDetailsLookup<BookChapter>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<BookChapter>? {
        val view = recyclerView.findChildViewUnder(e.x,e.y)
    view?.let {
        return (recyclerView.getChildViewHolder(view) as BookChapterAdapter.BookChViewHolder).getItemDetails()
    }
        return null
    }
}