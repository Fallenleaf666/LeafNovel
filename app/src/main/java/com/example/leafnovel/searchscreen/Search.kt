package com.example.leafnovel.searchscreen

import NovelApi
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.*
import com.example.leafnovel.data.Repository
import com.example.leafnovel.mybooksscreen.MyBooksViewModel
import com.example.leafnovel.mybooksscreen.MyBooksViewModelFactory
import com.example.leafnovel.viewmodel.SearchBookViewModel
import com.example.leafnovel.viewmodel.SearchBookViewModelFactory
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Search : Fragment(),BookAdapter.OnItemClickListener {
    companion object{
        val newiInstance : Search by lazy{
            Search()
        }
    }
//    private lateinit var viewModel : MyBooksViewModel
    val bookAdapter = BookAdapter()
    private lateinit var viewModel : SearchBookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        SF_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }

        Log.d("Viewmodel","BEFORE")
        viewModel = ViewModelProvider(this,SearchBookViewModelFactory(context!!)).get(SearchBookViewModel::class.java)
        Log.d("Viewmodel","AFTER")
        viewModel.searchBooksResults.observe(viewLifecycleOwner, Observer {
                searchBooks ->
            bookAdapter.setItems(searchBooks,this)
        })




        SF_searchView.apply {
            setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.SearchBooks(query)
//                    CoroutineScope(Dispatchers.IO).launch {
//                        Log.d("TAG","BeforeRequest String: $query")
//                        val bookResults : BooksResults = NovelApi.RequestSearchNovel(query)
//                        Log.d("TAG","AfterRequest String : $query")
//                        launch(Dispatchers.Main) {
//                            Log.d("TAG","onCreate:${bookResults.size}")
//                            bookAdapter.setItems(bookResults, this@Search)
//                        }
//                    }
                    return false
                }
                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
        }
//        viewModel = ViewModelProvider(this).get(MyBooksViewModel::class.java)
//        viewModel.getStoredBooks().observe(this, Observer {
//            storedBooks ->
//            bookAdapter.setItems(storedBooks)
//        })
    }

    override fun onItemClick(book: Book) {
        Toast.makeText(context, "Item ${book.bookUrl} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_ID",book.bookId)
            putExtra("BOOK_TITLE",book.booktitle)
            putExtra("BOOK_AUTHOR",book.author)
            putExtra("BOOK_URL",book.bookUrl)
        }
        this.startActivity(intent)
    }
}