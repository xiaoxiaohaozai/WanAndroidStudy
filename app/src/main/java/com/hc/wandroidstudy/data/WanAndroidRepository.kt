package com.hc.wandroidstudy.data

import com.hc.wandroidstudy.data.network.WanAndroidApi
import com.hc.wandroidstudy.data.network.WanAndroidClient

/**
 * @author ace
 * @createDate 2022/1/4
 * @explain
 * 由 WanAndroid 开放 API 提供的数据
 */
class WanAndroidRepository(private val wanAndroidClient: WanAndroidClient) : WanAndroidApi by wanAndroidClient, IWanAndroidRepository