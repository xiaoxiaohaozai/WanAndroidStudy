package com.hc.wandroidstudy.common.base


/**
 * 缺省页状态
 */
sealed class PageStatus{
    object Empty : PageStatus()
    object Loading : PageStatus()
    object Success : PageStatus()
    data class Error(val error: Throwable) : PageStatus()
}



/**
 * 列表加载更多状态
 */
sealed class LoadStatus{
    object LoadMoreInit : LoadStatus()
    object LoadMoreLoading : LoadStatus()
    data class LoadMoreSuccess(val hasMore: Boolean) : LoadStatus()
    data class LoadMoreFail(val error: Throwable) : LoadStatus()
}



/**
 * 列表刷新状态
 */
sealed class RefreshStatus{
    object RefreshInit : RefreshStatus()
    object RefreshLoading : RefreshStatus()
    object  RefreshSuccess : RefreshStatus()
    data class RefreshFail(val error: Throwable) : RefreshStatus()
}





