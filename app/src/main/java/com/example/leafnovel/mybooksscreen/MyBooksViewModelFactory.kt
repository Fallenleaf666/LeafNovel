package com.example.leafnovel.mybooksscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyBooksViewModelFactory(private val context :Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyBooksViewModel::class.java)){
            return MyBooksViewModel(context) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}