package com.hc.wandroidstudy.module.home.presentation.vm

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
import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectData
import com.hc.wandroidstudy.common.data.WxData
import com.hc.wandroidstudy.common.network.WanAndroidClient
import com.hc.wandroidstudy.common.network.errorMsg
import com.hc.wandroidstudy.module.home.data.HomeRepository
import com.hc.wandroidstudy.module.home.domain.IHomeRepository
import com.hc.wandroidstudy.module.home.presentation.data.BannerUIModel
import com.hc.wandroidstudy.module.home.presentation.data.CategoryItemUIModel
import com.hc.wandroidstudy.module.home.presentation.data.WxUIModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  包含整个界面的状态
 */
data class HomePageViewState(val pageStatus: PageStatus = PageStatus.Empty,
                         val refreshStatus: RefreshStatus = RefreshStatus.RefreshInit,
                         val loadMoreStatus: LoadStatus = LoadStatus.LoadMoreInit,
                         val data: List<Any> = emptyList()) : UIState

/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  单次事件
 */
sealed class HomePageViewEffect : UIEffect {
    data class ShowToast(val message: String) : HomePageViewEffect()
    object ShowLoadingDialog : HomePageViewEffect()
    object DismissLoadingDialog : HomePageViewEffect()
}

/**
 * @author ace
 * @createDate 2021/12/31
 * @explain
 *  用户意图事件
 */
sealed class HomePageViewEvent : UIEvent {
    object LoadData : HomePageViewEvent()
    object LoadDataMore : HomePageViewEvent()
    object RefreshData : HomePageViewEvent()
}

class HomePageViewModel(private val repository: IHomeRepository) : BaseViewModel<HomePageViewState, HomePageViewEvent, HomePageViewEffect>() {
    private var currentPage = 0


    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            //TODO 可以使用依赖注入
            val wanAndroidApi = WanAndroidClient()
            val homeRepository = HomeRepository(wanAndroidApi)
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
            repository.getHotProjectList(currentPage).onStart {
                setState { copy(loadMoreStatus = LoadStatus.LoadMoreLoading) }
            }.catch {
                setEffect { HomePageViewEffect.ShowToast(it.errorMsg) }
                setState { copy(loadMoreStatus = LoadStatus.LoadMoreFail(it)) }
            }.collect {
                currentPage = tempPage
                setState { copy(data = this.data + it.datas, loadMoreStatus = LoadStatus.LoadMoreSuccess(it.curPage < 5)) }
            }
        }
    }

    /**
     * 第一次加载
     */
    private fun loadData() {
        viewModelScope.launch {
            currentPage = 0
            combine(repository.getBanner(), repository.getWx(), repository.getHotProjectList(currentPage)) { array ->
                val bannerData = array[0] as List<BannerData>
                val wxData = array[1] as List<WxData>
                val hotProjectData = array[2] as HotProjectData
                delay(2000)
                setState { copy(pageStatus = PageStatus.Success, data = toUI(bannerData, wxData, hotProjectData), loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)) }
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
            currentPage = 0
            combine(repository.getBanner(), repository.getWx(), repository.getHotProjectList(currentPage)) { array ->
                val bannerData = array[0] as List<BannerData>
                val wxData = array[1] as List<WxData>
                val hotProjectData = array[2] as HotProjectData
                setState { copy(data = toUI(bannerData, wxData, hotProjectData), refreshStatus = RefreshStatus.RefreshSuccess, loadMoreStatus = LoadStatus.LoadMoreSuccess(hotProjectData.curPage < hotProjectData.pageCount)) }
            }.onStart {
                setState { copy(refreshStatus = RefreshStatus.RefreshLoading) }
            }.catch {
                setEffect { HomePageViewEffect.ShowToast(it.errorMsg) }
                setState { copy(refreshStatus = RefreshStatus.RefreshFail(it), pageStatus = PageStatus.Error(it)) }
            }.collect()
        }
    }


    private fun toUI(bannerData: List<BannerData>, wxData: List<WxData>, hotProjectData: HotProjectData): List<Any> {
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