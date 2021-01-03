package com.example.leafnovel.ui.main.adapter

import android.content.Context
import android.os.Bundle
import com.example.leafnovel.R

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import com.rishabhharit.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.row_mybook.view.*

class StoredBookAdapter() : RecyclerView.Adapter<StoredBookAdapter.StoredBookViewHolder>() {
    private var items = emptyList<StoredBook>()
    private var listener: OnItemClickListener? = null
    private var context : Context? = null
    private var viewModel: MyBooksViewModel? = null
    private val viewBinderHelperRight: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }
    private val viewBinderHelperLeft: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }
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
        private val bookInfoView: FrameLayout = view.BookInfoView
        val swipeLayoutRight:SwipeRevealLayout = view.SwipeLayoutViewRight
        val swipeLayoutLeft:SwipeRevealLayout = view.SwipeLayoutViewLeft
        val author: TextView = view.MyBookAuthor
        val bookTitle: TextView = view.MyBookName
        val bookLastRead: TextView = view.LastReadChapter
        val bookNewChapter: TextView = view.NewChapter
        val bookImg: RoundedImageView = view.MyBookImgView
        val deleteBT: ImageButton = view.StoredBookDeleteBT
        val pinningBT: ImageButton = view.StoredSpecialBT
        init {
            bookInfoView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tempBook = items[position]
                p0?.let { listener?.onItemClick(tempBook, it) }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(sbBook: StoredBook, view:View)
        fun onDeleteClick(sbBook: StoredBook, view:View)
        fun onPinningClick(sbBook: StoredBook, view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredBookViewHolder {
        return StoredBookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_mybook, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
//        綁定讓swipemenu可以儲存開關狀態的唯一識別
        val sbBook: StoredBook = items[position]
        viewBinderHelperRight.bind(holder.swipeLayoutRight,sbBook.bookid)
        viewBinderHelperLeft.bind(holder.swipeLayoutLeft,sbBook.bookid)

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
        holder.deleteBT.setOnClickListener{
            holder.swipeLayoutRight.close(true)
            listener?.onDeleteClick(items[position], it )
        }
        holder.pinningBT.setOnClickListener{
            holder.swipeLayoutLeft.close(true)
            listener?.onPinningClick(items[position], it )
        }
        holder.swipeLayoutRight.setSwipeListener(object :SwipeRevealLayout.SwipeListener{
            override fun onClosed(view: SwipeRevealLayout?) {
                holder.swipeLayoutLeft.setLockDrag(false)
            }
            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                holder.swipeLayoutLeft.setLockDrag(true)
            }
            override fun onOpened(view: SwipeRevealLayout?) {}
        })

        holder.swipeLayoutLeft.setSwipeListener(object :SwipeRevealLayout.SwipeListener{
            override fun onClosed(view: SwipeRevealLayout?) {
                holder.swipeLayoutRight.setLockDrag(false)
            }
            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                holder.swipeLayoutRight.setLockDrag(true)
            }
            override fun onOpened(view: SwipeRevealLayout?) {}
        })
    }

    fun saveStates(outState:Bundle){
        viewBinderHelperRight.saveStates(outState)
        viewBinderHelperLeft.saveStates(outState)
    }
    fun restoreStates(outState:Bundle){
        viewBinderHelperRight.restoreStates(outState)
        viewBinderHelperLeft.restoreStates(outState)
    }
}


