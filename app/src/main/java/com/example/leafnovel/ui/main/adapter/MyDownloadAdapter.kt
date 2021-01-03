package com.example.leafnovel.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.R
import com.example.leafnovel.data.model.ChapterIndex
import kotlinx.android.synthetic.main.row_bookchapter.view.*

class MyDownloadAdapter : RecyclerView.Adapter<MyDownloadAdapter.BookChViewHolder>() {
    private val items = ArrayList<BookChapter>()
    private var listener: OnItemClickListener? = null

    private var thisPosition: Int? = null
    private var savedChapterIndexes: List<ChapterIndex>? = null
    private var isPostiveOrder: Boolean = true

    var tracker: SelectionTracker<BookChapter>? = null
    fun getThisPosition(): Int? {
        return thisPosition
    }

    fun setItems(bookChs: ArrayList<BookChapter>, itemClickListener: OnItemClickListener) {
        items.clear()
        items.addAll(bookChs)
        listener = itemClickListener
        notifyDataSetChanged()
    }

    fun getSpecialItems(startIndex: Int, endIndex: Int): List<BookChapter> {
        val tempList = ArrayList<BookChapter>()
        for (i in startIndex downTo endIndex) {
            tempList.add(items[i])
        }
        return tempList.toList()
    }

    fun setSavedIndex(indexes: List<ChapterIndex>) {
        savedChapterIndexes = indexes
        for (i in indexes) {
            i.index?.let { notifyItemChanged(it) }
        }
    }

    fun reversedItems(isReverseOrder: Boolean) {
        isPostiveOrder = !isReverseOrder
        items.reverse()
        thisPosition = items.size - 1 - (thisPosition?:0)
        if (isReverseOrder) {
            if (items[0].chIndex == 0) {
                items.reverse()
            }
        } else {
            if (items[0].chIndex != 0) {
                items.reverse()
            }
        }
//        thisPosition?.let { lastPositionChange(items.size - 1 - it) }
        notifyDataSetChanged()
    }

    fun lastPositionChange(selectPosition: Int) {
        val newPosition = if (isPostiveOrder) selectPosition else items.size - 1 - selectPosition
//        if (selectPosition != RecyclerView.NO_POSITION) {
//            thisPosition?.let { notifyItemChanged(it) }
//            thisPosition = selectPosition
//            notifyItemChanged(selectPosition)
//        }
        if (newPosition != RecyclerView.NO_POSITION) {
            thisPosition?.let { notifyItemChanged(it) }
            thisPosition = newPosition
            notifyItemChanged(newPosition)
        }
    }

    inner class BookChViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        //        val view:View = view
        val title: TextView = view.ChapterTitle
        val index: TextView = view.IndexView
        val chapterRow: LinearLayout = view.ChapterRow
        val downloadChapter: ImageView = view.DownloadBT
        val savedStat: ImageView = view.Saved_StatView

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            val oldPosition = getThisPosition()
            if (position != RecyclerView.NO_POSITION) {
//            which you click will change color
                thisPosition = position
                val tempBookCh = items[position]
                listener?.onItemClick(tempBookCh, position)
                oldPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(position)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<BookChapter> =
            object : ItemDetailsLookup.ItemDetails<BookChapter>() {
                override fun getPosition(): Int {
                    return adapterPosition
                }

                override fun getSelectionKey(): BookChapter? {
                    return items[position]
                }
            }

        fun setChapterBackground(isActivated: Boolean = false) {
            chapterRow.setBackgroundResource(R.drawable.item_chapter_background)
            itemView.isActivated = isActivated
        }

        fun setSavedChapterBackground(resId: Int) {
            savedStat.setImageResource(resId)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(bookCh: BookChapter, position: Int)
        fun onMoreClick(bookCh: BookChapter, position: Int, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookChViewHolder {
        return BookChViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_bookchapter, parent, false))
    }

    override fun onBindViewHolder(holder: BookChViewHolder, position: Int) {
        tracker?.let {
            holder.setChapterBackground(it.isSelected(items[position]))
        }
        holder.title.text = items[position].chtitle
        holder.index.text = position.toString()

        tracker?.let {
            if (!it.hasSelection()) {
                if (position == getThisPosition()) {
//            holder.chapterRow.setBackgroundColor(R.color.selectChBg)
                    holder.chapterRow.setBackgroundResource(R.color.selectChBg)
                } else {
//            holder.chapterRow.setBackgroundResource(R.color.unselectChBg)
                    holder.chapterRow.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

        holder.downloadChapter.setOnClickListener {
            listener?.onMoreClick(items[position], position, it)
        }
        savedChapterIndexes?.let {
            if (it.contains(ChapterIndex(position))) {
//                holder.savedStat.setImageResource(R.drawable.chapter_saved_stat)
                holder.setSavedChapterBackground(R.drawable.chapter_saved_stat)
            } else {
                holder.setSavedChapterBackground(R.drawable.chapter_saved_stat_unsaved)
//                holder.savedStat.setImageResource(R.drawable.chapter_saved_stat_unsaved)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int) = items[position]
    fun getPosition(chapterId: Int) = items.indexOfFirst { it.chIndex == chapterId }


}