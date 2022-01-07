package com.hc.wandroidstudy.di

import com.hc.wandroidstudy.data.network.WanAndroidClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent


/**
 * @author ace
 * @createDate 2022/1/7
 * @explain
 *  注入网络模块
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providerWanAndroidApi(): WanAndroidClient {
        return WanAndroidClient()
    }
}