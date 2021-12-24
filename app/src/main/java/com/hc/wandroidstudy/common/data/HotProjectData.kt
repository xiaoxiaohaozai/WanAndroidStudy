package com.hc.wandroidstudy.common.data


data class HotProjectData(
        val datas: List<HotProjectItemData>,
        val curPage :Int,
        val offset: Int,
        val over: Boolean,
        val pageCount: Int,
        val size: Int,
        val total: Int
)