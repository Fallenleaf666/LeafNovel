package com.example.leafnovel.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.R
import com.example.leafnovel.ui.main.adapter.BookAdapter.BookViewHolder
import com.facebook.shimmer.Shimmer
import kotlinx.android.synthetic.main.row_book.view.*

class BookAdapter() : RecyclerView.Adapter<BookViewHolder>() {
    private val items = ArrayList<Book>()
    private var listener: OnItemClickListener? = null
    private var shimmerNum: Int = 5
    var isShimmer: IsShimmer = IsShimmer.WAIT


    //    private  val listener : OnItemClickListener
//    fun setItems(books:ArrayList<Book>){
//        items.clear()
//        items.addAll(books)
//        notifyDataSetChanged()
//    }
    fun setItems(books: List<Book>?, itemClickListener: OnItemClickListener?) {
        items.clear()
        books?.let {
            items.addAll(books)
        }
        itemClickListener?.let {
            listener = it
        }
        notifyDataSetChanged()
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val author = view.BookAuthor
        val booktitle = view.BookName
        val bookShimmerLayout = view.BookShimmerLayout
        val bookDescripe = view.BookDescripe
//        val updateTime=view.update_time
//        val bookUrl=view.update_time

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && isShimmer == IsShimmer.STOP) {
                val tempbook = items[position]
                listener?.onItemClick(tempbook)
            }
        }
        fun cancelShimmerBackground() {
            bookShimmerLayout.stopShimmer()
            bookShimmerLayout.setShimmer(null)
            author.background = null
            booktitle.background = null
            bookDescripe.background = null
        }

        fun setShimmerView() {
            booktitle.text = getSpaceString((9..20).random())
            author.text =  getSpaceString((4..10).random())
            bookDescripe.text = getSpaceRow((0..2).random())
            author.setBackgroundResource(R.color.color_shimmer)
            booktitle.setBackgroundResource(R.color.color_shimmer)
            bookDescripe.setBackgroundResource(R.color.color_shimmer)
            bookShimmerLayout.setShimmer(Shimmer.AlphaHighlightBuilder().build())
            bookShimmerLayout.startShimmer()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(book: Book)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_book, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        when (isShimmer) {
            IsShimmer.ACT -> {
                holder.setShimmerView()
            }
            IsShimmer.WAIT -> {

            }
            IsShimmer.STOP -> {
                holder.cancelShimmerBackground()
                holder.author.text = items[position].author
                holder.booktitle.text = items[position].booktitle
                holder.bookDescripe.text = items[position].bookDescripe
//        holder.bookUrl.setText(items.get(position).bookUrl)
//        holder.updateTime.setText(items.get(position).updateTime)
            }
        }
    }

    //    override fun getItemCount(): Int = if(isShimmer)shimmerNum else items.size
    override fun getItemCount(): Int = when (isShimmer) {
        IsShimmer.ACT -> {
            shimmerNum
        }
        IsShimmer.WAIT -> {
            0
        }
        IsShimmer.STOP -> {
            items.size
        }
    }

    enum class IsShimmer {
        ACT, STOP, WAIT
    }

    fun getSpaceString(num:Int):String{
        var s = ""
        for(i in 0 .. num){
            s += " "
        }
        return s
    }

    fun getSpaceRow(num:Int):String{
        var s = "\n\n"
        for(i in 0 until num){
            s += "\n"
        }
        return s
    }
}


