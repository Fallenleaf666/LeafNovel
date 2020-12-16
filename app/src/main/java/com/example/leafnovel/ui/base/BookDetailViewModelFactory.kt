package com.example.leafnovel.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafnovel.data.model.BookChapter
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.repository.Repository
import com.example.leafnovel.ui.main.viewmodel.BookContentViewModel
import com.example.leafnovel.ui.main.viewmodel.BookDetailViewModel

//afterday can change trans map
class BookDetailViewModelFactory (private val context : Context, val storedBook : StoredBook,
                                  val repository: Repository ) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BookDetailViewModel::class.java)){
            return BookDetailViewModel(context,storedBook,repository) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}