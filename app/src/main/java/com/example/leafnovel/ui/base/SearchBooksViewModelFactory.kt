package com.example.leafnovel.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafnovel.ui.main.viewmodel.SearchBookViewModel

class SearchBookViewModelFactory(private val context : Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchBookViewModel::class.java)){
            return SearchBookViewModel(context) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}