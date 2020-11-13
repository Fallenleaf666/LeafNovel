package com.example.leafnovel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.leafnovel.bookcase.StoredBook
import com.example.leafnovel.bookcase.StoredBookDB
import com.example.leafnovel.data.Repository
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId =  intent.getStringExtra("BOOK_ID")
        val booktitle =  intent.getStringExtra("BOOK_TITLE")
        val author =  intent.getStringExtra("BOOK_AUTHOR")
        val bookUrl =  intent.getStringExtra("BOOK_URL")
        val bookDetailMap : MutableMap<String,String> = mutableMapOf()
        val repository = StoredBookDB.getInstance(applicationContext)?.storedbookDao()?.let { Repository(it) }


        CoroutineScope(Dispatchers.IO).launch {
            bookDetailMap.putAll(NovelApi.RequestNovelDetail(bookId))
            println(bookDetailMap)
            CoroutineScope(Dispatchers.Main).launch {
                NewChapterText.text = bookDetailMap.get("newChapter")
                UpdateTimeText.text = bookDetailMap.get("updateTime")
                Book_DescripeView.text = bookDetailMap.get("bookDescripe")
                Book_stateText.text = bookDetailMap.get("novelState")
                Glide.with(applicationContext).load("http:"+bookDetailMap.get("imgUrl"))
                    .placeholder(R.drawable.ic_outline_image_search_24)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .fallback(R.drawable.ic_baseline_image_24)
                    .into(Book_imgView)
                println("bookDetailMap")
                println(bookDetailMap)

                StoreBT.setOnClickListener{
                    val storedbook = StoredBook(booktitle,author,
                        "UU看書",
                        NewChapterText.text.toString(),
                        bookDetailMap.get("imgUrl").toString(),
                        bookId)
                    CoroutineScope(Dispatchers.IO).launch {
                        repository?.insert(storedbook)
                    }
                }
                LoadProgressBar.visibility = View.INVISIBLE
            }
        }


        val book_titleView = findViewById<TextView>(R.id.Book_titleView).apply { text = booktitle }
        val book_authorView = findViewById<TextView>(R.id.Book_authorView).apply { text = author }
//        val book_descripeView = findViewById<TextView>(R.id.Book_DescripeView).apply { text = bookUrl }
        val book_directory = findViewById<Button>(R.id.bookDitectoryBT).apply { setOnClickListener(View.OnClickListener {
            if(bookId!=null){val intent = Intent(this@BookDetailActivity,BookDirectoryActivity::class.java).apply {
                putExtra("BOOK_ID",bookId)
                putExtra("BOOK_TITLE",booktitle)
            }
                startActivity(intent)}

        })}
    }


}