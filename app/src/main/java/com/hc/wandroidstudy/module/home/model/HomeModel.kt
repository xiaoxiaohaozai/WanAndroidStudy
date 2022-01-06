package com.hc.wandroidstudy.module.home.model


import com.hc.wandroidstudy.data.WanAndroidRepository
import com.hc.wandroidstudy.data.bean.HotProjectData

import kotlinx.coroutines.flow.*
import javax.inject.Inject


/**
 * @author ace
 * @createDate 2022/1/4
 * @explain
 *
 *  作用:
 *  处理首页业务逻辑,首页数据的整合和获取
 */
class HomeModel @Inject constructor(private val repository: WanAndroidRepository) {

    fun loadData(page: Int = 1): List<Flow<Any>> {
        return arrayListOf(repository.getBanner(), repository.getWx(), repository.getHotProjectList(page))
    }

    fun loadDataMore(page: Int): Flow<HotProjectData> {
        return repository.getHotProjectList(page)
    }
}