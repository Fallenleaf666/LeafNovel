package com.example.leafnovel.ui.main.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.example.leafnovel.R
import com.example.leafnovel.checkNetConnect
import com.example.leafnovel.customToast
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.base.BookDetailViewModelFactory
import com.example.leafnovel.ui.main.adapter.BookDetailPageAdapter
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.fragment_book_directory.*


class BookDetailActivity : AppCompatActivity() {
    private var isNetConnected = false
    private var repository: Repository? = null

    var viewModel :BookDetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        repository = StoredBookDB.getInstance(applicationContext)?.storedbookDao()?.let { Repository(it) }
//        isNetConnected = CheckNetConnect()
        isNetConnected = checkNetConnect(applicationContext)

        if (isNetConnected) {
            getIntentString()
            getBookDetail()
            initUi()
            setActbar()
            setUiListener()
        } else {
//            no netView
//            LoadProgressBar.visibility = View.INVISIBLE
//            Toast.makeText(this, "哎呀似乎還沒連線呢！", Toast.LENGTH_SHORT).show()
            customToast(this as Activity, "哎呀似乎還沒連線呢！").show()
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
        bookDetailTabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab?.position == 0){
//                    MutiSelectBT.visibility = View.INVISIBLE
                    OrderSwitch.visibility = View.GONE
                }else if(tab?.position == 1){
//                    MutiSelectBT.visibility = View.VISIBLE
                    OrderSwitch.visibility = View.VISIBLE
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun getIntentString() {
        val bookIsStored = intent.getBooleanExtra("BOOK_IS_STORED", false)
        var bookDetail: StoredBook? = null
        if (bookIsStored) {
            bookDetail = intent.getParcelableExtra<StoredBook>("BOOK_Detail") ?: null
            viewModel?.isBookStored?.value = true
        } else {
            val bookInfo: Book? = intent.getParcelableExtra("BOOK_INFO")
            bookInfo?.let {
                bookDetail =
                    StoredBook(it.booktitle, it.author, "UU看書", "", "", it.bookUrl, false, -5, it.bookId) ?: null
            }
        }

        repository?.let { r ->
            bookDetail?.let { bd ->
                viewModel = ViewModelProvider(this, BookDetailViewModelFactory(applicationContext, bd, r))
                    .get(BookDetailViewModel::class.java)
                viewModel?.isBookStored?.value = bookIsStored
            }
        }
//        viewModel?.storedBook()
//        val bookIsStored = intent.getBooleanExtra("BOOK_IS_STORED",false)
//        var bookDetail : StoredBook? = null
//        if(bookIsStored) {
//            bookDetail = intent.getParcelableExtra<StoredBook>("BOOK_Detail")?:null
//            repository?.let {
//                viewModel = ViewModelProvider(
//                    this,
//                    BookDetailViewModelFactory(applicationContext, bookDetail!!, it)
//                ).get(BookDetailViewModel::class.java)
//            }
//            viewModel?.isBookStored?.value = true
//        } else {
//            val bookInfo : Book? = intent.getParcelableExtra("BOOK_INFO")
//            bookInfo?.let {
//            bookDetail = StoredBook(it.booktitle, it.author, "UU看書", "", "", it.bookUrl, false, -5, it.bookId)?:null
//            }
//        }
//            bookDetail?.let {
//            viewModel = ViewModelProvider(
//                this, BookDetailViewModelFactory(applicationContext, it, repository!!)
//            ).get(BookDetailViewModel::class.java)
//            }
    }


    fun getActivityViewModel(): BookDetailViewModel?{
        return viewModel
    }

    private fun setUiListener() {}
    private fun getBookDetail() {}

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActbar() {
        setSupportActionBar(NovelDetailToolbar)
        supportActionBar?.apply {
            title = viewModel?.bookInformation?.value?.bookname
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        viewModel?.isBookStoredStateChange?.value?.let {
            if(it){
                intent.apply {
                    putExtra("ISBOOKSTORED", viewModel?.isBookStored?.value ?: false)
                }
            }
        }
        intent.putExtra("BOOKID", viewModel?.bookInformation?.value?.bookid)
        intent.putExtra("LASTREADCHAPTER", viewModel?.bookLastReadInfo?.value?.chapterTitle)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    companion object {
        const val TAG = "BookDetailActivity"
    }
}