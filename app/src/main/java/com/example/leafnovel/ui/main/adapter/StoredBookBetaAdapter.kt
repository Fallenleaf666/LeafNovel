package com.example.leafnovel.ui.main.adapter

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.leafnovel.R
import com.example.leafnovel.bean.MyBookFirstBean
import com.example.leafnovel.bean.MyBookSecondBean
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import com.rishabhharit.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.row_mybook.view.*


class StoredBookBetaAdapter() : RecyclerView.Adapter<StoredBookBetaAdapter.StoredBookViewHolder>() {
    //分類資料夾
    private val firstBeanSparseArray: SparseArray<MyBookFirstBean> = SparseArray<MyBookFirstBean>()
    //書本
    private val secondBeanSparseArray: SparseArray<MyBookSecondBean> = SparseArray<MyBookSecondBean>()

    private var items = emptyList<StoredBook>()
    private var listener: OnItemClickListener? = null
    private var context: Context? = null
    private var viewModel: MyBooksViewModel? = null
    private val viewBinderHelperRight: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }
    private val viewBinderHelperLeft: ViewBinderHelper = ViewBinderHelper().apply {
        setOpenOnlyOne(true)
    }

    constructor(_mContext: Context) : this() {
        context = _mContext
    }
    fun putList(list:List<MyBookFirstBean> ){
        for (i in list.indices) {
            firstBeanSparseArray.put(i, list[i])
        }
    }
    fun setItems(books: List<StoredBook>, itemClickListener: OnItemClickListener) {
        this.items = books
        listener = itemClickListener
        notifyDataSetChanged()
    }

    fun setViewModel(viewModel: MyBooksViewModel) {
        this.viewModel = viewModel
    }

    inner class StoredBookViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val bookInfoView: FrameLayout = view.BookInfoView
        val swipeLayoutRight: SwipeRevealLayout = view.SwipeLayoutViewRight
        val swipeLayoutLeft: SwipeRevealLayout = view.SwipeLayoutViewLeft
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
        fun onItemClick(sbBook: StoredBook, view: View)
        fun onDeleteClick(sbBook: StoredBook, view: View)
        fun onPinningClick(sbBook: StoredBook, view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoredBookViewHolder {
        when (viewType) {
            TYPE_FIRST -> return StoredBookViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_mybook_folder, parent, false)
            )
            TYPE_SECOND -> return StoredBookViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_mybook, parent, false)
            )
        }
        return StoredBookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_mybook, parent, false))
    }

    override fun getItemCount(): Int = firstBeanSparseArray.size() + secondBeanSparseArray.size()

