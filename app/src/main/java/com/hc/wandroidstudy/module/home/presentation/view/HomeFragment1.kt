package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle

import android.view.View

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.common.base.*
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.mvi_core.observeState
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
                binding.refresh.closeHeaderOrFooter()
                binding.refresh.setEnableLoadMore(false)
                binding.refresh.setEnableRefresh(false)
                when (it) {
                    is PageStatus.Empty -> binding.state.showEmpty()
                    is PageStatus.Success -> {
                        binding.refresh.setEnableLoadMore(true)
                        binding.refresh.setEnableRefresh(true)
                        binding.state.showContent()
                    }
                    is PageStatus.Error -> binding.state.showError()
                    is PageStatus.Loading -> binding.state.showLoading()
                }
            }

            //监听下拉刷新
            observeState(viewLifecycleOwner, HomeViewState::refreshStatus) {
                LogUtils.eTag("下拉状态", it.javaClass.simpleName)
                when (it) {
                    is RefreshStatus.RefreshSuccess -> {
                        binding.refresh.finishRefresh()
                    }
                    is RefreshStatus.RefreshLoading -> {
                        if (!binding.refresh.isRefreshing) {
                            binding.refresh.autoRefreshAnimationOnly()
                        }
                    }
                    is RefreshStatus.RefreshFail -> {
                        binding.refresh.finishRefresh(false)
                    }
                }
            }

            //监听上拉加载
            observeState(viewLifecycleOwner, HomeViewState::loadMoreStatus) {
                LogUtils.eTag("上拉状态", it.javaClass.simpleName)
                when (it) {
                    is LoadStatus.LoadMoreSuccess -> {
                        if (it.hasMore) {
                            binding.refresh.finishLoadMore()
                        } else {
                            binding.refresh.finishLoadMoreWithNoMoreData()
                        }
                    }
                    is LoadStatus.LoadMoreLoading -> {
                        if (!binding.refresh.isLoading) {
                            binding.refresh.autoLoadMoreAnimationOnly()
                        }
                    }
                    is LoadStatus.LoadMoreFail -> {
                        binding.refresh.finishLoadMore(false)
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


    override fun initAdapterConfig(adapter: BaseBinderAdapter) {
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