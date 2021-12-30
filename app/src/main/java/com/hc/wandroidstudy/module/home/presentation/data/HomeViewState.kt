package com.hc.wandroidstudy.module.home.presentation.data



import com.hc.wandroidstudy.common.base.LoadStatus
import com.hc.wandroidstudy.common.base.PageStatus
import com.hc.wandroidstudy.common.base.RefreshStatus


data class HomeViewState(val pageStatus: PageStatus = PageStatus.Empty,
                         val refreshStatus: RefreshStatus = RefreshStatus.RefreshInit,
                         val loadMoreStatus:LoadStatus = LoadStatus.LoadMoreInit,
                         val data: List<Any> = emptyList())

sealed class HomeViewEvent {
    data class ShowToast(val message: String) : HomeViewEvent()
    object ShowLoadingDialog : HomeViewEvent()
    object DismissLoadingDialog : HomeViewEvent()
}

sealed class HomeViewAction {
    object LoadData:HomeViewAction()
    object LoadDataMore:HomeViewAction()
    object RefreshData:HomeViewAction()
}



