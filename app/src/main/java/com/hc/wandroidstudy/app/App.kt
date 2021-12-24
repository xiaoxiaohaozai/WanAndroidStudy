package com.hc.wandroidstudy.app

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.blankj.utilcode.util.Utils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins


class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Mavericks.initialize(this)
        RxHttpPlugins.init(RxHttpPlugins.getOkHttpClient()).setDebug(true)

        //brv 全局配置刷新
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(this) }
    }
}