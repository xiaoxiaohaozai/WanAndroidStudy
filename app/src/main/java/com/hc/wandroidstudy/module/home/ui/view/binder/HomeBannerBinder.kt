package com.hc.wandroidstudy.module.home.presentation.view.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import coil.load
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.hc.wandroidstudy.data.bean.BannerData
import com.hc.wandroidstudy.databinding.HomeLayoutBannerBinding
import com.hc.wandroidstudy.module.home.presentation.model.BannerUIModel
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator

/**
 * @author ace
 * @createDate 2021/12/27
 * @explain
 *
 *  首页Banner
 */
class HomeBannerBinder : QuickViewBindingItemBinder<BannerUIModel, HomeLayoutBannerBinding>() {
    override fun convert(holder: BinderVBHolder<HomeLayoutBannerBinding>, data: BannerUIModel) {
        holder.viewBinding.banner.adapter.setDatas(data.items)
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): HomeLayoutBannerBinding {
        val binding = HomeLayoutBannerBinding.inflate(layoutInflater, parent, false)
        binding.banner.setBannerRound(SizeUtils.dp2px(6f).toFloat())
        binding.banner.setAdapter(object : BannerImageAdapter<BannerData>(emptyList()) {
            override fun onBindView(holder: BannerImageHolder, data: BannerData, position: Int, size: Int) {
                holder.imageView.load(data.imagePath)
            }
        }).indicator = CircleIndicator(context)
        return binding
    }
}