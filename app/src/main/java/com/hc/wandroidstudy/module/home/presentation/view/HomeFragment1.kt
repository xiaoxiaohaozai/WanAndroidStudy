package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.createViewModelLazy
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.common.base.*
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.mvi_core.observeState
import com.hc.wandroidstudy.databinding.FragmentHomeBinding
import com.hc.wandroidstudy.module.home.presentation.data.HomeState
import com.hc.wandroidstudy.module.home.presentation.data.HomeViewAction
import com.hc.wandroidstudy.module.home.presentation.data.HomeViewEvent
import com.hc.wandroidstudy.module.home.presentation.data.HomeViewState
import com.hc.wandroidstudy.module.home.presentation.view.binder.*
import com.hc.wandroidstudy.module.home.presentation.vm.HomeViewModel1


class HomeFragment1 : CommonListFragment() {

    private val viewModel by viewModels<HomeViewModel1> {
        HomeViewModel1.ViewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is HomeViewEvent.ShowToast -> {
                    ToastUtils.showShort(event.message)
                }
                else -> {
                }
            }
        }

        viewModel.viewState.run {

            //监听界面状态
            observeState(viewLifecycleOwner, HomeViewState::pageStatus) {
                LogUtils.eTag("界面状态", it.javaClass.simpleName)
                when (it) {
                    is PageStatus.Empty -> {
                        baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
                        binding.refresh.setEnableRefresh(false)
                        binding.state.showEmpty()
                    }
                    is PageStatus.Success -> {
                        baseBinderAdapter.loadMoreModule.isEnableLoadMore = true
                        binding.refresh.setEnableRefresh(true)
                        binding.state.showContent()
                    }
                    is PageStatus.Error -> {
                        baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
                        binding.refresh.setEnableRefresh(false)
                        binding.state.showError()
                    }
                    is PageStatus.Loading -> {
                        baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
                        binding.refresh.setEnableRefresh(false)
                        binding.state.showLoading()
                    }
                }
            }

            //监听下拉刷新
            observeState(viewLifecycleOwner, HomeViewState::refreshStatus) {
                LogUtils.eTag("下拉状态", it.javaClass.simpleName)
                when (it) {
                    is RefreshStatus.RefreshSuccess -> {
                        binding.refresh.finishRefresh()
                        if (!baseBinderAdapter.loadMoreModule.isEnableLoadMore) {
                            baseBinderAdapter.loadMoreModule.isEnableLoadMore = true
                        }
                    }
                    is RefreshStatus.RefreshLoading -> {
                        if (baseBinderAdapter.loadMoreModule.isEnableLoadMore) {
                            baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
                        }
                    }
                    is RefreshStatus.RefreshFail -> {
                        binding.refresh.finishRefresh(false)
                        if (!baseBinderAdapter.loadMoreModule.isEnableLoadMore) {
                            baseBinderAdapter.loadMoreModule.isEnableLoadMore = true
                        }
                    }
                }
            }

            //监听上拉加载
            observeState(viewLifecycleOwner, HomeViewState::loadMoreStatus) {
                LogUtils.eTag("上拉状态", it.javaClass.simpleName)
                when (it) {
                    is LoadStatus.LoadMoreSuccess -> {
                        baseBinderAdapter.loadMoreModule.isAutoLoadMore = true
                        binding.refresh.setEnableRefresh(true)
                        if (it.hasMore) {
                            baseBinderAdapter.loadMoreModule.loadMoreComplete()
                        } else {
                            baseBinderAdapter.loadMoreModule.loadMoreEnd()
                        }
                    }
                    is LoadStatus.LoadMoreLoading -> {
                        baseBinderAdapter.loadMoreModule.isAutoLoadMore = false
                        binding.refresh.setEnableRefresh(false)
                    }
                    is LoadStatus.LoadMoreFail -> {
                        baseBinderAdapter.loadMoreModule.loadMoreFail()
                        baseBinderAdapter.loadMoreModule.isAutoLoadMore = true
                        binding.refresh.setEnableRefresh(true)
                    }
                }
            }

            //监听数据
            observeState(viewLifecycleOwner, HomeViewState::data) {
                LogUtils.eTag("界面数据", it.size)

                baseBinderAdapter.setDiffNewData(it.toMutableList())

            }
        }
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
        viewModel.dispatch(HomeViewAction.LoadDataMore)
    }

    override fun onRefreshEvent() {
        viewModel.dispatch(HomeViewAction.RefreshData)
    }
}