//    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
////        綁定讓swipemenu可以儲存開關狀態的唯一識別
//        val sbBook: StoredBook = items[position]
//        viewBinderHelperRight.bind(holder.swipeLayoutRight, sbBook.bookid)
//        viewBinderHelperLeft.bind(holder.swipeLayoutLeft, sbBook.bookid)
//
//        holder.bookTitle.text = items[position].bookname
//        holder.author.text = items[position].bookauthor
//        holder.bookLastRead.text = if(items[position].lastread!="")items[position].lastread else "尚未閱讀本書"
//        holder.bookNewChapter.text = items[position].newchapter
//        Glide.with(holder.itemView).load("http:" + items[position].bookUrl)
//            .placeholder(R.drawable.ic_outline_image_search_24)
//            .error(R.drawable.ic_baseline_broken_image_24)
//            .fallback(R.drawable.ic_baseline_image_24)
//            .centerInside()
//            .into(holder.bookImg)
//        holder.deleteBT.setOnClickListener{
//            holder.swipeLayoutRight.close(true)
//            listener?.onDeleteClick(items[position], it)
//        }
//        holder.pinningBT.setOnClickListener{
//            holder.swipeLayoutLeft.close(true)
//            listener?.onPinningClick(items[position], it)
//        }
//        holder.swipeLayoutRight.setSwipeListener(object : SwipeRevealLayout.SwipeListener {
//            override fun onClosed(view: SwipeRevealLayout?) {
//                holder.swipeLayoutLeft.setLockDrag(false)
//            }
//
//            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
//                holder.swipeLayoutLeft.setLockDrag(true)
//            }
//
//            override fun onOpened(view: SwipeRevealLayout?) {}
//        })
//
//        holder.swipeLayoutLeft.setSwipeListener(object : SwipeRevealLayout.SwipeListener {
//            override fun onClosed(view: SwipeRevealLayout?) {
//                holder.swipeLayoutRight.setLockDrag(false)
//            }
//
//            override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
//                holder.swipeLayoutRight.setLockDrag(true)
//            }
//
//            override fun onOpened(view: SwipeRevealLayout?) {}
//        })
//    }

    fun saveStates(outState: Bundle){
        viewBinderHelperRight.saveStates(outState)
        viewBinderHelperLeft.saveStates(outState)
    }
    fun restoreStates(outState: Bundle){
        viewBinderHelperRight.restoreStates(outState)
        viewBinderHelperLeft.restoreStates(outState)
    }

    override fun getItemViewType(position: Int): Int {
        if (secondBeanSparseArray.get(position) != null) {
            return TYPE_SECOND;
        }
        return TYPE_FIRST;
    }

    private inner class FirstViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //item点击事件监听
            itemView.setOnClickListener {
                //若為本來是開啟則關閉擴展
                if (firstBeanSparseArray[layoutPosition].isExpand) {
                    firstBeanSparseArray[layoutPosition].isExpand = false
                    //取得該資料夾下的書本數
                    val addedSubNum: Int = firstBeanSparseArray[layoutPosition].addedSubNum
                    //移除資料夾下的書本viewItem
                    removeItems(layoutPosition, addedSubNum)
                    notifyItemRangeRemoved(layoutPosition + 1, addedSubNum)
                } else {
                    //若為本來是關閉則開啟擴展
                    firstBeanSparseArray[layoutPosition].isExpand = true
                    //u8r 加載取得該資料夾下的書本
                    val list: MutableList<MyBookSecondBean> = ArrayList()
//                    TODO 改
                    for (i in 0..4) {
                        list.add(MyBookSecondBean())
                    }
                    val addedSubNum = setEachFlows(layoutPosition, list)
                    //將書本viewItem加入
                    firstBeanSparseArray[layoutPosition].addedSubNum = addedSubNum
                    notifyItemRangeInserted(layoutPosition + 1, addedSubNum)
                }
            }
        }
    }

    private inner class SecondViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


    }

    /**
     * 點擊展開時加載
     * @param parentPosition
     * @param list
     * @return
     */
    fun setEachFlows(parentPosition: Int, list: List<MyBookSecondBean>): Int {

        //更新position大于当前点击的position的第一级布局的item的position
        for (i in itemCount - 1 downTo parentPosition + 1) {
            val index = firstBeanSparseArray.indexOfKey(i)
            if (index < 0) {
                continue
            }
            val dailyFlow: MyBookFirstBean? = firstBeanSparseArray.valueAt(index)
            firstBeanSparseArray.removeAt(index)
            firstBeanSparseArray.put(list.size + i, dailyFlow)
        }
        //更新position大于当前点击的position的第二级布局的item的position
        for (i in itemCount - 1 downTo parentPosition + 1) {
            val index = secondBeanSparseArray.indexOfKey(i)
            if (index < 0) {
                continue
            }
            val eachFlow: MyBookSecondBean? = secondBeanSparseArray.valueAt(index)
            secondBeanSparseArray.removeAt(index)
            secondBeanSparseArray.append(list.size + i, eachFlow)
        }
        //把获取到的数据根据相应的position放入SparseArray中。
        for (i in list.indices) {
            secondBeanSparseArray.put(parentPosition + i + 1, list[i])
        }
        return list.size
    }

    /**
     * 点击折叠时移除相应数据
     * @param clickPosition
     * @param addedSubNum
     */
    private fun removeItems(clickPosition: Int, addedSubNum: Int) {
        //更新position大于当前点击的position的第一级布局item的position
        val temp: SparseArray<MyBookFirstBean?> = SparseArray()
        for (i in itemCount - 1 downTo clickPosition + addedSubNum + 1) {
            val index = firstBeanSparseArray.indexOfKey(i)
            if (index < 0) {
                continue
            }
            val dailyFlow: MyBookFirstBean? = firstBeanSparseArray.valueAt(index)
            firstBeanSparseArray.removeAt(index)
            temp.put(i - addedSubNum, dailyFlow)
        }
        for (i in 0 until temp.size()) {
            val key = temp.keyAt(i)
            firstBeanSparseArray.put(key, temp[key])
        }
        //更新position大于当前点击的position的第二级布局item的position
        val temp2: SparseArray<MyBookSecondBean?> = SparseArray()
        for (i in itemCount - 1 downTo clickPosition + addedSubNum + 1) {
            val index = secondBeanSparseArray.indexOfKey(i)
            if (index < 0) {
                continue
            }
            val eachFlow: MyBookSecondBean? = secondBeanSparseArray.valueAt(index)
            secondBeanSparseArray.removeAt(index)
            temp2.put(i - addedSubNum, eachFlow)
        }
        for (i in 1..addedSubNum) {
            //移除被折叠的第二级布局数据
            secondBeanSparseArray.remove(clickPosition + i)
        }
        for (i in 0 until temp2.size()) {
            val key = temp2.keyAt(i)
            secondBeanSparseArray.put(key, temp2[key])
        }
    }

    companion object {
        private const val TYPE_FIRST = 0
        private const val TYPE_SECOND = 1
    }

    override fun onBindViewHolder(holder: StoredBookViewHolder, position: Int) {
    }
}


