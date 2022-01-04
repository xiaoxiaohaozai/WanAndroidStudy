package com.hc.wandroidstudy.module.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hc.wandroidstudy.common.base.LoadStatus
import com.hc.wandroidstudy.common.base.PageStatus
import com.hc.wandroidstudy.common.base.RefreshStatus
import com.hc.wandroidstudy.common.base.mvi.BaseViewModel
import com.hc.wandroidstudy.common.base.mvi.UIEffect
import com.hc.wandroidstudy.common.base.mvi.UIEvent
import com.hc.wandroidstudy.common.base.mvi.UIState
import com.hc.wandroidstudy.data.WanAndroidRepository
import com.hc.wandroidstudy.data.bean.BannerData
import com.hc.wandroidstudy.data.bean.HotProjectData
import com.hc.wandroidstudy.data.bean.WxData
import com.hc.wandroidstudy.data.network.WanAndroidClient
import com.hc.wandroidstudy.data.network.errorMsg
import com.hc.wandroidstudy.module.home.model.HomeModel
import com.hc.wandroidstudy.module.home.presentation.model.BannerUIModel
import com.hc.wandroidstudy.module.home.presentation.model.CategoryItemUIModel
import com.hc.wandroidstudy.module.home.presentation.model.WxUIModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 *  包含整个界面的状态
 */
data class HomePageViewState(
        val pageStatus: PageStatus = PageStatus.Empty,
        val refreshStatus: RefreshStatus = RefreshStatus.RefreshIdle,
        val loadMoreStatus: LoadStatus = LoadStatus.LoadMoreIdle,
        val data: List<Any> = emptyList()
) : UIState

/**
 *  单次事件
 */
sealed class HomePageViewEffect : UIEffect {
    data class ShowToast(val message: String) : HomePageViewEffect()
    object ShowLoadingDialog : HomePageViewEffect()
    object DismissLoadingDialog : HomePageViewEffect()
}

/**
 *  用户意图事件
 */
sealed class HomePageViewEvent : UIEvent {
    object LoadData : HomePageViewEvent()
    object LoadDataMore : HomePageViewEvent()
    object RefreshData : HomePageViewEvent()
}

/**
 * @author ace
 * @createDate 2022/1/4
 * @explain
 * 作用:
 *  1.ViewModel 处理 表现层逻辑(视图逻辑) ,维护视图的 状态 和 行为
 *  2.完成 PO -> VO 的转换 (一般 PO 和 VO 差距不大,很多情况下都是复用 PO 的对象,但是他们的意义是不一样的)
 *
 * 注意:
 * MVI 将状态封装在了 State 中统一管理,和 MVVM 有所区别
 */
class HomePageViewModel(private val homeModel: HomeModel) :
        BaseViewModel<HomePageViewState, HomePageViewEvent, HomePageViewEffect>() {
    private var currentPage = 0


    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            //TODO 可以使用依赖注入
            val wanAndroidApi = WanAndroidClient()
            val repository = WanAndroidRepository(wanAndroidApi)
            val homeRepository = HomeModel(repository)
            return HomePageViewModel(homeRepository) as T
        }
    }

    override fun providerInitialState(): HomePageViewState = HomePageViewState()

    override fun handleEvent(event: HomePageViewEvent) {
        when (event) {
            is HomePageViewEvent.LoadData -> loadData()
            is HomePageViewEvent.LoadDataMore -> loadDataMore()
            is HomePageViewEvent.RefreshData -> refreshData()
        }
    }

    init {
        loadData()
    }

    /**
     * 加载更多
     */
    private fun loadDataMore() {
        viewModelScope.launch {
            val tempPage = currentPage + 1
            homeModel.loadDataMore(currentPage)
                    .onStart {
                        setState { copy(loadMoreStatus = LoadStatus.LoadMoreLoading) }
                    }.catch {
                        setEffect { HomePageViewEffect.ShowToast(it.errorMsg) }
                        setState { copy(loadMoreStatus = LoadStatus.LoadMoreFail(it)) }
                    }.collect {
                        currentPage = tempPage
                        setState {
                            copy(data = this.data + it.datas,
                                    loadMoreStatus = LoadStatus.LoadMoreSuccess(it.curPage < it.pageCount)
                            )
                        }
                    }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(homeModel.loadData(0)) { array ->
                val bannerData = array[0] as List<BannerData>
                val wxData = array[1] as List<WxData>
                val hotProjectData = array[2] as HotProjectData
                currentPage = 0
                setState {
                    copy(
                            pageStatus = PageStatus.Success,
                            data = convertPoToVo(bannerData, wxData, hotProjectData),
                            loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)
                    )
                }
            }.onStart {
                setState { copy(pageStatus = PageStatus.Loading) }
            }.catch {
                setEffect { HomePageViewEffect.ShowToast(it.errorMsg) }
                setState { copy(pageStatus = PageStatus.Error(it)) }
            }.collect()
        }
    }


    private fun refreshData() {
        viewModelScope.launch {
            combine(homeModel.loadData(0)) { array ->
                val bannerData = array[0] as List<BannerData>
                val wxData = array[1] as List<WxData>
                val hotProjectData = array[2] as HotProjectData
                currentPage = 0
                setState {
                    copy(
                            data = convertPoToVo(bannerData, wxData, hotProjectData),
                            refreshStatus = RefreshStatus.RefreshSuccess,
                            loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)
                    )
                }
            }.onStart {
                setState { copy(refreshStatus = RefreshStatus.RefreshLoading) }
            }.catch {
                setEffect { HomePageViewEffect.ShowToast(it.errorMsg) }
                setState {
                    copy(
                            refreshStatus = RefreshStatus.RefreshFail(it),
                            pageStatus = PageStatus.Error(it)
                    )
                }
            }.collect()
        }
    }


    private fun convertPoToVo(
            bannerData: List<BannerData>,
            wxData: List<WxData>,
            hotProjectData: HotProjectData
    ): List<Any> {
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
        return items
    }
}