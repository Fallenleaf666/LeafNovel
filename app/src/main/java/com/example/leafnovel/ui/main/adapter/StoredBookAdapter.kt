package com.example.leafnovel.ui.main.adapter

import android.content.Context
import com.example.leafnovel.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import com.rishabhharit.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.row_mybook.view.*

class StoredBookAdapter() : RecyclerView.Adapter<StoredBookAdapter.StoredBookViewHolder>() {
    private var items = emptyList<StoredBook>()
    private var listener: OnItemClickListener? = null
    private var context : Context? = null
    private var viewModel: MyBooksViewModel? = null
    constructor(_mContext: Context) : this(){
        context = _mContext
    }


    fun setItems(books: List<StoredBook>, itemClickListener: OnItemClickListener) {
        this.items = books
        listener = itemClickListener
        notifyDataSetChanged()
    }

    fun setViewModel(viewModel : MyBooksViewModel) {
        this.viewModel = viewModel
    }

    inner class StoredBookViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val author: TextView = view.MyBookAuthor
        val bookTitle: TextView = view.MyBookName
        val bookLastRead: TextView = view.LastReadChapter
        val bookNewChapter: TextView = view.NewChapter
        val bookImg: RoundedImageView = view.MyBookImgView
        val moreBT: ImageButton = view.MoreBT
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tempBook = items[position]
//                listener?.onItemClick(tempBook)
                p0?.let { listener?.onItemClick(tempBook, it) }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(sbBook: StoredBook, view:View)
        fun onMoreClick(sbBook: StoredBook, view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredBookViewHolder {
        return StoredBookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_mybook, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
        holder.bookTitle.text = items[position].bookname
        holder.author.text = items[position].bookauthor
        holder.bookLastRead.text = if(items[position].lastread!="")items[position].lastread else "尚未閱讀本書"
        holder.bookNewChapter.text = items[position].newchapter
        Glide.with(holder.itemView).load("http:" + items[position].bookUrl)
            .placeholder(R.drawable.ic_outline_image_search_24)
            .error(R.drawable.ic_baseline_broken_image_24)
            .fallback(R.drawable.ic_baseline_image_24)
            .centerInside()
            .into(holder.bookImg)
        holder.moreBT.setOnClickListener{
            listener?.onMoreClick(items[position], it )
        }
    }
}