package com.hc.wandroidstudy.module.home.presentation.view

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.*

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

import com.hc.wandroidstudy.R
import com.hc.wandroidstudy.common.adapter.CommonListAdapter
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.data.PageState
import com.hc.wandroidstudy.common.livedata_ext.*

import com.hc.wandroidstudy.common.mvrx.MvRxFragment
import com.hc.wandroidstudy.common.network.errorCode
import com.hc.wandroidstudy.common.network.errorMsg
import com.hc.wandroidstudy.databinding.*
import com.hc.wandroidstudy.module.home.presentation.data.HomeState
import com.hc.wandroidstudy.module.home.presentation.view.binder.*

import com.hc.wandroidstudy.module.home.presentation.vm.HomeViewModel
import com.hc.wandroidstudy.utils.viewbinding.viewBinding


class HomeFragment : MvRxFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by fragmentViewModel()

    private val binding: FragmentHomeBinding by viewBinding()

    var adapter = CommonListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //监听错误
        viewModel.onEach(HomeState::error, uniqueOnly()) {
            it?.errorMsg?.run {
                ToastUtils.showShort(this)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresh()
        initLoadMore()
        initAdapter()
    }

    /**
     * 下拉刷新
     */
    private fun initRefresh() {
        binding.refresh.setOnRefreshListener {
            // 这里的作用是防止下拉刷新的时候还可以上拉加载
            adapter.loadMoreModule.isEnableLoadMore = false
            viewModel.refreshHomeData()
        }
    }

    /**
     * 初始化加载更多
     */
    private fun initLoadMore() {
        adapter.loadMoreModule.setOnLoadMoreListener { viewModel.loadHomeDataMore() }
        adapter.loadMoreModule.isAutoLoadMore = true
    }


    @SuppressLint("SetTextI18n")
    private fun initAdapter() {
        adapter.apply {
            addItemBinder(HomeBannerBinder())
            addItemBinder(HomeWxBinder())
            addItemBinder(HomeCategoryBinder(), HomeCategoryBinder.Differ())
            addItemBinder(HomeTitleBinder(), HomeTitleBinder.Differ())
            addItemBinder(HomeProjectBinder(), HomeProjectBinder.Differ())
        }
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItem(position)) {
                    is HotProjectItemData -> 1
                    else -> 2
                }
            }
        }
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = adapter
    }

    /**
     * 数据变化时
     */
    override fun invalidate() {
        withState(viewModel) {
            LogUtils.e("invalidate", "界面状态 ${it.pageState}")
            //重置上拉刷新和下拉刷新状态
            adapter.loadMoreModule.isEnableLoadMore = true
            binding.refresh.finishRefresh()

            //更新数据
            adapter.setDiffNewData(it.data.toMutableList())

            when (it.pageState) {
                PageState.Complete -> adapter.loadMoreModule.loadMoreComplete()
                PageState.End -> adapter.loadMoreModule.loadMoreEnd()
                PageState.Fail -> adapter.loadMoreModule.loadMoreFail()
            }
        }
    }
}