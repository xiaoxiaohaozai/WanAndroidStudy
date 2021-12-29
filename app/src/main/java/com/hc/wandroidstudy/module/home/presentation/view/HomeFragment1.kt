package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.common.base.BaseFragment
import com.hc.wandroidstudy.common.base.CommonListFragment
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.databinding.FragmentHomeBinding
import com.hc.wandroidstudy.module.home.presentation.view.binder.*
import com.hc.wandroidstudy.module.home.presentation.vm.HomeViewModel1


class HomeFragment1 : CommonListFragment() {

    val viewModel = viewModels<HomeViewModel1>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun initAdapter(adapter: BaseBinderAdapter) {
        adapter.apply {
            addItemBinder(HomeBannerBinder())
            addItemBinder(HomeWxBinder())
            addItemBinder(HomeCategoryBinder(), HomeCategoryBinder.Differ())
            addItemBinder(HomeTitleBinder(), HomeTitleBinder.Differ())
            addItemBinder(HomeProjectBinder(), HomeProjectBinder.Differ())
        }
    }

    override fun getLayoutManager(adapter: BaseBinderAdapter): RecyclerView.LayoutManager {
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItem(position)) {
                    is HotProjectItemData -> 1
                    else -> 2
                }
            }
        }
        return layoutManager
    }


    override fun onLoadMoreEvent() {

    }

    override fun onRefreshEvent() {

    }
}