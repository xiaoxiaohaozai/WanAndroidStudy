package com.hc.wandroidstudy.module.home.presentation.data


import com.airbnb.mvrx.MavericksState
import com.hc.wandroidstudy.common.data.PageState


/**
 * @author ace
 * @createDate 2021/12/21
 * @explain
 *  Home 页包含的所有ui数据
 */
data class HomeState(
        val pageState: PageState = PageState.Empty,
        val isRefreshing: Boolean = true,
        val data: List<Any> = emptyList(), //数据
        val error: Throwable? = null,//错误
) : MavericksState


