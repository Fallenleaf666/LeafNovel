package com.example.leafnovel

import RequestSearchNovel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),BookAdapter.OnItemClickListener {
    lateinit var searchView : SearchView
    val adapter = BookAdapter()
    companion object{
        val TAG = MainActivity::class.java.simpleName
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.search)

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                CoroutineScope(Dispatchers.IO).launch {
                    val bookResults :BooksResults=RequestSearchNovel(query)
//            Log.d(TAG,"onCreate:${bookResults.size}")
                    launch(Dispatchers.Main) {
                        adapter.setItems(bookResults, this@MainActivity)
                    }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
//                TODO("Not yet implemented")
                return false
            }
        })

        recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }


    }

    override fun onItemClick(book: Book) {
        Toast.makeText(this, "Item ${book.bookUrl} clicked", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,BookDetailActivity::class.java).apply {
            putExtra("BOOK_ID",book.bookUrl)
            putExtra("BOOK_TITLE",book.booktitle)
            putExtra("BOOK_AUTHOR",book.author)
            putExtra("BOOK_URL",book.bookUrl)
        }
        this.startActivity(intent)
//        print(bookResults)
    }

}


