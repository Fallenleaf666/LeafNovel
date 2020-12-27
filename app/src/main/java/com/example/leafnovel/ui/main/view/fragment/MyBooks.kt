package com.example.leafnovel.ui.main.view.fragment

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.*
import com.example.leafnovel.ui.main.adapter.StoredBookAdapter
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.base.MyBooksViewModelFactory
import com.example.leafnovel.ui.main.view.BookDetailActivity
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import kotlinx.android.synthetic.main.fragment_my_books.*


class MyBooks : Fragment(), StoredBookAdapter.OnItemClickListener {
    companion object {
        val newInstance: MyBooks by lazy {
            MyBooks()
        }
    }

    lateinit var mLinearLayoutManager: LinearLayoutManager
    val hasMore = false
    var currentChapter = 0
    var isLoading = false
    var lastVisibleItem = 0


    private lateinit var viewModel: MyBooksViewModel
    private lateinit var storedBookAdapter: StoredBookAdapter
//    val storedBookAdapter = context?.let { StoredBookAdapter(it) }?:StoredBookAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_my_books, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storedBookAdapter = activity?.applicationContext?.let { StoredBookAdapter(it) } ?: StoredBookAdapter()
        initUI()
        initUiLister()
        loadingMore()
//        currentChapter = 1
    }

    private fun loadingMore() {
        SB_recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateNewChapter()
                }
            }
        })
    }

    private fun updateNewChapter() {
//        Handler().postDelayed({MyBookRefreshLayout.isRefreshing = false},2000)
    }

    private fun initUiLister() {
        MyBookRefreshLayout.setOnRefreshListener(refreshListener)

    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
//        myList.shuffle()
//        adapter.notifyDataSetChanged()
        Handler().postDelayed({ MyBookRefreshLayout.isRefreshing = false }, 2000)
    }


    override fun onItemClick(sbBook: StoredBook, view: View) {
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_IS_STORED", true)
            putExtra("BOOK_Detail", sbBook)
        }
        this.startActivity(intent)
    }

    override fun onMoreClick(sbBook: StoredBook, view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.mybook_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menuBookDelete -> viewModel.delete(sbBook)
            }
            true
        }
        popupMenu.show()
    }


    private fun initUI() {
        val decoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        SB_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = storedBookAdapter
            addItemDecoration(decoration)
        }

        Log.d("Viewmodel", "BEFORE")
        viewModel = ViewModelProvider(this, MyBooksViewModelFactory(context!!)).get(MyBooksViewModel::class.java)
        storedBookAdapter.setViewModel(viewModel)
        context?.let { Log.d("Viewmodel", "has context") }
        Log.d("Viewmodel", "AFTER")
        viewModel.allsbBooks.observe(viewLifecycleOwner, { storedBooks ->
            storedBookAdapter.setItems(storedBooks, this)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    inner class ItemDecoration :RecyclerView.ItemDecoration(){
//        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//            super.onDraw(c, parent, state)
//        }
//
//        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//            super.onDrawOver(c, parent, state)
//        }
//
//        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//            super.getItemOffsets(outRect, view, parent, state)
//        }
//    }

}