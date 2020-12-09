package com.example.leafnovel.ui.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.leafnovel.ui.main.viewmodel.MyBooksViewModel

class MyBooksViewModelFactory(private val context :Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MyBooksViewModel::class.java)){
            return MyBooksViewModel(context) as T }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}