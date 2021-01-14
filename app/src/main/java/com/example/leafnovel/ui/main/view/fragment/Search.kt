package com.example.leafnovel.ui.main.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leafnovel.*
import com.example.leafnovel.ui.main.adapter.BookAdapter
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.ui.main.viewmodel.SearchBookViewModel
import com.example.leafnovel.ui.base.SearchBookViewModelFactory
import com.example.leafnovel.ui.main.view.BookDetailActivity
import kotlinx.android.synthetic.main.fragment_search.*

class Search : Fragment(), BookAdapter.OnItemClickListener {
    companion object {
        val newInstance: Search by lazy {
            Search()
        }
    }

    val bookAdapter = BookAdapter()
    private lateinit var viewModel: SearchBookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        val view = inflater.inflate(R.layout.fragment_search, container, false)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initUiListener()
    }

    private fun initUiListener() {
        SF_searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.searchBooks(query)
                    SF_searchView.clearFocus()
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            })
//            setOnFocusChangeListener{
//                    _, isFocus->
//                if(isFocus){
//                    SearchCancelBT.setTextColor(ContextCompat.getColor(context, R.color.searchOnFocus))
//                }else{
//                    SearchCancelBT.setTextColor(ContextCompat.getColor(context, R.color.searchNoFocus))
//                }
//            }

            setOnQueryTextFocusChangeListener{
                    _, isFocus->
                if(isFocus){
                    SearchCancelBT.setTextColor(ContextCompat.getColor(context, R.color.searchOnFocus))
                }else{
                    SearchCancelBT.setTextColor(ContextCompat.getColor(context, R.color.searchNoFocus))
                }
            }
        }

        SearchCancelBT.setOnClickListener{
            SF_searchView.clearFocus()
        }
    }

    private fun initUI() {

        SF_recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = bookAdapter
        }
        context?.let {
            viewModel = ViewModelProvider(this, SearchBookViewModelFactory(it)).get(SearchBookViewModel::class.java)
            viewModel.searchBooksResults.observe(viewLifecycleOwner, { searchBooks ->
                bookAdapter.setItems(searchBooks, this)
            })
        }

    }

    override fun onItemClick(book: Book) {
        val intent = Intent(context, BookDetailActivity::class.java).apply {
            putExtra("BOOK_IS_STORED", false)
            putExtra("BOOK_INFO", book)
        }
        this.startActivity(intent)
    }
}