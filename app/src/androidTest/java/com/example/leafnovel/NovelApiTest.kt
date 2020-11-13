package com.example.leafnovel


import NovelApi
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.runner.AndroidJUnitRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

    @RunWith(JUnit4::class)
class NovelApiTest{
//    @get:Rule
//        val novelApi = NovelApi()


    @Test
    fun GetNovelChapterContextTrue(){
//        NovelApi.RequestChTextBETA("/b/141809/39784.html")
        NovelApi.RequestSearchNovel("基因")
//        val appContext = InstrumentationRegistry.getTargetContext()
//        Assert.assertEquals("com.example.leafnovel", appContext.packageName)
    }
    
}