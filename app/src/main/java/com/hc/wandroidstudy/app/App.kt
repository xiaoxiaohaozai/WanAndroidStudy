package com.hc.wandroidstudy.app

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import com.drake.statelayout.StateConfig
import com.hc.wandroidstudy.R

import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cahce.CacheMode
import java.io.File


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Mavericks.initialize(this)
        LogUtils.getConfig().apply {
            setBorderSwitch(false)
            setLogHeadSwitch(false)
        }


        //设置网络
        val cacheDir = File(externalCacheDir, "RxHttpCache")
        RxHttpPlugins.init(RxHttpPlugins.getOkHttpClient())
                //设置最大缓存为10M，缓存有效时长为60秒
                .setCache(cacheDir, 10 * 1024 * 1024, CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK, 60 * 1000)
                .setDebug(true)

        StateConfig.apply {
            errorLayout = R.layout.layout_error
            emptyLayout = R.layout.layout_empty
            loadingLayout = R.layout.layout_loading
        }
    }


}