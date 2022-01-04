package com.hc.wandroidstudy.app.view

import androidx.fragment.app.Fragment

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hc.wandroidstudy.module.article.view.HotArticlesFragment
import com.hc.wandroidstudy.module.home.ui.view.HomePageFragment
import java.security.InvalidParameterException


class MainFragmentAdapter(fragment: Fragment, private val tabs: List<String>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomePageFragment()
            1 -> HotArticlesFragment()
            else -> throw InvalidParameterException("current tab not support")
        }
    }
}