package com.example.leafnovel.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.ui.main.viewmodel.BookContentBetaViewModel
import com.example.leafnovel.ui.main.viewmodel.BookContentViewModel

//afterday can change trans map
class BookContentViewModelFactory (private val context : Context, val firstBookChapter : BookChapter,
                                   val chapterList : ArrayList<BookChapter>,val _bookTitle : String,val _bookId : String) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if(modelClass.isAssignableFrom(BookContentViewModel::class.java)){
        if(modelClass.isAssignableFrom(BookContentBetaViewModel::class.java)){
//            return BookContentViewModel(context,firstBookChapter,chapterList,_bookTitle,_bookId) as T }
            return BookContentBetaViewModel(context,firstBookChapter,chapterList,_bookTitle,_bookId) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}