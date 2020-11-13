package com.example.leafnovel.adapter

import com.example.leafnovel.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.leafnovel.bookcase.StoredBook
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.row_mybook.view.*
import kotlin.coroutines.coroutineContext

class StoredBookAdapter : RecyclerView.Adapter<StoredBookAdapter.StoredBookViewHolder>() {
    private  var items = emptyList<StoredBook>()
    private  var listener:OnItemClickListener?= null

    fun setItems(books:List<StoredBook>, itemClickListener: OnItemClickListener){
        this.items = books
        listener = itemClickListener
        notifyDataSetChanged()
    }

    inner class StoredBookViewHolder(view: View):RecyclerView.ViewHolder(view),View.OnClickListener{
        val author=view.MyBookAuthor
        val booktitle=view.MyBookName
        val bookLastRead=view.LastReadChapter
        val bookImg=view.MyBookImgView
        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION){
                val tempbook = items[position]
                listener?.onItemClick(tempbook)
            }
        }

    }
    interface OnItemClickListener{
        fun onItemClick(sbBook: StoredBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredBookViewHolder {
        return StoredBookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_mybook,parent,false))
    }

    override fun getItemCount(): Int =items.size

    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
        holder.booktitle.setText(items.get(position).bookname)
        holder.author.setText(items.get(position).bookauthor)
        holder.bookLastRead.setText(items.get(position).lastread)
        Glide.with(holder.itemView).load("http:"+items.get(position).bookUrl)
            .placeholder(R.drawable.ic_outline_image_search_24)
            .error(R.drawable.ic_baseline_broken_image_24)
            .fallback(R.drawable.ic_baseline_image_24)
            .into(holder.bookImg)
    }

}