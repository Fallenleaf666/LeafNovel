package com.example.leafnovel.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.R
import com.example.leafnovel.ui.main.adapter.BookAdapter.BookViewHolder
import kotlinx.android.synthetic.main.row_book.view.*

class BookAdapter():RecyclerView.Adapter<BookViewHolder>() {
    private  val items = ArrayList<Book>()
    private  var listener: OnItemClickListener?= null
//    private  val listener : OnItemClickListener
//    fun setItems(books:ArrayList<Book>){
//        items.clear()
//        items.addAll(books)
//        notifyDataSetChanged()
//    }
fun setItems(books:List<Book>?, itemClickListener: OnItemClickListener){
    items.clear()
    if (books != null) {
        items.addAll(books)
    }
    listener = itemClickListener
    notifyDataSetChanged()
}



    inner class BookViewHolder(view: View):RecyclerView.ViewHolder(view),View.OnClickListener{
        val author=view.BookAuthor
        val booktitle=view.BookName
//        val updateTime=view.update_time
//        val bookUrl=view.update_time
        val bookDescripe=view.BookDescripe
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
        fun onItemClick(book: Book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_book,parent,false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.author.setText(items.get(position).author)
        holder.booktitle.setText(items.get(position).booktitle)
//        holder.bookUrl.setText(items.get(position).bookUrl)
//        holder.updateTime.setText(items.get(position).updateTime)
        holder.bookDescripe.setText(items.get(position).bookDescripe)
    }

    override fun getItemCount(): Int =items.size

}


