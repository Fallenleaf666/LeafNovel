package com.example.leafnovel.mybooksscreen

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var viewModel : MyBooksViewModel
    val storedBookAdapter = StoredBookAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_my_books,container,false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUIlister()
    }

    private fun initUIlister() {
        MyBookRefreshLayout.setOnRefreshListener(refreshListener)
    }

    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
//        myList.shuffle()
//        adapter.notifyDataSetChanged()
        Handler().postDelayed({MyBookRefreshLayout.isRefreshing = false},2000)
    }


    override fun onItemClick(sbBook: StoredBook) {
        Toast.makeText(context, "Item ${sbBook.bookid} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_ID",sbBook.bookid)
            putExtra("BOOK_TITLE",sbBook.bookname)
            putExtra("BOOK_AUTHOR",sbBook.bookauthor)
            putExtra("BOOK_URL",sbBook.bookid)
        }
        this.startActivity(intent)
    }

    private fun initUI() {
        SB_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = storedBookAdapter
        }

        Log.d("Viewmodel","BEFORE")
        viewModel = ViewModelProvider(this,MyBooksViewModelFactory(context!!)).get(MyBooksViewModel::class.java)
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