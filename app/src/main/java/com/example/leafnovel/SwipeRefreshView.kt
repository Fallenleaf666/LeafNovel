package com.example.leafnovel

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.SwipeRefreshView.xAdapter


/**
 * Author   : xiaoyu
 * Date     : 2018/10/11 下午3:14
 * Version  : v1.0.0 Describe : 下拉刷新

 * 1. down refresh 2. up load more
 *
 *
 * 使用方式： 1. 布局文件使用同原生RecyclerView <com.oy.wrapperrecyclerview.widget.xRecyclerView android:id="@+id/gank_recycler_view" android:layout_width="match_parent" android:layout_height="match_parent"></com.oy.wrapperrecyclerview.widget.xRecyclerView>
 *
 *
 * 2. Adapter需要继承自[xAdapter],同时实现3个方法：[xAdapter.getxItemCount],[ ][xAdapter.onCreatexViewHolder] 和[xAdapter.onBindxViewHolder]。
 *
 *
 * 3. 通过[.setListener]来监听刷新回调和加载更多回调。
 *
 *
 * 4. 通过[.stopRefreshing]或[.stopLoadingMore]来更新xRecyclerView的状态。
 *
 *
 * 5. 刷新列表数据调用[xAdapter.notifyDataSetChanged]
 *
 *
 * 问题 当首次加载数据不足以填满屏幕，此时'下拉刷新'也会触发'上拉加载更多'

 */
class SwipeRefreshView : SwipeRefreshLayout {
    private var mRecyclerView: RecyclerView? = null
    private var mListener: xAdapterListener? = null
    private var mXAdapter: xAdapter<*>? = null
    private var mState = STATE_IDLE

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    private fun initView() {
        mRecyclerView = RecyclerView(context)
        addView(mRecyclerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)
        setOnRefreshListener {

            // 只有处于IDLE状态时，才允许刷新
            if (mState != STATE_IDLE) {
                // 如果正在LoadMore，停止刷新动画
                if (mState == STATE_LOADINGMORE) isRefreshing = false
                return@setOnRefreshListener
            }
            // refresh监听为空，则停止刷新动画，不进行刷新动画
            if (mListener == null) {
                isRefreshing = false
                return@setOnRefreshListener
            }
            switchState(STATE_REFRESHING)
            mListener!!.startRefresh()
        }
    }

    fun setItemDecoration(@DrawableRes decorationRes: Int) {
        val drawable = ContextCompat.getDrawable(context, decorationRes)
        if (drawable != null) {
            val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            decoration.setDrawable(drawable)
            mRecyclerView!!.addItemDecoration(decoration)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }

    /** 设置adapter  */
    fun setAdapter(adapter: xAdapter<*>) {
        mXAdapter = adapter
        mRecyclerView!!.adapter = adapter
        mRecyclerView!!.addOnScrollListener(xScrollListener(mRecyclerView!!, adapter))
    }

    /** 设置'下拉刷新'和'上滑加载更多'的触发监听  */
    fun setListener(l: xAdapterListener?) {
        mListener = l
    }

    /** 手动切换至刷新状态，一般在进入页面首次加载前调用  */
    fun startRefreshing() {
        if (mState != STATE_IDLE) return
        switchState(STATE_REFRESHING)
    }

    /** 刷新完成后调用  */
    fun stopRefreshing() {
        if (mState != STATE_REFRESHING) return
        switchState(STATE_IDLE)
    }

    /** 加载更多完成后调用  */
    fun stopLoadingMore() {
        if (mState != STATE_LOADINGMORE) return
        switchState(STATE_IDLE)
    }

    // 切换状态，[STATE_IDLE, STATE_REFRESHING, STATE_LOADINGMORE]
    private fun switchState(newState: Int) {
        // out from old state时额外的操作
        if (mState == STATE_LOADINGMORE) {
            // 切换LoadMoreItem状态，并隐藏LoadMoreItem
            mXAdapter!!.changeLoadMoreState(false)
            mXAdapter!!.changeLoadMoreVisibility(false)
        } else if (mState == STATE_REFRESHING) {
            // 停止刷新动画
            isRefreshing = false
        }
        // into new state时额外的操作
        if (newState == STATE_REFRESHING) {
            isRefreshing = true
        } else if (newState == STATE_LOADINGMORE) {
            mXAdapter!!.changeLoadMoreState(true)
            mXAdapter!!.changeLoadMoreVisibility(true)
        }
        // 切换状态
        mState = newState
    }

    abstract class xAdapter<VH : RecyclerView.ViewHolder?> : RecyclerView.Adapter<VH>() {
        private var mLoadMoreItemHolder: LoadMoreViewHolder? = null
        override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): VH {
            if (itemType == ITEM_TYPE_LOADMORE) {
                if (mLoadMoreItemHolder == null) {
                    mLoadMoreItemHolder = createLoadMoreViewHolder(viewGroup)
                    if (mLoadMoreItemHolder == null) mLoadMoreItemHolder = LoadMoreViewHolder.defaultHolder(viewGroup)
                }
                return mLoadMoreItemHolder!!.getViewHolder()
            }
            return onCreatexViewHolder(viewGroup, itemType)
        }

        override fun onBindViewHolder(vh: VH, i: Int) {
            // 非'加载更多'item，进行View和数据的绑定
            if (i != itemCount - 1) {
                onBindxViewHolder(vh, i)
            }
        }

        override fun getItemCount(): Int {
            // item数量应该等于数据item加上底部加载提示item
            return getxItemCount() + 1
        }

        // 显示/隐藏LoadMoreView
        fun changeLoadMoreVisibility(show: Boolean) {
            if (mLoadMoreItemHolder == null) return
            mLoadMoreItemHolder!!.changeLoadMoreViewVisibility(show)
        }

        // 加载中状态/非加载状态
        fun changeLoadMoreState(loading: Boolean) {
            if (mLoadMoreItemHolder == null) return
            mLoadMoreItemHolder!!.changeLoadMoreViewState(loading)
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == itemCount - 1) {
                ITEM_TYPE_LOADMORE
            } else getxItemViewType(position)
        }

