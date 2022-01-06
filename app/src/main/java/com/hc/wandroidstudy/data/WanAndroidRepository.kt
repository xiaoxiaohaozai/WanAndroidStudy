package com.hc.wandroidstudy.data

import com.hc.wandroidstudy.data.network.WanAndroidApi
import com.hc.wandroidstudy.data.network.WanAndroidClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author ace
 * @createDate 2022/1/4
 * @explain
 * 由 WanAndroid 开放 API 提供的数据
 * 作用:屏蔽数据来源,对外统一提供数据 (网络,数据库,SharedPreferences,数据缓存等)
 */
@Singleton
class WanAndroidRepository @Inject constructor(private val wanAndroidClient: WanAndroidClient) : WanAndroidApi by wanAndroidClient, IWanAndroidRepository