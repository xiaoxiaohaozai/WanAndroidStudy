package com.hc.wandroidstudy.common.network

import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectData
import com.hc.wandroidstudy.common.data.WxData
import kotlinx.coroutines.flow.Flow

import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toFlowResponse

/**
 * @author ace
 * @createDate 2021/12/21
 * @explain
 *
 * 网络数据中间层实现 @link [WanAndroidApi]
 */
class WanAndroidClient : WanAndroidApi {

    override fun getBanner(): Flow<List<BannerData>> {
        return RxHttp.get(Url.BANNER).toFlowResponse()
    }

    override fun getWx(): Flow<List<WxData>> {
        return RxHttp.get(Url.WX_ARTICLE).toFlowResponse()
    }

    override fun getHotProjectList(page: Int): Flow<HotProjectData> {
        return RxHttp.get("/article/listproject/${page}/json").toFlowResponse()
    }
}