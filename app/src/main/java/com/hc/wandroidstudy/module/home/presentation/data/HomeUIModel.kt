package com.hc.wandroidstudy.module.home.presentation.data

import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.WxData


data class BannerUIModel(val items: List<BannerData>)

/**公众号*/
data class WxUIModel(val items: List<WxData>)

/**栏目分类模型*/
data class CategoryItemUIModel(val title: String, val description: String, val image: Int)

 class Footer()
