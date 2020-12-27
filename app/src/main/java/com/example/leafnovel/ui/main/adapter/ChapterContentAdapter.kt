package com.example.leafnovel.ui.main.adapter

import android.content.Context
import com.example.leafnovel.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.ChapterContentBeta
import com.example.leafnovel.ui.main.viewmodel.BookContentBetaViewModel
import kotlinx.android.synthetic.main.activity_book_content.*
import kotlinx.android.synthetic.main.row_chapter_content.view.*

class ChapterContentAdapter() : RecyclerView.Adapter<ChapterContentAdapter.ChapterContentAdapterViewHolder>() {
    private var items = arrayListOf<ChapterContentBeta>()
    private var fontSize = 16f
    private var isUiModeNight = false
    private var listener: OnItemClickListener? = null
    private var context : Context? = null
    constructor(_mContext: Context) : this(){
        context = _mContext
    }

    fun setItems(chapterContent: ChapterContentBeta, itemClickListener: OnItemClickListener) {
        items.clear()
        items.add(chapterContent)
        listener = itemClickListener
        notifyDataSetChanged()
    }
    fun addItem(chapterContent:ChapterContentBeta, itemClickListener: OnItemClickListener) {
        items.add(chapterContent)
        listener = itemClickListener
        notifyItemInserted(items.size-1)
    }

    fun getChapterTitleByPosition(chapterPosition:Int):String = items[chapterPosition].chTitle

    inner class ChapterContentAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val chapterTitle: TextView = view.row_chapter_title
        val chapterContent: TextView = view.row_chapter_content
        val chapterContentView: ConstraintLayout = view.row_chapter_content_view
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                val tempBook = items[position]
//                p0?.let { listener?.onItemClick(tempBook, it) }
//            }
        }
        fun setFontSize(fontSize:Float) {
            chapterTitle.textSize = fontSize
            chapterContent.textSize = fontSize + 4
        }
        fun setUiByDayNightMode(isUiModeNight:Boolean) {
            if (isUiModeNight) {
                context?.let {
                chapterTitle.setTextColor(ContextCompat.getColor(it, R.color.fontNight))
                chapterContent.setTextColor(ContextCompat.getColor(it, R.color.fontNight))
                }
            } else {
                context?.let {
                chapterTitle.setTextColor(ContextCompat.getColor(it, R.color.fontMorning))
                chapterContent.setTextColor(ContextCompat.getColor(it, R.color.fontMorning))
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onScreenClick(chapterContent:ChapterContentBeta, view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterContentAdapterViewHolder {
        return ChapterContentAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_chapter_content, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChapterContentAdapterViewHolder, position: Int) {
        holder.chapterTitle.text = items[position].chTitle
        holder.chapterContent.text = items[position].chContent
        holder.chapterContentView.setOnClickListener{
            listener?.onScreenClick(items[position], it )
        }
        holder.setFontSize(fontSize)
        holder.setUiByDayNightMode(isUiModeNight)
    }
    fun setFontSize(fontSize:Float) {
        this.fontSize = fontSize
        notifyDataSetChanged()
    }
    fun setUiByDayNightMode(isUiModeNight:Boolean) {
        this.isUiModeNight = isUiModeNight
        notifyDataSetChanged()
    }


    fun refresh(){
//        TODO 刷新
    }
}