package com.example.leafnovel.ui.main.view

import com.example.leafnovel.data.api.NovelApi
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leafnovel.R
import com.example.leafnovel.ui.main.adapter.BookChAdapter
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.BookChsResults
import kotlinx.android.synthetic.main.activity_book_directory.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookDirectoryActivity : AppCompatActivity(), BookChAdapter.OnItemClickListener {
    val adapter = BookChAdapter()
    var bookId = ""
    var booktitle = ""
    private lateinit var transBookChResults: BookChsResults

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_directory)

        bookId = intent.getStringExtra("BOOK_ID") ?: ""

        booktitle = intent.getStringExtra("BOOK_TITLE") ?: ""

        BookTitleView.text = booktitle

        BookChRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookDirectoryActivity)
            adapter = this@BookDirectoryActivity.adapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.RequestChList(bookId)
            transBookChResults = bookChResults
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookDirectoryActivity)
            }
        }
    }

    override fun onItemClick(bookCh: BookChapter,position:Int) {
//        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, BookContentActivity::class.java).apply {
            putExtra("BOOK_INDEX", position)
            putExtra("BOOK_ID", bookId)
            putExtra("BOOK_CH_ID", bookCh.chId)
            putExtra("BOOK_CH_URL", bookCh.chUrl)
            putExtra("BOOK_CH_TITLE", bookCh.chtitle)
            putExtra("BOOK_TITLE", booktitle)
            putParcelableArrayListExtra("NOVEL_CHAPTERS", transBookChResults)
        }
        this.startActivity(intent)
    }

}