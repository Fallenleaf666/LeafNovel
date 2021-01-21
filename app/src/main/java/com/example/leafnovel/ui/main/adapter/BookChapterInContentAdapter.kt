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

class BookChapterInContentAdapter : RecyclerView.Adapter<BookChapterInContentAdapter.BookChViewHolder>() {
    private val items = ArrayList<BookChapter>()
    private var listener: OnItemClickListener? = null

    private var thisPosition: Int? = null
    private var savedChapterIndexes: List<ChapterIndex>? = null
    private var isPostiveOrder: Boolean = true
    private var indexesList: ArrayList<Int> = ArrayList()
    private var setMoreVisible = true
    private var isSmallfontSize = false


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
            downloadChapter.visibility = View.GONE
            savedStat.visibility = View.GONE
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

        fun setChapterBackground() {
            chapterRow.setBackgroundResource(R.drawable.item_chapter_background)
        }

        fun setFontSize() {
            title.textSize = 12f
        }
    }

    interface OnItemClickListener {
        fun onItemClick(bookCh: BookChapter, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookChViewHolder {
        return BookChViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_bookchapter, parent, false))
    }

    override fun onBindViewHolder(holder: BookChViewHolder, position: Int) {
        holder.title.text = items[position].chtitle
        holder.index.text = position.toString()

        if (position == getThisPosition()) {
            holder.chapterRow.setBackgroundResource(R.color.selectChBg)
        } else {
            holder.chapterRow.setBackgroundColor(Color.TRANSPARENT)
        }
        holder.setFontSize()
    }

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int) = items[position]
    fun getPosition(chapterId: Int) = items.indexOfFirst { it.chIndex == chapterId }

}