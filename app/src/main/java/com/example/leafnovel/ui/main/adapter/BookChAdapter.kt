package com.example.leafnovel.ui.main.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.R
import com.example.leafnovel.data.model.BookChsResults
import com.example.leafnovel.data.model.ChapterIndex
import kotlinx.android.synthetic.main.row_bookchapter.view.*

class BookChAdapter : RecyclerView.Adapter<BookChAdapter.BookChViewHolder>() {
    private val items = ArrayList<BookChapter>()
    private var listener: OnItemClickListener? = null

    private var thisPosition: Int? = null
    private var savedChapterIndexes: List<ChapterIndex>? = null
    fun getThisPosition(): Int? {
        return thisPosition
    }
//    fun setThisPosition(position: Int)
//    {this.thisPosition = position}

    fun setItems(bookChs: ArrayList<BookChapter>, itemClickListener: OnItemClickListener) {
        items.clear()
        items.addAll(bookChs)
        listener = itemClickListener
        notifyDataSetChanged()
    }

    fun setSavedIndex(indexes:List<ChapterIndex>) {
        savedChapterIndexes = indexes
        for(i in indexes){
            i.index?.let { notifyItemChanged(it) }
        }
//        notifyDataSetChanged()
    }

    fun reversedItems() {
        val reversedNovelChs = items.reversed()
        items.clear()
        items.addAll(reversedNovelChs)
        thisPosition?.let { lastPositionChange(items.size - 1 - it) }
        notifyDataSetChanged()
    }

    fun lastPositionChange(selectPosition : Int){
        if (selectPosition != RecyclerView.NO_POSITION) {
            thisPosition?.let { notifyItemChanged(it) }
            thisPosition = selectPosition
            notifyItemChanged(selectPosition)
        }
    }

//    fun downloadChange(index : Int){
//        if (index != RecyclerView.NO_POSITION) {
//            notifyItemChanged(index)
//        }
//    }
//    fun reverseItems(){
//        items.reverse()
//        notifyDataSetChanged()
//    }

    inner class BookChViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.ChapterTitle
        val index: TextView = view.IndexView
        val chapterRow: LinearLayout = view.ChapterRow
        val doenloadChapter: ImageView = view.DownloadBT
        val savedStat: ImageView = view.Saved_StatView

        //        val booktitle=view.book_name
//        val bookUrl=view.other
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
                listener?.onItemClick(tempBookCh,position)
                oldPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(bookCh: BookChapter,position:Int)
        fun onMoreClick(bookCh: BookChapter,position:Int,view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookChViewHolder {
        return BookChViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_bookchapter, parent, false))
    }

    override fun onBindViewHolder(holder: BookChViewHolder, position: Int) {
//        holder.title.setText(items[position].chtitle)
        holder.title.text = items[position].chtitle
        holder.index.text = position.toString()


        if (position == getThisPosition()) {
//            holder.chapterRow.setBackgroundColor(R.color.selectChBg)
            holder.chapterRow.setBackgroundResource(R.color.selectChBg)
        } else {
//            holder.chapterRow.setBackgroundResource(R.color.unselectChBg)
            holder.chapterRow.setBackgroundColor(Color.TRANSPARENT)
        }
        holder.doenloadChapter.setOnClickListener{
            listener?.onMoreClick(items[position],position,it)
        }

        savedChapterIndexes?.let {
                if(it.contains(ChapterIndex(position))){
                    holder.savedStat.setImageResource(R.drawable.chapter_saved_stat)
                }else{
                    holder.savedStat.setImageResource(R.drawable.chapter_saved_stat_unsaved)
                }
        }




//        holder.booktitle.setText(items.get(position).booktitle)
//        holder.bookUrl.setText(items.get(position).bookUrl)
    }

    override fun getItemCount(): Int = items.size

}