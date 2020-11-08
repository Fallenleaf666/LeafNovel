package com.example.leafnovel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class BookDetailActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bookId =  intent.getStringExtra("BOOK_ID")
        val booktitle =  intent.getStringExtra("BOOK_TITLE")
        val author =  intent.getStringExtra("BOOK_AUTHOR")
        val bookUrl =  intent.getStringExtra("BOOK_URL")

        val book_titleView = findViewById<TextView>(R.id.Book_titleView).apply { text = booktitle }
        val book_authorView = findViewById<TextView>(R.id.Book_authorView).apply { text = author }
        val book_descripeView = findViewById<TextView>(R.id.Book_descripeView).apply { text = bookUrl }
        val book_directory = findViewById<Button>(R.id.bookDitectoryBT).apply { setOnClickListener(View.OnClickListener {
            if(bookId!=null){val intent = Intent(this@BookDetailActivity,BookDirectoryActivity::class.java).apply {
                putExtra("BOOK_ID",bookId)
                putExtra("BOOK_TITLE",booktitle)
            }
                startActivity(intent)}

        })}
    }


}