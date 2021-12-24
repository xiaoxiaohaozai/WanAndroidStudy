package com.hc.wandroidstudy.common.network

import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectData
import com.hc.wandroidstudy.common.data.WxData
import kotlinx.coroutines.flow.Flow


/**
 * @author ace
 * @createDate 2021/12/21
 * @explain
 * 网络数据中间层
 * 从数据实际需求出发对 api 进行过滤,加工,合并等操作
 */
interface WanAndroidApi {

    fun getBanner(): Flow<List<BannerData>>

    fun getWx(): Flow<List<WxData>>

    fun getHotProjectList(page: Int): Flow<HotProjectData>

}