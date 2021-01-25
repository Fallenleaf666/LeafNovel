package com.example.leafnovel.data.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.leafnovel.data.model.SearchResult
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NovelApiTest {
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//    @MockK(relaxed = true)
    @Before
    fun setUp(){
//        MockKAnnotations.init(this)
    }

    @Test
    fun isRequestSearchNovelHasResult(){
        val searchBookResult = NovelApi.requestSearchNovel("基因")
        assert(searchBookResult.state == SearchResult.SUCCESS)
        assert(searchBookResult.booksResults.size != 0)
    }

    @Test
    fun isRequestSearchNovelResultFailWhenNoNet(){
        val searchBookResult = NovelApi.requestSearchNovel("基因")
        assert(searchBookResult.state == SearchResult.FAIL)
        assert(searchBookResult.booksResults.size == 0)
    }
}