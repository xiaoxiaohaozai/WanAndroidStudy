package com.hc.wandroidstudy.module.home.presentation.data


import com.airbnb.mvrx.MavericksState
import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.data.WxData

/**
 * @author ace
 * @createDate 2021/12/21
 * @explain
 *  Home 页包含的所有ui数据
 */
data class HomeState(
        val items: List<Any> = emptyList(),
        val hasMore: Boolean = false,
        val isLoading: Boolean = true,
        val error: Throwable? = null
) : MavericksState