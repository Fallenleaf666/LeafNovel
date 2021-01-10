package com.example.leafnovel.bean

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.leafnovel.R
import com.example.leafnovel.data.model.StoredBook
import com.rishabhharit.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.row_mybook.view.*
import kotlinx.android.synthetic.main.row_mybook_folder.view.*

class MyBookAdapter:ExpandableItemAdapter() {
    companion object{
        const val TYPE_GROUP = 0xfa01
        const val TYPE_CHILD = 0xfa02
    }

    private val viewBinderHelperRight: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }
    private val viewBinderHelperLeft: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    private var listener: OnItemClickListener? = null
    private var folderListener: OnFolderItemClickListener? = null

    fun setListener(itemClickListener:OnItemClickListener) {
        listener = itemClickListener
    }

    fun setListener(itemClickListener:OnItemClickListener,folderListener: OnFolderItemClickListener) {
        this.listener = itemClickListener
        this.folderListener = folderListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View
        var itemViewHolder: ItemViewHolder? = null
        when (viewType) {
            TYPE_GROUP -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_mybook_folder, parent, false)
                itemViewHolder = GroupViewHolder(view)
            }
            TYPE_CHILD -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_mybook, parent, false)
                itemViewHolder = ChildViewHolder(view)
            }
        }
        return itemViewHolder!!
    }


    override fun onBindViewHolder(bookVH: ItemViewHolder, position: Int) {
        val item: Item = getItem(position)
        when (getItemViewType(position)) {
            TYPE_GROUP -> {
                val folder = item as Group
                val folderVH = bookVH as GroupViewHolder
                folderVH.changeIcon(item.isExpendable)
                folderVH.title.text = folder.title
                folderVH.itemView.setOnClickListener{
                    toggle(folder)
                    bookVH.changeIcon(folder.isExpendable)
                    notifyDataSetChanged()
                }
                folderVH.more.setOnClickListener{
                    folderListener?.onMoreClick(folder,it,position)
                }
                folderVH.more.visibility = if(folder.id == -5)View.INVISIBLE else View.VISIBLE
            }
            TYPE_CHILD -> {
                val book = item as Child
                val bookVH = bookVH as ChildViewHolder
                viewBinderHelperRight.bind(bookVH.swipeLayoutRight, book.bookId)
                viewBinderHelperLeft.bind(bookVH.swipeLayoutLeft, book.bookId)
                bookVH.bookTitle.text = book.bookName
                bookVH.author.text = book.bookAuthor
                bookVH.bookLastRead.text = if(book.lastRead!="")book.lastRead else "尚未閱讀本書"
                bookVH.bookNewChapter.text = book.newChapter
                Glide.with(bookVH.itemView).load("http:${book.bookUrl}")
                    .placeholder(R.drawable.ic_outline_image_search_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .fallback(R.drawable.ic_baseline_image_24)
                    .centerInside()
                    .into(bookVH.bookImg)

                bookVH.deleteBT.setOnClickListener{
                    bookVH.swipeLayoutRight.close(true)
                    listener?.onDeleteClick(book, it)
                }
                bookVH.pinningBT.setOnClickListener{
                    bookVH.swipeLayoutLeft.close(true)
                    listener?.onPinningClick(book, it)
                }
                bookVH.swipeLayoutRight.setSwipeListener(object : SwipeRevealLayout.SwipeListener {
                    override fun onClosed(view: SwipeRevealLayout?) {
                        bookVH.swipeLayoutLeft.setLockDrag(false)
                    }

                    override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                        bookVH.swipeLayoutLeft.setLockDrag(true)
                    }

                    override fun onOpened(view: SwipeRevealLayout?) {}
                })

                bookVH.swipeLayoutLeft.setSwipeListener(object : SwipeRevealLayout.SwipeListener {
                    override fun onClosed(view: SwipeRevealLayout?) {
                        bookVH.swipeLayoutRight.setLockDrag(false)
                    }

                    override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                        bookVH.swipeLayoutRight.setLockDrag(true)
                    }

                    override fun onOpened(view: SwipeRevealLayout?) {}
                })
            }
        }

    }


    private inner class GroupViewHolder(itemView: View) : ItemViewHolder(itemView) {
        var title: TextView = itemView.BookFolderTitleText
        var icon: ImageView = itemView.BookFolderExpandableIcon
        var more: ImageButton = itemView.BookFolderMoewBT

        init {
        }

        override fun getType(): Int {
            return TYPE_GROUP
        }

        fun changeIcon(isExpandable:Boolean){
            if(isExpandable){
                icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }else{
                icon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_black_24)
            }
        }
    }

    private inner class ChildViewHolder(itemView: View) : ItemViewHolder(itemView),View.OnClickListener{
        private val bookInfoView: FrameLayout = itemView.BookInfoView
        val swipeLayoutRight: SwipeRevealLayout = itemView.SwipeLayoutViewRight
        val swipeLayoutLeft: SwipeRevealLayout = itemView.SwipeLayoutViewLeft
        val author: TextView = itemView.MyBookAuthor
        val bookTitle: TextView = itemView.MyBookName
        val bookLastRead: TextView = itemView.LastReadChapter
        val bookNewChapter: TextView = itemView.NewChapter
        val bookImg: RoundedImageView = itemView.MyBookImgView
        val deleteBT: ImageButton = itemView.StoredBookDeleteBT
        val pinningBT: ImageButton = itemView.StoredSpecialBT
        init {
            bookInfoView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tempBook = mItems[position] as Child
                if(tempBook.getType() == TYPE_CHILD)
                p0?.let { listener?.onItemClick(tempBook, it) }
            }
        }
        override fun getType(): Int {
            return TYPE_CHILD
        }
    }

    interface OnItemClickListener {
        fun onItemClick(sbBook: Child, view: View)
        fun onDeleteClick(book:Child, view: View)
        fun onPinningClick(sbbook:Child, view: View)
    }

    interface OnFolderItemClickListener {
        fun onMoreClick(bookFolder: Group, view: View,position:Int)
    }

    fun saveStates(outState: Bundle){
        viewBinderHelperRight.saveStates(outState)
        viewBinderHelperLeft.saveStates(outState)
    }
    fun restoreStates(outState: Bundle){
        viewBinderHelperRight.restoreStates(outState)
        viewBinderHelperLeft.restoreStates(outState)
    }

}