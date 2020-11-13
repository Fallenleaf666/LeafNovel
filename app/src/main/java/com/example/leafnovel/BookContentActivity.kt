package com.example.leafnovel

import NovelApi
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_content)

        val bookChId =  intent.getStringExtra("BOOK_CH_ID")
        val chTitle =  intent.getStringExtra("BOOK_CH_TITLE")
        val chUrl =  intent.getStringExtra("BOOK_CH_URL")
        val bookId =  intent.getStringExtra("BOOK_ID")
        val bookTitle =  intent.getStringExtra("BOOK_TITLE")


        val book_chTitleView = findViewById<TextView>(R.id.ChTitle).apply { text = chTitle }
        val book_chContentView = findViewById<TextView>(R.id.ChContent).apply { text = "" }
        CoroutineScope(Dispatchers.IO).launch {
//            val chapterContents = NovelApi.RequestChText(chUrl)
            val chapterContents = NovelApi.RequestChTextBETA(chUrl,bookTitle)
//            Log.d(TAG,"onCreate:${bookResults.size}")
            launch(Dispatchers.Main) {
//                for(line in chapterContents){
//                    book_chContentView.setText(book_chContentView.text.toString() + line.chapterLineContent +"\n\n")
                    book_chContentView.setText(chapterContents)
//                }
            }
        }
//請求資源
    }
}