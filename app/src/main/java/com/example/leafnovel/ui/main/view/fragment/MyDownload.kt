package com.example.leafnovel.ui.main.view.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.leafnovel.*
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.adapter.StoredBookAdapter
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.ui.base.MyBooksViewModelFactory
import com.example.leafnovel.ui.main.adapter.MyDownloadAdapter
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel
import com.example.leafnovel.ui.main.viewmodel.MyDownloadViewModel
import kotlinx.android.synthetic.main.fragment_my_download.*


class MyDownload : Fragment(), MyDownloadAdapter.OnItemClickListener {
    companion object {
        val newInstance: MyDownload by lazy {
            MyDownload()
        }
    }

//    lateinit var mLinearLayoutManager: LinearLayoutManager
//    val hasMore = false
//    var currentChapter = 0
//    var isLoading = false
//    var lastVisibleItem = 0

    private lateinit var viewModel: MyDownloadViewModel
    private lateinit var myDownloadAdapter: MyDownloadAdapter
//    val storedBookAdapter = context?.let { StoredBookAdapter(it) }?:StoredBookAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_my_download, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        myDownloadAdapter = activity?.applicationContext?.let { MyDownloadAdapter(it) } ?: MyDownloadAdapter()
        initUI()
        initUiLister()
        loadingMore()
//        currentChapter = 1
    }

    private fun loadingMore() {
        MyDownload_RefreshLayout
    }

    private fun updateNewChapter() {
//        Handler().postDelayed({MyBookRefreshLayout.isRefreshing = false},2000)
    }

    private fun initUiLister() {

    }

    private fun initUI() {
        val decoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        MyDownload_Recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = myDownloadAdapter
            addItemDecoration(decoration)
        }

//        viewModel = ViewModelProvider(this, MyBooksViewModelFactory(context!!)).get(MyBooksViewModel::class.java)
//        storedBookAdapter.setViewModel(viewModel)
//        context?.let { Log.d("Viewmodel", "has context") }
//        viewModel.allsbBooks.observe(viewLifecycleOwner, { storedBooks ->
//            storedBookAdapter.setItems(storedBooks, this)
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onItemClick(bookCh: BookChapter, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onMoreClick(bookCh: BookChapter, position: Int, view: View) {
        TODO("Not yet implemented")
    }
}