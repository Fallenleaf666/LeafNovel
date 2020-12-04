package com.example.leafnovel

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_bookchapter.view.*

class BookChAdapter():RecyclerView.Adapter<BookChAdapter.BookChViewHolder>(){
    private  val items = ArrayList<BookChapter>()
    private  var listener: OnItemClickListener?= null

    private var   thisPosition : Int ?= null
    fun getThisPosition(): Int?
    {return thisPosition}
//    fun setThisPosition(position: Int)
//    {this.thisPosition = position}

    fun setItems(bookChs:ArrayList<BookChapter>,itemClickListener:OnItemClickListener){
        items.clear()
        items.addAll(bookChs)
        listener = itemClickListener
        notifyDataSetChanged()
    }

//    fun reverseItems(){
//        items.reverse()
//        notifyDataSetChanged()
//    }

    inner class BookChViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        val title = view.ChapterTitle
        val chapterRow = view.ChapterRow
//        val booktitle=view.book_name
//        val bookUrl=view.other
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            val oldPosition = getThisPosition()
            if(position!= RecyclerView.NO_POSITION){
//            which you click will change color
                thisPosition = position
                val tempBookCh = items[position]
                listener?.onItemClick(tempBookCh)
                oldPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(position)
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
        if(position == getThisPosition()){
//            holder.chapterRow.setBackgroundColor(R.color.selectChBg)
            holder.chapterRow.setBackgroundResource(R.color.selectChBg)
        }else{
//            holder.chapterRow.setBackgroundResource(R.color.unselectChBg)
            holder.chapterRow.setBackgroundColor(Color.TRANSPARENT)
        }
//        holder.booktitle.setText(items.get(position).booktitle)
//        holder.bookUrl.setText(items.get(position).bookUrl)
    }

    override fun getItemCount(): Int = items.size

}