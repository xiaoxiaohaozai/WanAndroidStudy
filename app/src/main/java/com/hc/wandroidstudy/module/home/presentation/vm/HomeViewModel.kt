package com.hc.wandroidstudy.module.home.presentation.vm


import com.airbnb.mvrx.*
import com.blankj.utilcode.util.LogUtils

import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectData
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.data.WxData

import com.hc.wandroidstudy.common.mvrx.MvRxViewModel

import com.hc.wandroidstudy.common.network.WanAndroidClient
import com.hc.wandroidstudy.module.home.data.HomeRepository
import com.hc.wandroidstudy.module.home.domain.IHomeRepository
import com.hc.wandroidstudy.module.home.presentation.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeViewModel(initialState: HomeState, private val repository: IHomeRepository) : MvRxViewModel<HomeState>(initialState) {


    var currentPage = 0

    /**
     * 构建相关带参数的ViewModel 否则报错
     */
    companion object : MavericksViewModelFactory<HomeViewModel, HomeState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeState): HomeViewModel {
            //可以使用依赖注入,这里简单使用
            val wanAndroidApi = WanAndroidClient()
            val homeRepository = HomeRepository(wanAndroidApi)
            return HomeViewModel(state, homeRepository)
        }
    }

    init {
        refreshHomeData()
    }

    fun loadHomeDataMore() {
        viewModelScope.launch {
            val tempPage = currentPage + 1
            repository.getHotProjectList(currentPage).collect {
                currentPage = tempPage
                setState {
                    copy(items = items + it.datas, hasMore = it.curPage < it.pageCount)
                }
            }
        }
    }

    fun refreshHomeData() {
        currentPage = 0
        viewModelScope.launch {
            /**合并三个请求*/
            combine(repository.getBanner(), repository.getWx(), repository.getHotProjectList(currentPage)) { array ->
                val bannerData = array[0] as List<BannerData>
                val wxData = array[1] as List<WxData>
                val hotProjectData = array[2] as HotProjectData
                val items = mutableListOf<Any>()
                val wxItems = wxData.subList(0, wxData.size.coerceAtMost(7))
                (wxItems as MutableList).add(WxData(-1, -1, "更多", -1, -1))
                items.apply {
                    add(BannerUIModel(bannerData))
                    add(WxUIModel(wxItems))
                    add(CategoryItemUIModel())
                    add("热门项目")
                    addAll(hotProjectData.datas)
                }
                setState {
                    copy(items = items, hasMore = hotProjectData.curPage < 3)
                }
            }
                    .onStart { setState { copy(isLoading = true) } }
                    .onCompletion { setState { copy(isLoading = false) } }
                    .catch { setState { copy(error = it) } }.collect()
        }
    }
}