        /** 获取数据item数量，不包含加载提示item  */
        protected abstract fun getxItemCount(): Int

        /** 创建xViewHolder  */
        protected abstract fun onCreatexViewHolder(viewGroup: ViewGroup?, itemType: Int): VH

        /** 将item数据和xViewHolder绑定  */
        protected abstract fun onBindxViewHolder(holder: VH, position: Int)

        /** 获取item的类型  */
        protected fun getxItemViewType(position: Int): Int {
            return 0
        }

        /** 自定义LoadMoreView样式，不重写则使用默认样式  */
        protected fun createLoadMoreViewHolder(vg: ViewGroup?): LoadMoreViewHolder? {
            return null
        }

        companion object {
            private const val ITEM_TYPE_LOADMORE = 1 shl 12
        }
    }

    // LoadMoreView管理：切换LoadMoreItem显隐形、LoadMoreView加载中/非加载状态
    abstract class LoadMoreViewHolder protected constructor() {
        private val mVh: RecyclerView.ViewHolder
        protected var mLoadMoreView: View
        fun <VH> getViewHolder(): VH {
            return mVh as VH
        }

        // 展示/隐藏LoadMoreView
        fun changeLoadMoreViewVisibility(show: Boolean) {
            mVh.itemView.visibility = if (show) VISIBLE else INVISIBLE
        }

        /** 创建自定义的LoadMoreView  */
        abstract fun createLoadMoreView(): View

        /** 改变LoadMoreView状态：加载中/非加载  */
        abstract fun changeLoadMoreViewState(loading: Boolean)

        companion object {
            // 默认的LoadMoreView样式：一个ProgressBar和提示文字
            // 非加载状态时，只显示提示文字；加载中状态时只显示ProgressBar
            fun defaultHolder(vg: ViewGroup): LoadMoreViewHolder {
                return object : LoadMoreViewHolder() {
                    private var mProgress: View? = null
                    private var mTv: TextView? = null
                    override fun createLoadMoreView(): View {
                        // ViewGroup
                        val context = vg.context
                        val itemView = FrameLayout(context)
                        val displayMetrics = context.resources.displayMetrics
                        val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, displayMetrics)
                            .toInt()
                        val lp = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height)
                        itemView.layoutParams = lp
                        // ProgressBar
                        val progressSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, displayMetrics)
                            .toInt()
                        val plp = FrameLayout.LayoutParams(progressSize, progressSize)
                        mProgress = ProgressBar(context)
                        plp.gravity = Gravity.CENTER
                        itemView.addView(mProgress, plp)
                        // TextView
                        mTv = TextView(context)
                        mTv!!.text = "上拉加载更多"
                        mTv!!.setTextColor(Color.BLACK)
                        val tlp = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                        tlp.gravity = Gravity.CENTER
                        itemView.addView(mTv, tlp)
                        return itemView
                    }

                    override fun changeLoadMoreViewState(loading: Boolean) {
                        mProgress!!.visibility = if (loading) VISIBLE else GONE
                        mTv!!.visibility = if (loading) GONE else VISIBLE
                    }
                }
            }
        }

        init {
            mLoadMoreView = createLoadMoreView()
            val wrap = FrameLayout(mLoadMoreView.context)
            wrap.addView(mLoadMoreView)
            wrap.layoutParams = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            mVh = object : RecyclerView.ViewHolder(wrap) {}
            changeLoadMoreViewState(false)
            changeLoadMoreViewVisibility(false)
        }
    }

    private inner class xScrollListener internal constructor(recyclerView: RecyclerView, adapter: xAdapter<*>) :
        RecyclerView.OnScrollListener() {
        private val mAdapter: xAdapter<*>
        private val mManager: RecyclerView.LayoutManager?
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (mManager !is LinearLayoutManager) return
            // 状态改变成IDLE状态，判断是否需要处理LoadMoreView状态
            // LoadMoreView展示，但未完全展示时，滑动隐藏LoadMoreView
            // LoadMoreView完全展示时，触发加载更多
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val layoutManager = mManager
                val lastCompletePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val lastVisibleChild = layoutManager.findViewByPosition(lastVisibleItemPosition) ?: return

                // RecyclerView用来显示item的区域，未减去topPadding
                val rvInHeight = recyclerView.bottom - recyclerView.paddingBottom - recyclerView.top

                // refreshing 或 loading more 时不处理
                if (mState != STATE_IDLE) {
                    return
                }
                val child = layoutManager.findViewByPosition(lastCompletePosition) ?: return
                if (lastCompletePosition == mAdapter.itemCount - 2) {
                    val deltaY = rvInHeight - child.bottom
                    // 滑动至LoadMoreItem刚好隐藏，前提是有足够的下滑空间
                    val firstChild = recyclerView.getChildAt(0) ?: return
                    val available = firstChild.top - recyclerView.paddingTop
                    // deltaY是需要下滑的距离，available是可用距离
                    if (deltaY > 0) {
                        recyclerView.smoothScrollBy(0, -Math.min(deltaY, Math.abs(available)))
                    }
                } else if (lastCompletePosition == mAdapter.itemCount - 1) {
                    // 触发LoadMore操作前提，滑动list，LoadMoreView逐渐展示出来
                    // 判断：item是否把RecyclerView占满，如果尚未占满则没有必要展示LoadMoreView，数据量太小
                    // 当LoadMoreView完整展示时，如果第一个item也完整展示，则说明无法达到出发LoadMore操作条件
                    val firstCompletelyPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (firstCompletelyPosition == 0) {
                        return
                    }
                    if (mListener == null) {
                        return
                    }
                    switchState(STATE_LOADINGMORE)
                    mListener!!.startLoadMore()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (mManager !is LinearLayoutManager || mState != STATE_IDLE) return
            val newState = recyclerView.scrollState
            val linearLayoutManager = mManager
            val lastCompletePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
            val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                // LoadMoreView 展示:完整展示或部分展示
                if (lastVisibleItemPosition == mAdapter.itemCount - 1 || lastCompletePosition == mAdapter.itemCount - 1) {
                    if (mState == STATE_IDLE) {
                        mAdapter.changeLoadMoreVisibility(true)
                        mAdapter.changeLoadMoreState(false)
                    }
                }
            }
        }

        init {
            mManager = recyclerView.layoutManager
            mAdapter = adapter
        }
    }

    interface xAdapterListener {
        fun startRefresh()
        fun startLoadMore()
    }

    companion object {
        // 空闲，非加载状态
        private const val STATE_IDLE = 0

        // 刷新中
        private const val STATE_REFRESHING = 1

        // 加载更多中
        private const val STATE_LOADINGMORE = 2
    }
}