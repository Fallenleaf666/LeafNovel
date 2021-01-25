package com.example.leafnovel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.leafnovel.data.api.NovelApi
import com.example.leafnovel.data.model.Book
import com.example.leafnovel.data.model.BooksResults
import com.example.leafnovel.data.model.SearchResult
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.main.viewmodel.SearchBookViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchBooksViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @MockK(relaxed = true)
    lateinit var repository: Repository
    private var searchBooksResult = BooksResults()
    private lateinit var viewModel:SearchBookViewModel

    @Before
    fun setUp(){
        MockKAnnotations.init(this)
//        BooksResults().add(Book().apply {
//            booktitle =  "測試書本1號"
//            bookId = "140551"
//            bookUrl = "http://test1"
//            author = "我是王老一"
//            bookDescripe = "這是一本講述王老一一生輝煌的書"
//            updateTime = "2021-1-21"
//        })
//        BooksResults().add(Book().apply {
//            booktitle =  "測試書本2號"
//            bookId = "140552"
//            bookUrl = "http://test2"
//            author = "我是王老二"
//            bookDescripe = "這是一本講述王老二一生輝煌的書"
//            updateTime = "2021-1-22"
//        })
//        viewModel = SearchBookViewModel()
    }

    @Test
    fun apiTest(){
        val searchBookResult = NovelApi.requestSearchNovel("基因")
        assert(searchBookResult.state == SearchResult.SUCCESS)
        assert(searchBookResult.booksResults.size != 0)
    }

}