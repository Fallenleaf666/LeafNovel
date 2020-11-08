package com.example.leafnovel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_bookchapter.view.*

class BookChAdapter():RecyclerView.Adapter<BookChAdapter.BookChViewHolder>(){
    private  val items = ArrayList<BookChapter>()
    private  var listener: OnItemClickListener?= null

    fun setItems(bookChs:ArrayList<BookChapter>,itemClickListener:OnItemClickListener){
        items.clear()
        items.addAll(bookChs)
        listener = itemClickListener
        notifyDataSetChanged()
    }

    inner class BookChViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val title=view.ChapterTitle
//        val booktitle=view.book_name
//        val bookUrl=view.other
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position!= RecyclerView.NO_POSITION){
                val tempBookCh = items[position]
                listener?.onItemClick(tempBookCh)
            }
        }


    }

    interface OnItemClickListener{
        fun onItemClick(bookch:BookChapter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookChViewHolder {
        return BookChViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_bookchapter,parent,false))
    }

    override fun onBindViewHolder(holder: BookChViewHolder, position: Int) {
        holder.title.setText(items.get(position).chtitle)
//        holder.booktitle.setText(items.get(position).booktitle)
//        holder.bookUrl.setText(items.get(position).bookUrl)
    }

    override fun getItemCount(): Int = items.size

}