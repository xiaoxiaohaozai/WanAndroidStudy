package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.app.view.MainViewModel
import com.hc.wandroidstudy.common.base.CommonListFragment
import com.hc.wandroidstudy.common.base.LoadStatus
import com.hc.wandroidstudy.common.base.PageStatus
import com.hc.wandroidstudy.common.base.RefreshStatus
import com.hc.wandroidstudy.common.base.mvi.collectState
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.module.home.presentation.view.binder.*
import com.hc.wandroidstudy.module.home.presentation.vm.HomePageViewEffect
import com.hc.wandroidstudy.module.home.presentation.vm.HomePageViewEvent
import com.hc.wandroidstudy.module.home.presentation.vm.HomePageViewModel
import com.hc.wandroidstudy.module.home.presentation.vm.HomePageViewState
import kotlinx.coroutines.flow.collect


/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  采用mvi 模式搭建首页
 */
class HomePageFragment : CommonListFragment() {
    private val viewModel by viewModels<HomePageViewModel> {
        HomePageViewModel.ViewModelFactory()
    }
  

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //进入start时订阅
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            LogUtils.e("launchWhenStarted")
            //TODO  不嫩被多个观察者观察 问题?


            viewModel._state.collect {
                LogUtils.e("collect 1")
            }
            viewModel._state.collect {
                LogUtils.e("collect 2")
            }

            //监听一次性事件
            viewModel.effect.collect {
                when (it) {
                    is HomePageViewEffect.ShowToast -> ToastUtils.showShort(it.message)
                    else -> {
                    }
                }
            }
//            //监听界面状态
//            viewModel.state.collectState(HomePageViewState::pageStatus) {
//                LogUtils.e("界面状态", it.javaClass.simpleName)
//                refreshView.closeHeaderOrFooter()
//                refreshView.setEnableLoadMore(false)
//                refreshView.setEnableRefresh(false)
//                when (it) {
//                    is PageStatus.Empty -> stateView.showEmpty()
//                    is PageStatus.Success -> {
//                        refreshView.setEnableLoadMore(true)
//                        refreshView.setEnableRefresh(true)
//                        stateView.showContent()
//                    }
//                    is PageStatus.Error -> stateView.showError()
//                    is PageStatus.Loading -> stateView.showLoading()
//                }
//            }
//            //监听下拉刷新状态
//            viewModel.state.collectState(HomePageViewState::refreshStatus) {
//                LogUtils.e("下拉刷新", it.javaClass.simpleName)
//                when (it) {
//                    is RefreshStatus.RefreshSuccess -> {
//                        refreshView.finishRefresh()
//                    }
//                    is RefreshStatus.RefreshLoading -> {
//                        if (!refreshView.isRefreshing) {
//                            refreshView.autoRefreshAnimationOnly()
//                        }
//                    }
//                    is RefreshStatus.RefreshFail -> {
//                        refreshView.finishRefresh(false)
//                    }
//                }
//            }
//            //监听上拉加载状态
//            viewModel.state.collectState(HomePageViewState::loadMoreStatus) {
//                LogUtils.e("上拉加载", it.javaClass.simpleName)
//                when (it) {
//                    is LoadStatus.LoadMoreSuccess -> {
//                        if (it.hasMore) {
//                            refreshView.finishLoadMore()
//                        } else {
//                            refreshView.finishLoadMoreWithNoMoreData()
//                        }
//                    }
//                    is LoadStatus.LoadMoreLoading -> {
//                        if (!refreshView.isLoading) {
//                            refreshView.autoLoadMoreAnimationOnly()
//                        }
//                    }
//                    is LoadStatus.LoadMoreFail -> {
//                        refreshView.finishLoadMore(false)
//                    }
//                }
//            }

            //界面数据
//            viewModel.state.collectState(HomePageViewState::data) {
//                LogUtils.e("数据", "列表长度 ${it.size}")
//                baseBinderAdapter.setDiffNewData(it.toMutableList())
//            }

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
        viewModel.sendEvent(HomePageViewEvent.LoadDataMore)
    }

    override fun onRefreshEvent() {
        viewModel.sendEvent(HomePageViewEvent.RefreshData)

    }
}