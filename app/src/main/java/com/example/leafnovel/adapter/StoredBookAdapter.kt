package com.example.leafnovel.adapter

import android.content.Context
import com.example.leafnovel.R

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.mybooksscreen.MyBooksViewModel
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.row_mybook.view.*
import kotlin.coroutines.coroutineContext

class StoredBookAdapter() : RecyclerView.Adapter<StoredBookAdapter.StoredBookViewHolder>() {
    private var items = emptyList<StoredBook>()
    private var listener: OnItemClickListener? = null
    private var context : Context? = null
    private var viewmodel : MyBooksViewModel? = null
    constructor(_mContext: Context) : this(){
        context = _mContext
    }


    fun setItems(books: List<StoredBook>, itemClickListener: OnItemClickListener) {
        this.items = books
        listener = itemClickListener
        notifyDataSetChanged()
    }

    fun setViewModel(viewModel : MyBooksViewModel) {
        this.viewmodel = viewModel
    }

    inner class StoredBookViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val author = view.MyBookAuthor
        val booktitle = view.MyBookName
        val bookLastRead = view.LastReadChapter
        val bookImg = view.MyBookImgView
        val moreBT = view.MoreBT
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tempbook = items[position]
//                listener?.onItemClick(tempbook)
                p0?.let { listener?.onItemClick(tempbook, it) }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(sbBook: StoredBook,view:View)
        fun onMoreClick(sbBook: StoredBook,view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredBookViewHolder {
        return StoredBookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_mybook, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
        holder.booktitle.setText(items[position].bookname)
        holder.author.setText(items[position].bookauthor)
        holder.bookLastRead.setText(items[position].lastread)
        Glide.with(holder.itemView).load("http:" + items[position].bookUrl)
            .placeholder(R.drawable.ic_outline_image_search_24)
            .error(R.drawable.ic_baseline_broken_image_24)
            .fallback(R.drawable.ic_baseline_image_24)
            .into(holder.bookImg)
        holder.moreBT.setOnClickListener{
            listener?.onMoreClick(items[position], it )
        }
    }


}