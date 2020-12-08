package com.example.leafnovel.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafnovel.BookChapter


class BookContentViewModelFactory (private val context : Context,val firstBookChapter : BookChapter,val chapterList : ArrayList<BookChapter>) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BookContentViewModel::class.java)){
            return BookContentViewModel(context,firstBookChapter,chapterList) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}