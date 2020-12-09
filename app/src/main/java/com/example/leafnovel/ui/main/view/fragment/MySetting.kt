package com.example.leafnovel.ui.main.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leafnovel.R
import com.example.leafnovel.data.model.StoredBook
import com.example.leafnovel.data.database.StoredBookDB
import com.example.leafnovel.data.repository.Repository
import kotlinx.android.synthetic.main.fragment_my_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MySetting : Fragment() {
    companion object{
        val newiInstance : MySetting by lazy{
            MySetting()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = context?.let { StoredBookDB.getInstance(it)?.storedbookDao()?.let { Repository(it) } }

        DeleteDbBT.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
            repository?.deletdAll()
            }
        }
            AddBT.setOnClickListener{
                CoroutineScope(Dispatchers.IO).launch {
            val storedbook = StoredBook("易筋經","達摩","葉子書城","第一章 洒家不吃素啦",
                "http://www", "120000")
                    repository?.insert(storedbook)
                }
            }

    }
}