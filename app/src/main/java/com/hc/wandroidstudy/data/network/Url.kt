package com.hc.wandroidstudy.common.data.network

import rxhttp.wrapper.annotation.DefaultDomain


object Url {

    @JvmField
    @DefaultDomain //通过该注解设置默认域名
    var BASE_URL = "https://www.wanandroid.com"

    //首页Banner
    const val BANNER = "/banner/json"

    //顶置文章
    const val ARTICLE_TOP = "/article/top/json"

    //公众号
    const val WX_ARTICLE = "/wxarticle/chapters/json"

}