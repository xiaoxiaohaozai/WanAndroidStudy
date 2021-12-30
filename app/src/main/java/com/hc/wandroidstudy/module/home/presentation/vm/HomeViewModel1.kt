package com.hc.wandroidstudy.module.home.presentation.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chad.library.adapter.base.loadmore.LoadMoreStatus
import com.hc.wandroidstudy.common.base.LoadStatus
import com.hc.wandroidstudy.common.base.PageStatus
import com.hc.wandroidstudy.common.base.RefreshStatus
import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectData
import com.hc.wandroidstudy.common.data.PageState
import com.hc.wandroidstudy.common.data.WxData
import com.hc.wandroidstudy.common.mvi_core.*
import com.hc.wandroidstudy.common.network.WanAndroidClient
import com.hc.wandroidstudy.common.network.errorMsg
import com.hc.wandroidstudy.module.home.data.HomeRepository
import com.hc.wandroidstudy.module.home.domain.IHomeRepository
import com.hc.wandroidstudy.module.home.presentation.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeViewModel1(private val repository: IHomeRepository) : ViewModel() {

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            //可以使用依赖注入,这里简单使用
            val wanAndroidApi = WanAndroidClient()
            val homeRepository = HomeRepository(wanAndroidApi)
            return HomeViewModel1(homeRepository) as T
        }
    }

    private var currentPage = 0

    //界面数据
    private val _viewState = MutableLiveData(HomeViewState())

    val viewState = _viewState.asLiveData()

    //单次事件
    private val _viewEvent: SingleLiveEvent<HomeViewEvent> = SingleLiveEvent()

    val viewEvent = _viewEvent.asLiveData()


    //外部触发的action
    fun dispatch(action: HomeViewAction) {
        when (action) {
            is HomeViewAction.LoadData -> loadData()
            is HomeViewAction.RefreshData ->{
                refreshData()
            }
            is HomeViewAction.LoadDataMore -> loadDataMore()
        }
    }

    init {
        dispatch(HomeViewAction.LoadData)
    }

    /**
     * 加载更多
     */
    private fun loadDataMore() {
        viewModelScope.launch {
            val tempPage = currentPage + 1
            repository.getHotProjectList(currentPage).onStart {
                _viewState.setState {
                    copy(loadMoreStatus = LoadStatus.LoadMoreLoading)
                }
            }.catch {
                _viewState.setState {
                    copy(loadMoreStatus = LoadStatus.LoadMoreFail(it))
                }
                _viewEvent.setEvent(HomeViewEvent.ShowToast(it.errorMsg))
            }.collect {

               // delay(4000)
                currentPage = tempPage
                _viewState.setState {
                    copy(data = this.data + it.datas, loadMoreStatus = LoadStatus.LoadMoreSuccess(it.curPage < it.pageCount))
                }
            }
        }
    }

    /**
     * 第一次加载
     */
    private fun loadData() {
        viewModelScope.launch {
            currentPage = 0
            //合并三个请求

            combine(repository.getBanner(),
                    repository.getWx(),
                    repository.getHotProjectList(currentPage)) { array ->

              //  delay(2000)

                //构建ui模型
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

                _viewState.setState {
                    copy(
                            pageStatus = PageStatus.Success,
                            data = items,
                            loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)
                    )
                }
            }.onStart {
                _viewState.setState {
                    copy(pageStatus = PageStatus.Loading)
                }
            }.catch {
                _viewState.setState {
                    copy(pageStatus = PageStatus.Error(it))
                }
                _viewEvent.setEvent(HomeViewEvent.ShowToast(it.errorMsg))
            }.collect()
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            currentPage = 0
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
                _viewState.setState {
                    copy(
                            data = items,
                            refreshStatus = RefreshStatus.RefreshSuccess,
                            loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)
                    )
                }
            }.onStart {
                _viewState.setState {
                    copy(refreshStatus = RefreshStatus.RefreshLoading)
                }

            }.catch {
                _viewState.setState {
                    copy(refreshStatus = RefreshStatus.RefreshFail(it),pageStatus = PageStatus.Error(it))
                }
                _viewEvent.setEvent(HomeViewEvent.ShowToast(it.errorMsg))
            }.collect()
        }
    }
}