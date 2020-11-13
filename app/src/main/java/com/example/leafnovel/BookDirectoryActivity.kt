package com.example.leafnovel

import NovelApi
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_book_directory.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookDirectoryActivity : AppCompatActivity(),BookChAdapter.OnItemClickListener {

//    val novelApi=NovelApi()
    val adapter = BookChAdapter()
    var bookId =""
    var booktitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_directory)

//        recycler = findViewById<RecyclerView>(R.id.BookChRecycler)
        bookId =  intent.getStringExtra("BOOK_ID")

        booktitle =  intent.getStringExtra("BOOK_TITLE")

        val book_titleView = findViewById<TextView>(R.id.BookTitleView).apply { text = booktitle }

        BookChRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookDirectoryActivity)
            adapter = this@BookDirectoryActivity.adapter
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bookChResults = NovelApi.RequestChList(bookId)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
                adapter.setItems(bookChResults, this@BookDirectoryActivity)
            }
        }
    }

    override fun onItemClick(bookCh: BookChapter) {
        Toast.makeText(this, "Item ${bookCh.chtitle} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,BookContentActivity::class.java).apply {
            putExtra("BOOK_ID",bookId)
            putExtra("BOOK_CH_ID",bookCh.chId)
            putExtra("BOOK_CH_URL",bookCh.chUrl)
            putExtra("BOOK_CH_TITLE",bookCh.chtitle)
            putExtra("BOOK_TITLE",booktitle)
        }
        this.startActivity(intent)
//        print(bookResults)
    }
}