package com.hc.wandroidstudy.app.view

import androidx.fragment.app.Fragment

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hc.wandroidstudy.module.article.view.HotArticlesFragment
import com.hc.wandroidstudy.module.home.presentation.view.HomeFragment
import com.hc.wandroidstudy.module.home.presentation.view.HomeFragment1
import java.security.InvalidParameterException


class MainFragmentAdapter(fragment: Fragment, private val tabs: List<String>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment1()
            1 -> HotArticlesFragment()
            else -> throw InvalidParameterException("current tab not support")
        }
    }
}