package com.example.leafnovel.ui.main.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.leafnovel.ui.main.view.fragment.BookDirectoryFragment
import com.example.leafnovel.ui.main.view.fragment.BookIntroduceFragment

class BookDetailPageAdapter (fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager,lifecycle){


    private val tabFragmentsCreators:Map<Int,()->Fragment> = mapOf(
        0 to {BookIntroduceFragment()},
        1 to {BookDirectoryFragment()},
//        2 to {BookIntroduceFragment()}
    )
    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke()?:throw IndexOutOfBoundsException()
    }
}