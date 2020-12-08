package com.example.leafnovel.mybooksscreen

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.*
import com.example.leafnovel.adapter.StoredBookAdapter
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.bookcase.StoredBookDB
import kotlinx.android.synthetic.main.fragment_my_books.*
import kotlinx.coroutines.CoroutineScope


class MyBooks : Fragment() ,StoredBookAdapter.OnItemClickListener{
    companion object{
        val newiInstance : MyBooks by lazy{
            MyBooks()
        }
    }

    lateinit var mLinearLayoutManager :LinearLayoutManager
    val hasMore = false
    var currentChapter = 0
    var isLoading = false
    var lastVisibleItem = 0

    private lateinit var viewModel : MyBooksViewModel
    private lateinit var storedBookAdapter :StoredBookAdapter
//    val storedBookAdapter = context?.let { StoredBookAdapter(it) }?:StoredBookAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_my_books,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storedBookAdapter =  activity?.applicationContext?.let { StoredBookAdapter(it) } ?:StoredBookAdapter()
        initUI()
        initUIlister()
        loadingMore()
//        currentChapter = 1
    }


    private fun loadingMore() {
        SB_recycler.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        updateNewChapter()
                    }
            }
        })
    }

    private fun updateNewChapter() {
//        Handler().postDelayed({MyBookRefreshLayout.isRefreshing = false},2000)
    }

    private fun initUIlister() {
        MyBookRefreshLayout.setOnRefreshListener(refreshListener)


    }

    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
//        myList.shuffle()
//        adapter.notifyDataSetChanged()
        Handler().postDelayed({MyBookRefreshLayout.isRefreshing = false},2000)
    }


    override fun onItemClick(sbBook: StoredBook,view: View) {
//        Toast.makeText(context, "Item ${sbBook.bookid} clicked", Toast.LENGTH_SHORT).show()
//        Toast.makeText(context, "Id =  ${view.id} clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, BookDetailActivity::class.java).apply {
                    putExtra("BOOK_ID",sbBook.bookid)
                    putExtra("BOOK_TITLE",sbBook.bookname)
                    putExtra("BOOK_AUTHOR",sbBook.bookauthor)
                    putExtra("BOOK_URL",sbBook.bookid)
                }
                this.startActivity(intent)
    }

    override fun onMoreClick(sbBook: StoredBook, view: View) {
        val popupMenu = PopupMenu(context,view)
        popupMenu.inflate(R.menu.mybook_more_menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when(item.itemId){
                R.id.menuBookDelete-> viewModel.delete(sbBook)
            }
            true
        }
        popupMenu.show()
    }


    private fun initUI() {
        SB_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = storedBookAdapter
        }

        Log.d("Viewmodel","BEFORE")
        viewModel = ViewModelProvider(this,MyBooksViewModelFactory(context!!)).get(MyBooksViewModel::class.java)
        storedBookAdapter.setViewModel(viewModel)
        context?.let { Log.d("Viewmodel","has context") }
        Log.d("Viewmodel","AFTER")
        viewModel.allsbBooks.observe(viewLifecycleOwner, Observer {
                storedBooks ->
            storedBookAdapter.setItems(storedBooks,this)
        })
//        viewModel = ViewModelProvider(this).get(MyBooksViewModel::class.java)
//        viewModel.getStoredBooks().observe(this, Observer {
//            storedBooks ->
//            bookAdapter.setItems(storedBooks)
//        })

    }

}