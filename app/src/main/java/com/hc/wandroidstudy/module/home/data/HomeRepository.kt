package com.hc.wandroidstudy.module.home.data

import com.hc.wandroidstudy.common.network.WanAndroidApi
import com.hc.wandroidstudy.common.network.WanAndroidClient
import com.hc.wandroidstudy.module.home.domain.IHomeRepository



/**
 * @author ace
 * @createDate 2021/12/21
 * @explain
 *  首页数据仓库实现,通过委托简化模板代码
 */
class HomeRepository(private val wanAndroidClient: WanAndroidClient) : WanAndroidApi by wanAndroidClient, IHomeRepository