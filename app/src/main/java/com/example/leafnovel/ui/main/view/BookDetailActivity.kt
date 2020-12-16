package com.example.leafnovel.ui.main.view


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter

import com.bumptech.glide.Glide
import com.example.leafnovel.LeafFunction
import com.example.leafnovel.R
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.base.BookContentViewModelFactory
import com.example.leafnovel.ui.base.BookDetailViewModelFactory
import com.example.leafnovel.ui.main.adapter.BookDetailPageAdapter
import com.example.leafnovel.ui.main.viewmodel.BookContentViewModel
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {
    //    var bookId :String? = null
//    var booktitle :String? = null
//    var author  :String? = null
//    var bookUrl :String? = null
//    var bookDetail :StoredBook? = null
    private var isNetConnected = false
    private var repository: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        repository = StoredBookDB.getInstance(applicationContext)?.storedbookDao()?.let { Repository(it) }
//        isNetConnected = CheckNetConnect()
        isNetConnected = LeafFunction.CheckNetConnect(applicationContext)

        if (isNetConnected) {
            getIntentString()
            getBookDetail()
            initUi()
            setUiListener()
        } else {
//            no netView
//            LoadProgressBar.visibility = View.INVISIBLE
            Toast.makeText(this, "哎呀似乎還沒連線呢！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUi() {
        val pageAdapter = BookDetailPageAdapter(supportFragmentManager, lifecycle)
        bookDetailViewPager2.adapter = pageAdapter
        TabLayoutMediator(bookDetailTabLayout, bookDetailViewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "簡介"
                1 -> "目錄"
//                2 -> "書籤"
                else -> null
            }
        }.attach()
    }

    private fun getIntentString() {

        val bookDetail = intent.getParcelableExtra<StoredBook>("BOOK_Detail")
        if (bookDetail != null) {
            repository?.let {
                val viewModel = ViewModelProvider(
                    this,
                    BookDetailViewModelFactory(applicationContext, bookDetail!!, it)
                ).get(BookDetailViewModel::class.java)
            }
        } else {
            val bookId = intent.getStringExtra("BOOK_ID") ?: ""
            val booktitle = intent.getStringExtra("BOOK_TITLE") ?: ""
            val author = intent.getStringExtra("BOOK_AUTHOR") ?: ""
            val bookUrl = intent.getStringExtra("BOOK_URL") ?: ""
            val bookDetail = StoredBook(booktitle,author,"","",bookUrl,bookId)
            val viewModel = ViewModelProvider(
                this,
                BookDetailViewModelFactory(applicationContext, bookDetail, repository!!)
            ).get(BookDetailViewModel::class.java)
        }
    }

    private fun setUiListener() {
//        StoreBT.setOnClickListener{
//            if(booktitle!=null && author!=null && bookId!=null){
//                val storedbook = StoredBook(
//                booktitle!!,author!!,
//                "UU看書",
//                NewChapterText.text.toString(),
//                bookDetailMap["imgUrl"].toString(),
//                bookId!!)
//                CoroutineScope(Dispatchers.IO).launch {
//                repository?.insert(storedbook)
//            }
//            }
//        }
//
//        bookDitectoryBT.setOnClickListener{
//            if(bookId!=null){val intent = Intent(this@BookDetailActivity, BookDirectoryActivity::class.java).apply {
//                putExtra("BOOK_ID",bookId)
//                putExtra("BOOK_TITLE",booktitle)
//            }
//                startActivity(intent)}
//        }
    }

    private fun getBookDetail() {
//        CoroutineScope(Dispatchers.IO).launch {
////            bookDetailMap.putAll(com.example.leafnovel.data.api.NovelApi.RequestNovelDetail(bookId))
//            bookId?.let { bookDetailMap.putAll(NovelApi.RequestNovelDetail(it, booktitle!!)) }
//            println(bookDetailMap)
//            CoroutineScope(Dispatchers.Main).launch {
//                Book_titleView.text = booktitle
//                Book_authorView.text = author
//                NewChapterText.text = bookDetailMap["newChapter"]
//                UpdateTimeText.text = bookDetailMap["updateTime"]
//                Book_DescripeView.text = bookDetailMap["bookDescripe"]
//                val novelState = bookDetailMap["novelState"]
//                novelState?.let {
//                    Book_stateText.text =
//                        if(it.contains("載中")){"連載中"} else{"已完結"}
//                }
////                if (novelState != null) {
////                    Book_stateText.text =
////                        if(novelState.contains("載中")){"連載中"} else{"已完結"}
////                }
//
//                Glide.with(applicationContext).load("http:"+ bookDetailMap["imgUrl"])
//                    .placeholder(R.drawable.ic_outline_image_search_24)
//                    .error(R.drawable.ic_baseline_broken_image_24)
//                    .fallback(R.drawable.ic_baseline_image_24)
//                    .into(Book_imgView)
//                LoadProgressBar.visibility = View.INVISIBLE
//            }
//        }
    }
}