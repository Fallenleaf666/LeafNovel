package com.example.leafnovel.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchBookViewModelFactory(private val context : Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SearchBookViewModel::class.java)){
            return SearchBookViewModel(context) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}