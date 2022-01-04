package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.common.base.CommonListFragment
import com.hc.wandroidstudy.common.base.LoadStatus
import com.hc.wandroidstudy.common.base.PageStatus
import com.hc.wandroidstudy.common.base.RefreshStatus
import com.hc.wandroidstudy.common.base.mvi.collectState
import com.hc.wandroidstudy.data.bean.HotProjectItemData
import com.hc.wandroidstudy.module.home.presentation.view.binder.*
import com.hc.wandroidstudy.module.home.presentation.viewmodel.HomePageViewEffect
import com.hc.wandroidstudy.module.home.presentation.viewmodel.HomePageViewEvent
import com.hc.wandroidstudy.module.home.presentation.viewmodel.HomePageViewModel
import com.hc.wandroidstudy.module.home.presentation.viewmodel.HomePageViewState
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

        //TODO 调试使用，用于查看界面状态
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                LogUtils.eTag("界面状态","缺省页 ${it.pageStatus.javaClass.simpleName}，下拉刷新 ${it.refreshStatus.javaClass.simpleName},上拉加载 ${it.loadMoreStatus.javaClass.simpleName}，列表数据 ${it.data.size}")
            }
        }

        //TODO 注意 collect 是挂起方法需要放在不同lifecycleScope
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectState(HomePageViewState::loadMoreStatus) { state ->
                when (state) {
                    is LoadStatus.LoadMoreSuccess -> {
                        if (state.hasMore) {
                            refreshView.finishLoadMore()
                        } else {
                            refreshView.finishLoadMoreWithNoMoreData()
                        }
                    }
                    is LoadStatus.LoadMoreLoading -> {
                        if (!refreshView.isLoading) {
                            refreshView.autoLoadMoreAnimationOnly()
                        }
                    }
                    is LoadStatus.LoadMoreFail -> {
                        refreshView.finishLoadMore(false)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectState(HomePageViewState::refreshStatus) { state ->
                when (state) {
                    is RefreshStatus.RefreshSuccess -> {
                        refreshView.finishRefresh()
                    }
                    is RefreshStatus.RefreshLoading -> {
                        if (!refreshView.isRefreshing) {
                            refreshView.autoRefreshAnimationOnly()
                        }
                    }
                    is RefreshStatus.RefreshFail -> {
                        refreshView.finishRefresh(false)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectState(HomePageViewState::pageStatus) { state ->
                refreshView.closeHeaderOrFooter()
                refreshView.setEnableLoadMore(false)
                refreshView.setEnableRefresh(false)
                when (state) {
                    is PageStatus.Empty -> stateView.showEmpty()
                    is PageStatus.Success -> {
                        refreshView.setEnableLoadMore(true)
                        refreshView.setEnableRefresh(true)
                        stateView.showContent()
                    }
                    is PageStatus.Error -> stateView.showError()
                    is PageStatus.Loading -> stateView.showLoading()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.state.collectState(HomePageViewState::data) {
                baseBinderAdapter.setDiffNewData(it.toMutableList())
            }
        }

        //进入start时订阅
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            //监听一次性事件
            viewModel.effect.collect {
                when (it) {
                    is HomePageViewEffect.ShowToast -> ToastUtils.showShort(it.message)
                    else -> {
                    }
                }
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
        viewModel.sendEvent(HomePageViewEvent.LoadDataMore)
    }

    override fun onRefreshEvent() {
        viewModel.sendEvent(HomePageViewEvent.RefreshData)

    }
}