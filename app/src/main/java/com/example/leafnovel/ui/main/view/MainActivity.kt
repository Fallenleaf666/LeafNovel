package com.example.leafnovel.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle

import androidx.fragment.app.Fragment
import com.example.leafnovel.R
import com.example.leafnovel.ui.main.view.fragment.MyBooks
import com.example.leafnovel.ui.main.view.fragment.MySetting
import com.example.leafnovel.ui.main.view.fragment.Search
import com.google.android.material.bottomnavigation.BottomNavigationView


import com.ncapdevi.fragnav.FragNavController
import java.lang.IllegalStateException


class MainActivity : AppCompatActivity(),FragNavController.RootFragmentListener {

    companion object{
        val TAG = MainActivity::class.java.simpleName
    }
    lateinit var bottomNavigationView : BottomNavigationView
    private var fragNavController : FragNavController =
        FragNavController(supportFragmentManager, R.id.nav_host_container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        if (savedInstanceState == null) {
//            setupBottomNavigationBar()
//        } // Else, need to wait for onRestoreInstanceState
        initFragNavController(savedInstanceState)
        initBottomNavigationView()

    }

    private fun initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener (bottomNavigationViewListener)
    }

    private val bottomNavigationViewListener = BottomNavigationView.OnNavigationItemSelectedListener {
        item ->
        when(item.itemId){
            R.id.menuHomeItem -> {
                fragNavController.switchTab(FragNavController.TAB1)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menuSearchItem -> {
                fragNavController.switchTab(FragNavController.TAB2)
                return@OnNavigationItemSelectedListener true
            }
            R.id.menuSettingItem -> {
                fragNavController.switchTab(FragNavController.TAB3)
                return@OnNavigationItemSelectedListener true
            }
        }
            false
    }

    private fun initFragNavController(savedInstanceState: Bundle?) {
        val fragments = listOf<Fragment>(MyBooks.newInstance, Search.newInstance, MySetting.newInstance)
        fragNavController.apply {
            rootFragments = fragments
            initialize(FragNavController.TAB1,savedInstanceState)
        }

    }
    override val numberOfRootFragments : Int
        get() = 3

    override fun getRootFragment(index: Int): Fragment {
        when(index){
            FragNavController.TAB1 -> return MyBooks.newInstance
            FragNavController.TAB2 -> return Search.newInstance
            FragNavController.TAB3 -> return MySetting.newInstance
        }
        throw IllegalStateException("index unknow")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        fragNavController.onSaveInstanceState(outState)
    }


//    override fun onSupportNavigateUp(): Boolean {
//        return currentNavController?.value?.navigateUp() ?: false
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        // Now that BottomNavigationBar has restored its instance state
//        // and its selectedItemId, we can proceed with setting up the
//        // BottomNavigationBar with Navigation
//        setupBottomNavigationBar()
//    }


}


